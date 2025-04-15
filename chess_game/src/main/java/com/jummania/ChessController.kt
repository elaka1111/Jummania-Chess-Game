package com.jummania

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jummania.ChessView.Companion.isWhiteTurn


/**
 * Created by Jummania on 28/3/25.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */


internal class ChessController(
    private val context: Context,
    val isLightFilled: Boolean,
    val isDarkFilled: Boolean,
    val pieceLightColor: Int,
    val pieceDarkColor: Int
) {

    private val filledSymbols = arrayOf("♜", "♞", "♝", "♛", "♚", "♝", "♞", "♜")
    private val unfilledSymbols = arrayOf("♖", "♘", "♗", "♕", "♔", "♗", "♘", "♖")

    private val lightPieces = if (isLightFilled) {
        createPieces(filledSymbols, pieceLightColor)
    } else {
        createPieces(unfilledSymbols, pieceLightColor)
    }
    private val darkPieces = if (isDarkFilled) {
        createPieces(filledSymbols, pieceDarkColor)
    } else {
        createPieces(unfilledSymbols, pieceDarkColor)
    }

    private val chessBoard = arrayOfNulls<Piece>(64)

    private val indices = chessBoard.indices

    init {
        for (i in lightPieces.indices) chessBoard[i] = lightPieces[i]
        val lightPawnSymbol = if (isLightFilled) "♟" else "♙"
        for (i in 8 until 16) chessBoard[i] = Piece(lightPawnSymbol, pieceLightColor)
        val darkPawnSymbol = if (isDarkFilled) "♟" else "♙"
        for (i in 48 until 56) chessBoard[i] = Piece(darkPawnSymbol, pieceDarkColor)
        for (i in darkPieces.indices) chessBoard[56 + i] = darkPieces[i]
    }

    fun get(position: Int): Piece? {
        if (position in indices) return chessBoard[position]
        return null
    }

    private fun movePiece(fromIndex: Int, toIndex: Int, piece: Piece?) {
        chessBoard[toIndex] = piece
        chessBoard[fromIndex] = null
    }

    fun promotePawn(position: Int, symbol: String) {
        val piece = get(position)
        if (piece != null) {
            piece.symbol = symbol
            chessBoard[position] = piece
        }
    }

    fun isLightPiece(piece: Piece?): Boolean {
        return piece?.color == pieceLightColor
    }

    fun isLightPawn(piece: Piece?): Boolean {
        return piece != null && piece.isPawn() && piece.color == pieceLightColor
    }

    fun isBlackPawn(piece: Piece?): Boolean {
        return piece != null && piece.isPawn() && piece.color == pieceDarkColor
    }

    private fun isFriend(piece: Piece?, isWhitePiece: Boolean): Boolean {
        return piece != null && isLightPiece(piece) == isWhitePiece
    }

    private fun isValidMove(pos: Int, isWhitePiece: Boolean): Boolean {
        val piece = get(pos)
        return piece == null || !isFriend(piece, isWhitePiece)
    }

    fun getPromotedSymbols(isWhiteTurn: Boolean): Array<String> {
        val filled = if (isWhiteTurn) isLightFilled else isDarkFilled
        return if (filled) arrayOf("♛", "♜", "♝", "♞") // Filled (dark style)
        else arrayOf("♕", "♖", "♗", "♘") // Unfilled (light style)
    }

    fun swapTo(fromIndex: Int, toIndex: Int) {
        if (fromIndex !in indices || toIndex !in indices) return

        val fromPiece = get(fromIndex) ?: return

        val isFromWhitePiece = isLightPiece(fromPiece)

        // Check for turn validity
        if ((isFromWhitePiece && !isWhiteTurn) || (!isFromWhitePiece && isWhiteTurn)) {
            message("It's not your turn!")
            return
        }

        val toPiece = get(toIndex)

        // Check if the destination square contains a piece of the same color
        if (isFriend(toPiece, isFromWhitePiece)) {
            message("${if (isFromWhitePiece) "White" else "Black"} cannot move to its own piece.")
            return
        }

        // Piece-specific movement rules
        when {
            fromPiece.isPawn() -> {
                if (!isPawnMoveAllowed(fromIndex, toIndex, isFromWhitePiece)) {
                    message("The Pawn can only move one square forward.")
                    return
                }
            }

            fromPiece.isKnight() -> {
                if (!isKnightMoveAllowed(fromIndex, toIndex, isFromWhitePiece)) {
                    message("The Knight can only move in an L shape.")
                    return
                }
            }

            fromPiece.isBishop() -> {
                if (!isBishopMoveAllowed(fromIndex, toIndex, isFromWhitePiece)) {
                    message("The Bishop can only move diagonally.")
                    return
                }
            }

            fromPiece.isRook() -> {
                if (!isRookMoveAllowed(fromIndex, toIndex, isFromWhitePiece)) {
                    message("The Rook can only move horizontally or vertically.")
                    return
                }
            }

            fromPiece.isQueen() -> {
                if (!isQueenMoveAllowed(fromIndex, toIndex, isFromWhitePiece)) {
                    message("The Queen can move horizontally, vertically, or diagonally.")
                    return
                }
            }

            fromPiece.isKing() -> {
                if (!isKingMoveAllowed(fromIndex, toIndex, isFromWhitePiece)) {
                    message("The King can only move one square in any direction.")
                    return
                }
            }
        }

        if (isCheck(isFromWhitePiece)) {
            // Move the piece
            movePiece(fromIndex, toIndex, fromPiece)

            if (isCheck(isFromWhitePiece)) {
                chessBoard[toIndex] = toPiece
                chessBoard[fromIndex] = fromPiece
                message("You cannot move because your 'King' is in check.")
                return
            }
        }

        // Move the piece
        movePiece(fromIndex, toIndex, fromPiece)

        // Handle capture or king checkmate
        if (toPiece != null) {
            if (toPiece.isKing()) {
                showEndDialogue()
            } else {
                message("${fromPiece.symbol} attacks and captures ${toPiece.symbol}")
            }
        }

        // Change the turn
        isWhiteTurn = !isFromWhitePiece
    }

    private fun isCheck(isWhitePiece: Boolean): Boolean {
        val kingPosition =
            chessBoard.indexOfFirst { it?.isKing() == true && it.color == if (isWhitePiece) pieceLightColor else pieceDarkColor }
        if (kingPosition == -1) {
            showEndDialogue()
            return false
        }

        for (position in chessBoard.indices) {
            val piece = chessBoard[position]
            if (piece == null || isFriend(piece, isWhitePiece)) continue
            when {
                piece.isPawn() -> {
                    if (isPawnMoveAllowed(position, kingPosition, !isWhitePiece)) {
                        return true
                    }
                }

                piece.isKnight() -> {
                    if (isKnightMoveAllowed(position, kingPosition, !isWhitePiece)) {
                        return true
                    }
                }

                piece.isBishop() -> {
                    if (isBishopMoveAllowed(position, kingPosition, !isWhitePiece)) {
                        return true
                    }
                }

                piece.isRook() -> {
                    if (isRookMoveAllowed(position, kingPosition, !isWhitePiece)) {
                        return true
                    }
                }

                piece.isQueen() -> {
                    if (isQueenMoveAllowed(position, kingPosition, !isWhitePiece)) {
                        return true
                    }
                }

                piece.isKing() -> {
                    if (isKingMoveAllowed(position, kingPosition, !isWhitePiece)) {
                        return true
                    }
                }
            }
        }
        return false
    }


    private fun message(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun isMoveAllowed(
        from: Int,
        to: Int,
        isWhitePiece: Boolean,
        sequence: Int,
        horizontal: Boolean,
        vertical: Boolean,
        diagonal: Boolean
    ): Boolean {

        if (from == to) return false

        var left = horizontal
        var right = horizontal
        var up = vertical
        var down = vertical
        var upLeft = diagonal
        var upRight = diagonal
        var downLeft = diagonal
        var downRight = diagonal

        val leftLimit = from - (from % 8)
        val rightLimit = leftLimit + 8

        for (i in 1 until sequence) {
            if (left) {
                val pos = from - i
                if (pos >= leftLimit) {
                    if (pos == to) return isValidMove(pos, isWhitePiece)
                    left = get(pos) == null
                } else left = false
            }

            if (right) {
                val pos = from + i
                if (pos < rightLimit) {
                    if (pos == to) return isValidMove(pos, isWhitePiece)
                    right = get(pos) == null
                } else right = false
            }

            if (up) {
                val pos = from + 8 * i
                if (pos == to) return isValidMove(pos, isWhitePiece)
                up = get(pos) == null
            }

            if (down) {
                val pos = from - 8 * i
                if (pos == to) return isValidMove(pos, isWhitePiece)
                down = get(pos) == null
            }

            if (upLeft) {
                val pos = from + 7 * i
                if (pos == to) return isValidMove(pos, isWhitePiece)
                upLeft = get(pos) == null
                if (pos % 8 == 0) upLeft = false
            }

            if (upRight) {
                val pos = from + 9 * i
                if (pos == to) return isValidMove(pos, isWhitePiece)
                upRight = get(pos) == null
                if (pos % 8 == 7) upRight = false
            }

            if (downLeft) {
                val pos = from - 9 * i
                if (pos == to) return isValidMove(pos, isWhitePiece)
                downLeft = get(pos) == null
                if (pos % 8 == 0) downLeft = false
            }

            if (downRight) {
                val pos = from - 7 * i
                if (pos == to) return isValidMove(pos, isWhitePiece)
                downRight = get(pos) == null
                if (pos % 8 == 7) downRight = false
            }
        }

        return false
    }

    // KING
    private fun isKingMoveAllowed(from: Int, to: Int, isWhitePiece: Boolean): Boolean {
        return isMoveAllowed(
            from, to, isWhitePiece, 2, horizontal = true, vertical = true, diagonal = true
        )
    }

    // QUEEN
    private fun isQueenMoveAllowed(from: Int, to: Int, isWhitePiece: Boolean): Boolean {
        return isMoveAllowed(
            from, to, isWhitePiece, 8, horizontal = true, vertical = true, diagonal = true
        )
    }

    // ROOK
    private fun isRookMoveAllowed(from: Int, to: Int, isWhitePiece: Boolean): Boolean {
        return isMoveAllowed(
            from, to, isWhitePiece, 8, horizontal = true, vertical = true, diagonal = false
        )
    }

    // BISHOP
    private fun isBishopMoveAllowed(from: Int, to: Int, isWhitePiece: Boolean): Boolean {
        return isMoveAllowed(
            from, to, isWhitePiece, 8, horizontal = false, vertical = false, diagonal = true
        )
    }


    // KNIGHT
    private fun isKnightMoveAllowed(
        from: Int, to: Int, isWhitePiece: Boolean
    ): Boolean {

        if (from == to) return false

        val sequence = intArrayOf(
            from + 6, from - 6, from + 10, from - 10, from + 15, from - 15, from + 17, from - 17
        )

        if (to in sequence) {
            return isValidMove(to, isWhitePiece)
        }

        return false
    }


    // PAWN
    private fun isPawnMoveAllowed(from: Int, to: Int, isWhitePiece: Boolean): Boolean {
        if (from == to) return false

        val forwardStep = if (isWhitePiece) 8 else -8
        val doubleForwardStep = if (isWhitePiece) 16 else -16
        val captureLeft = if (isWhitePiece) 7 else -7
        val captureRight = if (isWhitePiece) 9 else -9

        // Check the capture positions (diagonal moves)
        if (to == from + captureLeft || to == from + captureRight) {
            val piece = get(to)
            return piece != null && !isFriend(piece, isWhitePiece)
        }

        // Check the single square forward move
        if (to == from + forwardStep) {
            return get(to) == null
        }

        // Check the double square forward move from the starting positions (2nd and 7th ranks)
        if ((from in 8..16 || from in 48..56) && to == from + doubleForwardStep) {
            return get(to) == null
        }

        return false
    }


    fun transform(symbol: String): String {
        return when (symbol) {
            "♙" -> "♟"
            "♖" -> "♜"
            "♘" -> "♞"
            "♗" -> "♝"
            "♕" -> "♛"
            "♔" -> "♚"

            "♟" -> "♙"
            "♜" -> "♖"
            "♞" -> "♘"
            "♝" -> "♗"
            "♛" -> "♕"
            "♚" -> "♔"

            else -> symbol
        }
    }

    private fun createPieces(symbols: Array<String>, color: Int): Array<Piece> {
        return Array(symbols.size) { i -> Piece(symbols[i], color) }
    }

    private fun showEndDialogue() {
        MaterialAlertDialogBuilder(context).setTitle("Game Over")
            .setPositiveButton("Restart") { _, _ -> (context as Activity).recreate() }
            .setCancelable(false).show()
    }

}

