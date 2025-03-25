package com.jummania.checkboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View


/**
 * Created by Jummania on 24/3/25.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
class CheckBoard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val paint = Paint()
    private var darkColor = Color.BLACK
    private var lightColor = Color.WHITE
    private var chessColor: Int = 0xFF808080.toInt()

    private var touchedX: Float = 0f
    private var touchedY: Float = 0f

    private val chessBoard = mutableListOf(
        "♖️",
        "♘️",
        "♗️",
        "♕️",
        "♔️",
        "♗️",
        "♘️",
        "♖️",
        "♙️",
        "♙️",
        "♙️",
        "♙️",
        "♙️",
        "♙️",
        "♙️",
        "♙️",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "",
        "♟️",
        "♟️",
        "♟️",
        "♟️",
        "♟️",
        "♟️",
        "♟️",
        "♟️",
        "♜️",
        "♞️",
        "♝️",
        "♛️",
        "♚️",
        "♝️",
        "♞️",
        "♜️"
    )


    private val pawns = "♟️"

    private var isSelected = false
    private var selectedRowNumber = -1
    private var selectedPiece = ""

    private var isInvalidate = false

    //  private var needToMove = false


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val min = minOf(height, width) - 55
        val x = (width - min) / 2f
        val y = (height - min) / 2f
        val size = min / 8

        var count = 0
        var rowNumber = 0

        var selected = false

        for (s in 0 until 8) {
            val top = y + (size * s)
            val bottom = top + size

            for (i in 0 until 8) {

                rowNumber++

                val left = x + (size * i)
                val right = left + size

                if (touchedX in left..right && touchedY in top..bottom) {

                    if (selectedRowNumber == rowNumber && isSelected) {
                        isSelected = false
                        selectedRowNumber = -1
                        selectedPiece = ""
                    } else if (isSelected && selectedPiece != "") {
                        swapTo(selectedRowNumber - 1, rowNumber - 1)
                        isSelected = false
                        selectedRowNumber = -1
                        isInvalidate = true
                        selectedPiece = ""
                        invalidate()
                    } else if (!isInvalidate) {
                        isSelected = true
                        selectedRowNumber = rowNumber
                        selectedPiece = chessBoard[rowNumber - 1]
                    }

                    Log.d("Jjj", "$isSelected, $selectedRowNumber")

                    selected = true
                }

                paint.style = Paint.Style.FILL
                paint.color = if (count % 2 == 0) darkColor else lightColor
                // canvas.drawRect(left, top, right, bottom, paint)


                if (selectedRowNumber == rowNumber && selected && isSelected) {
                    Log.d("Jjj", "$selectedRowNumber")
                    paint.style = Paint.Style.STROKE
                    val padding = 11f
                    paint.strokeWidth = padding
                    paint.color = Color.RED

                    canvas.drawRect(
                        left + padding, top + padding, right - padding, bottom - padding, paint
                    )
                }

                fun drawPieces(text: String) {
                    drawText(
                        canvas,
                        chessColor,
                        size * 0.6f,
                        text,
                        left + (size / 2),
                        top + (size / 1.5f)
                    )
                }

                drawPieces(chessBoard[rowNumber - 1])

                count += 1
            }
            count += 1
        }

        if (!selected) isSelected = false

        // Draw the border
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f

        val startX = x + min
        val startY = y + min

        canvas.drawRect(x, y, startX, startY, paint)

        drawText(canvas, Color.BLACK, 60f, "Chess Game", width / 2f, y - 30)
    }

    private fun drawText(
        canvas: Canvas, color: Int, textSize: Float, text: String, x: Float, y: Float
    ) {
        paint.style = Paint.Style.FILL
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = textSize
        paint.color = color
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(text, x, y, paint)
    }

    fun onTouch(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            touchedX = event.x
            touchedY = event.y
            invalidate()
            isInvalidate = false
        }
    }

    private fun swapTo(fromIndex: Int, toIndex: Int) {
        if (fromIndex in chessBoard.indices && toIndex in chessBoard.indices) {
            val fromPiece = chessBoard[fromIndex]
            if (fromPiece != "") {
                chessBoard[toIndex] = fromPiece
                chessBoard[fromIndex] = ""
            }
        }
    }


}