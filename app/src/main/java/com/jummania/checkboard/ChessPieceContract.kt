package com.jummania.checkboard

import android.graphics.Color


/**
 * Created by Jummania on 28/3/25.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */


val whiteColor = Color.WHITE
val blackColor = Color.BLACK
val emptyColor = Color.TRANSPARENT

val whitePiece = listOf(
    Piece("♜", whiteColor), // Rook
    Piece("♞", whiteColor), // Knight
    Piece("♝", whiteColor), // Bishop
    Piece("♛", whiteColor), // Queen
    Piece("♚", whiteColor), // King
    Piece("♝", whiteColor), // Bishop
    Piece("♞", whiteColor), // Knight
    Piece("♜", whiteColor)  // Rook
)

val blackPiece = listOf(
    Piece("♜", blackColor), // Rook
    Piece("♞", blackColor), // Knight
    Piece("♝", blackColor), // Bishop
    Piece("♛", blackColor), // Queen
    Piece("♚", blackColor), // King
    Piece("♝", blackColor), // Bishop
    Piece("♞", blackColor), // Knight
    Piece("♜", blackColor)  // Rook
)

val chessBoard = mutableListOf<Piece?>().also {
    it.addAll(whitePiece)
    for (i in 0 until 8) it.add(Piece("♟", whiteColor))
    for (i in 0 until 32) it.add(null)
    for (i in 0 until 8) it.add(Piece("♟", blackColor))
    it.addAll(blackPiece)
}
