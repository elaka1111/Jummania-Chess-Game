package com.jummania

import android.content.Context
import android.util.Log
import android.widget.Toast
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

    private val lightFilledPieces by lazy {
        arrayOf(
            Piece("♜", pieceLightColor), // Rook
            Piece("♞", pieceLightColor), // Knight
            Piece("♝", pieceLightColor), // Bishop
            Piece("♛", pieceLightColor), // Queen
            Piece("♚", pieceLightColor), // King
            Piece("♝", pieceLightColor), // Bishop
            Piece("♞", pieceLightColor), // Knight
            Piece("♜", pieceLightColor)  // Rook
        )
    }

    private val darkFilledPieces by lazy {
        arrayOf(
            Piece("♜", pieceDarkColor), // Rook
            Piece("♞", pieceDarkColor), // Knight
            Piece("♝", pieceDarkColor), // Bishop
            Piece("♛", pieceDarkColor), // Queen
            Piece("♚", pieceDarkColor), // King
            Piece("♝", pieceDarkColor), // Bishop
            Piece("♞", pieceDarkColor), // Knight
            Piece("♜", pieceDarkColor)  // Rook
        )
    }

    private val lightUnfilledPieces by lazy {
        arrayOf(
            Piece("♖", pieceLightColor), // Rook
            Piece("♘", pieceLightColor), // Knight
            Piece("♗", pieceLightColor), // Bishop
            Piece("♕", pieceLightColor), // Queen
            Piece("♔", pieceLightColor), // King
            Piece("♗", pieceLightColor), // Bishop
            Piece("♘", pieceLightColor), // Knight
            Piece("♖", pieceLightColor)  // Rook
        )
    }

    private val darkUnfilledPieces by lazy {
        arrayOf(
            Piece("♖", pieceDarkColor), // Rook
            Piece("♘", pieceDarkColor), // Knight
            Piece("♗", pieceDarkColor), // Bishop
            Piece("♕", pieceDarkColor), // Queen
            Piece("♔", pieceDarkColor), // King
            Piece("♗", pieceDarkColor), // Bishop
            Piece("♘", pieceDarkColor), // Knight
            Piece("♖", pieceDarkColor)  // Rook
        )
    }


    private val lightPieces = if (isLightFilled) lightFilledPieces else lightUnfilledPieces
    private val darkPieces = if (isDarkFilled) darkFilledPieces else darkUnfilledPieces
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
        if (fromIndex in indices() && toIndex in indices()) {
            val fromPiece = get(fromIndex) ?: return

            val toPiece = get(toIndex)

            val isFromWhitePiece = isLightPiece(fromPiece)

            if (isFromWhitePiece && !isWhiteTurn) {
                message("It's not your turn!")
                return
            } else if (!isFromWhitePiece && isWhiteTurn) {
                message("It's not your turn!")
                return
            }

            fun getSequence(
                sequence: Int, horizontal: Boolean, vertical: Boolean, diagonal: Boolean
            ): List<Int> {
                return getSequence(
                    fromIndex, sequence, isFromWhitePiece, horizontal, vertical, diagonal
                )
            }

            if (fromPiece.isKing()) {
                val sequence = getSequence(2, horizontal = true, vertical = true, diagonal = false)
                if (toIndex !in sequence) {
                    message("The King can only move one square in any direction.")
                    return
                }
            } else if (fromPiece.isRook()) {
                val sequence = getSequence(8, horizontal = true, vertical = true, diagonal = false)
                if (toIndex !in sequence) {
                    message("The Rook can only move one square in any direction.")
                    return
                }
            } else if (fromPiece.isBishop()) {
                val sequence = getSequence(8, horizontal = false, vertical = false, diagonal = true)
                if (toIndex !in sequence) {
                    message("The Bishop can only move one square diagonally.")
                    return
                }
            } else if (fromPiece.isQueen()) {
                val sequence = getSequence(8, horizontal = true, vertical = true, diagonal = true)
                if (toIndex !in sequence) {
                    message("The Queen can only move one square in any direction.")
                    return
                }
            } else if (fromPiece.isKnight()) {
                val sequence = getKnightSequence(fromIndex, isFromWhitePiece)
                if (toIndex !in sequence) {
                    message("The Knight can only move in an L shape.")
                    return
                }
            } else if (fromPiece.isPawn()) {
                Log.d("Jjj", "swapTo: true")
                val sequence = getPawnSequence(fromIndex, isFromWhitePiece)
                if (toIndex !in sequence) {
                    message("The Pawn can only move one square forward.")
                    return
                }
            } else return


            val isToWhitePiece = isLightPiece(toPiece)
            val isFromBlackPiece = isDarkPiece(fromPiece)
            val isToBlackPiece = isDarkPiece(toPiece)

            if (isFromWhitePiece && isToWhitePiece) {
                message("White cannot move to white")
            } else if (isFromBlackPiece && isToBlackPiece) {
                message("Black cannot move to black")
            } else {
                set(fromIndex, toIndex, fromPiece)

                if (toPiece != null) {
                    message(
                        String.format(
                            "%s attacks and captures %s!", fromPiece.symbol, toPiece.symbol
                        )
                    )
                }

            }

            isWhiteTurn = !isFromWhitePiece
        }
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

            if (left) {
                val pos = position - 1 * i
                left = if (pos >= leftLimit) add(pos)
                else false

            }

            if (right) {
                val pos = position + 1 * i
                right = if (pos <= rightLimit) add(pos)
                else false
            }

            if (up) up = add(position + 8 * i)

            if (down) down = add(position - 8 * i)

            if (upLeft) upLeft = add(position + 7 * i)

            if (upRight) upRight = add(position + 9 * i)

            if (downLeft) downLeft = add(position - 9 * i)

            if (downRight) downRight = add(position - 7 * i)
        }
        return list
    }

    private fun add(list: MutableList<Int>, pos: Int, isWhitePiece: Boolean): Boolean {
        if (pos !in indices()) return false
        val piece = get(pos)
        return if (piece == null) {
            list.add(pos)
        } else if (isEnemy(piece, isWhitePiece)) {
            !list.add(pos)
        } else false
    }

    private fun getKnightSequence(position: Int, isWhitePiece: Boolean): List<Int> {
        val list = mutableListOf<Int>()

        fun add(pos: Int) {
            add(list, pos, isWhitePiece)
        }

        add(position + 6)
        add(position - 6)
        add(position + 10)
        add(position - 10)
        add(position + 15)
        add(position - 15)
        add(position + 17)
        add(position - 17)

        return list
    }

    private fun getPawnSequence(position: Int, isWhitePiece: Boolean): List<Int> {
        val list = mutableListOf<Int>()

        fun add(pos: Int) {
            add(list, pos, isWhitePiece)
        }

        var animePosition = if (isWhitePiece) position + 7 else position - 7
        if (isEnemy(get(animePosition), isWhitePiece)) {
            add(animePosition)
        }

        animePosition = if (isWhitePiece) position + 8 else position - 8
        if (!isEnemy(get(animePosition), isWhitePiece)) {
            add(animePosition)
        }

        animePosition = if (isWhitePiece) position + 9 else position - 9
        if (isEnemy(get(animePosition), isWhitePiece)) {
            add(animePosition)
        }

        if (position in 8..16 || position in 48..56) {
            add(if (isWhitePiece) position + 16 else position - 16)
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

}

