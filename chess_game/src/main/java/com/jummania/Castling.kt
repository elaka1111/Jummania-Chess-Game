package com.jummania


/**
 * Created by Jummania on 17/4/25.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
class Castling {

    private var kingMoved: Boolean = false
    private var firstRookMoved: Boolean = false
    private var secondRookMoved: Boolean = false

    fun markKingMoved() {
        kingMoved = true
    }

    fun markFirstRookMoved() {
        firstRookMoved = true
    }

    fun markSecondRookMoved() {
        secondRookMoved = true
    }

    // Checking if king-side castling is possible
    fun isKingSideCastlingPossible(): Boolean {
        return !kingMoved && !secondRookMoved
    }

    // Checking if queen-side castling is possible
    fun isQueenSideCastlingPossible(): Boolean {
        return !kingMoved && !firstRookMoved
    }

    fun markCastled() {
        kingMoved = true
        firstRookMoved = true
        secondRookMoved = true
    }
}