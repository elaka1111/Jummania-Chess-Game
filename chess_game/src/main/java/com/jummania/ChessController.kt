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

    init {
        for (i in lightPieces.indices) chessBoard[i] = lightPieces[i]
        val lightPawnSymbol = if (isLightFilled) "♟" else "♙"
        for (i in 8 until 16) chessBoard[i] = Piece(lightPawnSymbol, pieceLightColor)
        val darkPawnSymbol = if (isDarkFilled) "♟" else "♙"
        for (i in 48 until 56) chessBoard[i] = Piece(darkPawnSymbol, pieceDarkColor)
        for (i in darkPieces.indices) chessBoard[56 + i] = darkPieces[i]
    }

    fun get(position: Int): Piece? {
        if (position in indices()) return chessBoard[position]
        return null
    }

    fun set(fromIndex: Int, toIndex: Int, piece: Piece?) {
        chessBoard[toIndex] = piece
        chessBoard[fromIndex] = null
    }

    fun set(position: Int, symbol: String) {
        get(position)?.symbol = symbol
    }

    private fun indices(): IntRange {
        return chessBoard.indices
    }

    fun isLightPiece(piece: Piece?): Boolean {
        return piece?.color == pieceLightColor
    }

    private fun isDarkPiece(piece: Piece?): Boolean {
        return piece?.color == pieceDarkColor
    }

    fun isWhitePawn(piece: Piece?): Boolean {
        if (piece == null) return false
        return piece.isPawn() && piece.color == pieceLightColor
    }

    fun isBlackPawn(piece: Piece?): Boolean {
        if (piece == null) return false
        return piece.isPawn() && piece.color == pieceDarkColor
    }

    private fun isEnemy(piece: Piece?, isWhitePiece: Boolean): Boolean {
        if (piece == null) return false
        return (isWhitePiece && isDarkPiece(piece)) || (!isWhitePiece && !isDarkPiece(piece))
    }

    fun getPromotedSymbols(isWhiteTurn: Boolean): Array<String> {
        val filled = if (isWhiteTurn) isLightFilled else isDarkFilled
        return if (filled) arrayOf("♛", "♜", "♝", "♞") // Filled (dark style)
        else arrayOf("♕", "♖", "♗", "♘") // Unfilled (light style)
    }

    fun swapTo(fromIndex: Int, toIndex: Int) {
        if (fromIndex !in indices() || toIndex !in indices()) return

        val fromPiece = get(fromIndex) ?: return

        val isFromWhitePiece = isLightPiece(fromPiece)

        // Check for turn validity
        if ((isFromWhitePiece && !isWhiteTurn) || (!isFromWhitePiece && isWhiteTurn)) {
            message("It's not your turn!")
            return
        }

        val isCheck = isCheck(isWhiteTurn)

        val toPiece = get(toIndex)

        // Function to get valid move sequence for the piece
        fun getMoveSequence(
            sequence: Int, horizontal: Boolean, vertical: Boolean, diagonal: Boolean
        ): List<Int> {
            return getSequence(
                fromIndex, sequence, isFromWhitePiece, horizontal, vertical, diagonal
            )
        }

        // Piece-specific movement rules
        when {
            fromPiece.isKing() -> {
                val sequence =
                    getMoveSequence(2, horizontal = true, vertical = true, diagonal = false)
                if (toIndex !in sequence) {
                    message("The King can only move one square in any direction.")
                    return
                }
            }

            fromPiece.isRook() -> {
                val sequence =
                    getMoveSequence(8, horizontal = true, vertical = true, diagonal = false)
                if (toIndex !in sequence) {
                    message("The Rook can only move horizontally or vertically.")
                    return
                }
            }

            fromPiece.isBishop() -> {
                val sequence =
                    getMoveSequence(8, horizontal = false, vertical = false, diagonal = true)
                if (toIndex !in sequence) {
                    message("The Bishop can only move diagonally.")
                    return
                }
            }

            fromPiece.isQueen() -> {
                val sequence =
                    getMoveSequence(8, horizontal = true, vertical = true, diagonal = true)
                if (toIndex !in sequence) {
                    message("The Queen can move horizontally, vertically, or diagonally.")
                    return
                }
            }

            fromPiece.isKnight() -> {
                val sequence = getKnightSequence(fromIndex, isFromWhitePiece)
                if (toIndex !in sequence) {
                    message("The Knight can only move in an L shape.")
                    return
                }
            }

            fromPiece.isPawn() -> {
                val sequence = getPawnSequence(fromIndex, isFromWhitePiece)
                if (toIndex !in sequence) {
                    message("The Pawn can only move one square forward.")
                    return
                }
            }
        }

        // Check if the destination square contains a piece of the same color

        val isToWhitePiece = isLightPiece(toPiece)
        val isFromBlackPiece = isDarkPiece(fromPiece)
        val isToBlackPiece = isDarkPiece(toPiece)

        if ((isFromWhitePiece && isToWhitePiece) || (isFromBlackPiece && isToBlackPiece)) {
            message("${if (isFromWhitePiece) "White" else "Black"} cannot move to its own piece.")
            return
        }

        // Move the piece
        set(fromIndex, toIndex, fromPiece)

        // Handle capture or king checkmate
        if (toPiece != null) {
            if (toPiece.isKing()) {
                MaterialAlertDialogBuilder(context).setTitle("Game Over")
                    .setPositiveButton("Restart") { _, _ -> (context as Activity).recreate() }
                    .setCancelable(false).show()
            } else {
                message("${fromPiece.symbol} attacks and captures ${toPiece.symbol}")
            }
        }

        // Change the turn
        isWhiteTurn = !isFromWhitePiece
    }

    private fun isCheck(whiteTurn: Boolean): Boolean {

        /*
        val kingPosition =
            chessBoard.indexOfFirst { it?.isKing() == true && it.color == if (whiteTurn) pieceLightColor else pieceDarkColor }
        if (kingPosition == -1) return false

         */

        return false
    }


    private fun message(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun getSequence(
        position: Int,
        sequence: Int,
        isWhitePiece: Boolean,
        horizontal: Boolean,
        vertical: Boolean,
        diagonal: Boolean
    ): List<Int> {
        val list = mutableListOf<Int>()

        var left = horizontal
        var right = horizontal
        var up = vertical
        var down = vertical
        var upLeft = diagonal
        var upRight = diagonal
        var downLeft = diagonal
        var downRight = diagonal

        val leftLimit = position - (position % 8)
        val rightLimit = leftLimit + 8

        fun add(pos: Int): Boolean {
            return add(list, pos, isWhitePiece)
        }

        for (i in 1 until sequence) {
            // Check left
            if (left) {
                val pos = position - 1 * i
                left = if (pos >= leftLimit) {
                    add(pos)
                } else {
                    false
                }
            }

            // Check right
            if (right) {
                val pos = position + 1 * i
                right = if (pos < rightLimit) add(pos)
                else false
            }

            // Check up
            if (up) up = add(position + 8 * i)

            // Check down
            if (down) down = add(position - 8 * i)

            // Check up-left diagonal
            if (upLeft) upLeft = add(position + 7 * i)

            // Check up-right diagonal
            if (upRight) upRight = add(position + 9 * i)

            // Check down-left diagonal
            if (downLeft) downLeft = add(position - 9 * i)

            // Check down-right diagonal
            if (downRight) downRight = add(position - 7 * i)
        }

        return list
    }


    private fun add(list: MutableList<Int>, pos: Int, isWhitePiece: Boolean): Boolean {
        if (pos !in indices()) return false // Check if the position is within valid board indices
        val piece = get(pos) // Get the piece at the position
        return if (piece == null) {
            list.add(pos) // If the position is empty, add it to the list
        } else if (isEnemy(piece, isWhitePiece)) {
            list.add(pos) // If it's an enemy piece, add it to the list (for capture)
        } else {
            false // If it's a friendly piece, return false (cannot move here)
        }
    }


    private fun getKnightSequence(position: Int, isWhitePiece: Boolean): List<Int> {
        val list = mutableListOf<Int>()

        // Helper function to add valid positions
        fun add(pos: Int) {
            add(list, pos, isWhitePiece)  // Calls the existing add function to check validity
        }

        // All possible knight moves (8 positions)
        add(position + 6)   // Right and down
        add(position - 6)   // Left and down
        add(position + 10)  // Right and up
        add(position - 10)  // Left and up
        add(position + 15)  // Right and up-right
        add(position - 15)  // Left and down-left
        add(position + 17)  // Right and up-left
        add(position - 17)  // Left and down-right

        return list
    }


    private fun getPawnSequence(position: Int, isWhitePiece: Boolean): List<Int> {
        val list = mutableListOf<Int>()

        // Helper function to add valid positions
        fun add(pos: Int) {
            add(list, pos, isWhitePiece)  // Calls the existing add function to check validity
        }

        // Capture moves (diagonal movement)
        var animePosition = if (isWhitePiece) position + 7 else position - 7
        if (animePosition in indices() && isEnemy(get(animePosition), isWhitePiece)) {
            add(animePosition)
        }

        animePosition = if (isWhitePiece) position + 9 else position - 9
        if (animePosition in indices() && isEnemy(get(animePosition), isWhitePiece)) {
            add(animePosition)
        }

        // Single square forward move
        animePosition = if (isWhitePiece) position + 8 else position - 8
        if (animePosition in indices() && !isEnemy(get(animePosition), isWhitePiece)) {
            add(animePosition)
        }

        // Double square forward move from starting positions (2nd and 7th ranks)
        if (position in 8..16 || position in 48..56) {
            animePosition = if (isWhitePiece) position + 16 else position - 16
            if (animePosition in indices() && !isEnemy(get(animePosition), isWhitePiece)) {
                add(animePosition)
            }
        }

        return list
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

}

