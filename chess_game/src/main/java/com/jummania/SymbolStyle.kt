package com.jummania

import androidx.annotation.Keep


/**
 * Created by Jummania on 11/4/25.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */

@Keep
enum class SymbolStyle(val value: Int) {
    STANDARD(0),   // system/default font
    CLASSIC(1),    // chess_alpha.ttf
    MERIDA(2),     // chess_merida_unicode.ttf
    SYMBOLA(3);    // symbola.ttf

    @Keep
    companion object {
        fun fromInt(value: Int): SymbolStyle {
            return entries.firstOrNull { it.value == value } ?: STANDARD
        }
    }
}