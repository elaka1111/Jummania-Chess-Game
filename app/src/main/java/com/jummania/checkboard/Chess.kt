package com.jummania.checkboard

import android.graphics.Color


/**
 * Created by Jummania on 28/3/25.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */


class Chess {
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

    private val chessBoard = mutableListOf<Piece?>().also {
        it.addAll(whitePiece)
        for (i in 0 until 8) it.add(Piece("♟", whiteColor))
        for (i in 0 until 32) it.add(null)
        for (i in 0 until 8) it.add(Piece("♟", blackColor))
        it.addAll(blackPiece)
    }

    fun get(position: Int): Piece? {
        if (position in chessBoard.indices) return chessBoard[position]
        return null
    }

    fun set(fromIndex: Int, toIndex: Int, piece: Piece?) {
        chessBoard[toIndex] = piece
        chessBoard[fromIndex] = null
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
        if (piece == null) return false
        return (isWhitePiece && isBlack(piece)) || (!isWhitePiece && !isBlack(piece))
    }
}

