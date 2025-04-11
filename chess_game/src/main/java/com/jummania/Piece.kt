package com.jummania


/**
 * Created by Jummania on 28/3/25.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
data class Piece(var symbol: String, val color: Int) {

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