package com.jummania.checkboard


/**
 * Created by Jummania on 28/3/25.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
data class Piece(val symbol: String, val color: Int) {

    fun isEmpty(): Boolean {
        return symbol.isEmpty()
    }

    fun isWhite(): Boolean {
        return color == whiteColor
    }

    fun isBlack(): Boolean {
        return color == blackColor
    }

    fun isNotEmpty(): Boolean {
        return !isEmpty()
    }

    fun isEnemy(isWhitePiece: Boolean): Boolean {
        return (isWhitePiece && isBlack()) || (!isWhitePiece && !isBlack())
    }

    fun isKing(): Boolean {
        return symbol == "♔" || symbol == "♚"
    }

    fun isQueen(): Boolean {
        return symbol == "♕" || symbol == "♛"
    }

    fun isRook(): Boolean {
        return symbol == "♖" || symbol == "♜"
    }

    fun isBishop(): Boolean {
        return symbol == "♗" || symbol == "♝"
    }

    fun isKnight(): Boolean {
        return symbol == "♘" || symbol == "♞"
    }

    fun isPawn(): Boolean {
        return symbol == "♙" || symbol == "♟"
    }

    fun isWhitePawn(): Boolean {
        return isPawn() && color == whiteColor
    }

    fun isBlackPawn(): Boolean {
        return isPawn() && color == blackColor
    }

}