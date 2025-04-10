package com.jummania

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import com.jummania.ChessView.Companion.isWhiteTurn


/**
 * Created by Jummania on 28/3/25.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */


class Chess(private val context: Context) {
    val whiteColor = Color.WHITE
    val blackColor = Color.BLACK

    private val whitePiece = listOf(
        Piece("♜", whiteColor), // Rook
        Piece("♞", whiteColor), // Knight
        Piece("♝", whiteColor), // Bishop
        Piece("♛", whiteColor), // Queen
        Piece("♚", whiteColor), // King
        Piece("♝", whiteColor), // Bishop
        Piece("♞", whiteColor), // Knight
        Piece("♜", whiteColor)  // Rook
    )

    private val blackPiece = listOf(
        Piece("♜", blackColor), // Rook
        Piece("♞", blackColor), // Knight
        Piece("♝", blackColor), // Bishop
        Piece("♛", blackColor), // Queen
        Piece("♚", blackColor), // King
        Piece("♝", blackColor), // Bishop
        Piece("♞", blackColor), // Knight
        Piece("♜", blackColor)  // Rook
    )

    private val chessBoard = mutableListOf<Piece?>()

    init {
        chessBoard.addAll(whitePiece)
        for (i in 0 until 8) chessBoard.add(Piece("♟", whiteColor))
        for (i in 0 until 32) chessBoard.add(null)
        for (i in 0 until 8) chessBoard.add(Piece("♟", blackColor))
        chessBoard.addAll(blackPiece)
    }

    private val whitePromotedSymbols by lazy {
        val array = arrayOfNulls<String>(4)
        for (i in whitePiece.indices) {
            val piece = whitePiece[i]
            if (piece.isQueen() || piece.isRook() || piece.isBishop() || piece.isKnight()) {
                val symbol = piece.symbol
                if (!array.contains(symbol)) array[i] = symbol
            }
        }
        array
    }
    private val blackPromotedSymbols by lazy {
        val array = arrayOfNulls<String>(4)
        for (i in blackPiece.indices) {
            val piece = blackPiece[i]
            if (piece.isQueen() || piece.isRook() || piece.isBishop() || piece.isKnight()) {
                val symbol = piece.symbol
                if (!array.contains(symbol)) array[i] = symbol
            }
        }
        array
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

    fun indices(): IntRange {
        return chessBoard.indices
    }

    fun isWhite(piece: Piece?): Boolean {
        return piece?.color == whiteColor
    }

    fun isBlack(piece: Piece?): Boolean {
        return piece?.color == blackColor
    }

    fun isWhitePawn(piece: Piece?): Boolean {
        if (piece == null) return false
        return piece.isPawn() && piece.color == whiteColor
    }

    fun isBlackPawn(piece: Piece?): Boolean {
        if (piece == null) return false
        return piece.isPawn() && piece.color == blackColor
    }

    fun isEnemy(piece: Piece?, isWhitePiece: Boolean): Boolean {
        if (piece.isEmpty()) return false
        return (isWhitePiece && isBlack(piece)) || (!isWhitePiece && !isBlack(piece))
    }

    fun getWhiteSymbols(): Array<String?> {
        return whitePromotedSymbols
    }

    fun getBlackPiece(): Array<String?> {
        return blackPromotedSymbols
    }

    fun swapTo(fromIndex: Int, toIndex: Int) {
        if (fromIndex in indices() && toIndex in indices()) {
            val fromPiece = get(fromIndex) ?: return

            if (fromPiece.isEmpty()) return

            val toPiece = get(toIndex)

            val isFromWhitePiece = isWhite(fromPiece)

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
                val sequence = getSequence(2, true, true, false)
                if (toIndex !in sequence) {
                    message("The King can only move one square in any direction.")
                    return
                }
            } else if (fromPiece.isRook()) {
                val sequence = getSequence(8, true, true, false)
                if (toIndex !in sequence) {
                    message("The Rook can only move one square in any direction.")
                    return
                }
            } else if (fromPiece.isBishop()) {
                val sequence = getSequence(8, false, false, true)
                if (toIndex !in sequence) {
                    message("The Bishop can only move one square diagonally.")
                    return
                }
            } else if (fromPiece.isQueen()) {
                val sequence = getSequence(8, true, true, true)
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


            val isToWhitePiece = isWhite(toPiece)
            val isFromBlackPiece = isBlack(fromPiece)
            val isToBlackPiece = isBlack(toPiece)

            if (isFromWhitePiece && isToWhitePiece) {
                message("White cannot move to white")
            } else if (isFromBlackPiece && isToBlackPiece) {
                message("Black cannot move to black")
            } else if (fromPiece.isNotEmpty()) {
                set(fromIndex, toIndex, fromPiece)

                if (toPiece.isNotEmpty()) {
                    message(
                        String.format(
                            "%s attacks and captures %s!", fromPiece.symbol, toPiece?.symbol
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
        return if (piece.isEmpty()) {
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

}

