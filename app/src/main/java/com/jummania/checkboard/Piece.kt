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

    fun isNotEmpty(): Boolean {
        return !isEmpty()
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

}