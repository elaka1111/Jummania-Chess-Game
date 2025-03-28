package com.jummania.checkboard

val emptyChess = Piece(" ", emptyColor)

fun Piece?.isEmpty(): Boolean {
    return this?.isEmpty() ?: true
}

fun Piece?.isNotEmpty(): Boolean {
    return this?.isNotEmpty() ?: false
}

fun Piece?.isWhite(): Boolean {
    return this?.isWhite() ?: false
}

fun Piece?.isBlack(): Boolean {
    return this?.isBlack() ?: false
}

fun Piece?.isEnemy(isWhitePiece: Boolean): Boolean {
    return this?.isEnemy(isWhitePiece) ?: false
}

fun Piece?.isKing(): Boolean {
    return this?.isKing() ?: false
}

fun Piece?.isQueen(): Boolean {
    return this?.isQueen() ?: false
}

fun Piece?.isRook(): Boolean {
    return this?.isRook() ?: false
}

fun Piece?.isBishop(): Boolean {
    return this?.isBishop() ?: false
}

fun Piece?.isKnight(): Boolean {
    return this?.isKnight() ?: false
}

fun Piece?.isPawn(): Boolean {
    return this?.isPawn() ?: false
}

fun Piece?.isWhitePawn(): Boolean {
    return this?.isWhitePawn() ?: false
}

fun Piece?.isBlackPawn(): Boolean {
    return this?.isBlackPawn() ?: false
}