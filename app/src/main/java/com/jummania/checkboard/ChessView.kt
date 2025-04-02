package com.jummania.checkboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toColorInt
import com.google.android.material.dialog.MaterialAlertDialogBuilder


/**
 * Created by Jummania on 24/3/25.
 * Email: sharifuddinjumman@gmail.com
 * Dhaka, Bangladesh.
 */
class ChessView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val chessFont = ResourcesCompat.getFont(context, R.font.chess_merida_unicode)
    private val paint = Paint()

    private val chessPiecePaint = Paint().apply {
        style = Paint.Style.FILL
        typeface = chessFont
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    private var darkColor = "#739654".toColorInt()
    private var lightColor = "#ebeed3".toColorInt()

    private var touchedX: Float = 0f
    private var touchedY: Float = 0f


    private var isSelected = false
    private var selectedRowNumber = -1
    private var selectedPiece = ""

    private var isInvalidate = false

    companion object {
        var isWhiteTurn = true
    }

    private val bitmap = getBitmapFromVectorDrawable(context, R.drawable.transform)


    //  private var needToMove = false

    private val chess = Chess(context)


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
                    } else if (isSelected && selectedPiece.isNotEmpty()) {
                        chess.swapTo(selectedRowNumber - 1, rowNumber - 1)
                        isSelected = false
                        selectedRowNumber = -1
                        isInvalidate = true
                        selectedPiece = ""
                        invalidate()
                    } else if (!isInvalidate) {
                        isSelected = true
                        selectedRowNumber = rowNumber
                        selectedPiece = chess.get(rowNumber - 1)?.symbol ?: ""
                    }

                    selected = true
                }

                paint.style = Paint.Style.FILL
                paint.color = if (count % 2 == 0) darkColor else lightColor
                canvas.drawRect(left, top, right, bottom, paint)


                if (selectedRowNumber == rowNumber && selected && isSelected) {
                    paint.style = Paint.Style.STROKE
                    val padding = size * 0.05f
                    paint.strokeWidth = padding
                    paint.color = Color.RED

                    canvas.drawRect(
                        left + padding, top + padding, right - padding, bottom - padding, paint
                    )
                }

                fun drawPieces(text: String, chessColor: Int) {
                    drawText(
                        canvas,
                        chessColor,
                        size * 0.6f,
                        text,
                        left + (size / 2),
                        top + (size / 1.5f)
                    )
                }

                val chess = chess.get(rowNumber - 1)
                if (chess != null) drawPieces(chess.symbol, chess.color)

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

        drawText(canvas, Color.BLACK, 100f, "Chess"/*"♔♕♖♗♘♙ ♚♛♜♝♞♟"*/, width / 2f, y - size)

        val halfSize = size / 2f

        paint.color = Color.RED

        val turnIndicatorY = if (isWhiteTurn) y - halfSize else startY + halfSize
        val leftX = width / 2f
        val topY = turnIndicatorY - halfSize / 2f
        var shouldReplace = false
        if (isWhiteTurn) {
            if (selectedRowNumber in 57..64 && chess.isWhitePawn(
                    chess.get(
                        selectedRowNumber - 1
                    )
                ) && bitmap != null
            ) {
                canvas.drawBitmap(bitmap, leftX, topY, paint)
                shouldReplace = true
            } else {
                canvas.drawCircle(
                    leftX, turnIndicatorY, 22f, paint
                )
            }
        } else {
            if (selectedRowNumber in 1..8 && chess.isBlackPawn(
                    chess.get(
                        selectedRowNumber - 1
                    )
                ) && bitmap != null
            ) {
                canvas.drawBitmap(bitmap, leftX, topY, paint)
                shouldReplace = true
            } else {
                canvas.drawCircle(
                    leftX, turnIndicatorY, 22f, paint
                )
            }
        }


        if (shouldReplace && bitmap != null && touchedX in leftX..leftX + bitmap.width && touchedY in topY..topY + bitmap.height) {
            val symbols = if (isWhiteTurn) chess.getWhiteSymbols() else chess.getBlackPiece()
            var position = 0

            MaterialAlertDialogBuilder(context).setTitle("Promote Your Pawn")
                .setPositiveButton("Okay") { _, _ ->
                    val symbol = symbols[position] ?: return@setPositiveButton
                    chess.set(selectedRowNumber - 1, symbol)
                    message("The Pawn Promoted to $symbol")
                    touchedX = 0f
                    touchedY = 0f
                    selectedRowNumber = -1
                    isInvalidate = true
                    selectedPiece = ""
                    invalidate()
                }.setNegativeButton("Cancel") { _, _ ->
                }.setSingleChoiceItems(
                    symbols, position
                ) { _, which ->
                    position = which
                }.show()
        }


    }

    private fun drawText(
        canvas: Canvas, color: Int, textSize: Float, text: String, x: Float, y: Float
    ) {
        chessPiecePaint.textSize = textSize
        chessPiecePaint.setShadowLayer(
            15f, 0f, 0f, if (color == chess.whiteColor) chess.blackColor else chess.whiteColor
        ) // Shadow properties
        chessPiecePaint.color = color
        canvas.drawText(text, x, y, chessPiecePaint)
    }

    fun onTouch(event: MotionEvent) {
        if (event.action == MotionEvent.ACTION_DOWN) {
            touchedX = event.x
            touchedY = event.y
            invalidate()
            isInvalidate = false
        }
    }

    private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
        return AppCompatResources.getDrawable(context, drawableId)?.toBitmap()
    }

    private fun message(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


}