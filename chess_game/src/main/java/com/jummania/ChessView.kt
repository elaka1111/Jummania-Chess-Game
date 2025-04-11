package com.jummania

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SoundEffectConstants
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

    private val paint = Paint()
    private val symbolPaint = Paint()

    private var isSelected = false
    private var isInvalidate = false

    companion object {
        var isWhiteTurn = true
    }

    private val bitmap by lazy { getBitmapFromVectorDrawable(context, R.drawable.transform) }

    private lateinit var chessController: ChessController

    private var touchedX: Float = 0f
    private var touchedY: Float = 0f

    private var selectedRowNumber = -1
    private var darkSquareColor: Int = "#739654".toColorInt()
    private var lightSquareColor: Int = "#ebeed3".toColorInt()

    init {

        val typedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.ChessView, defStyleAttr, defStyleAttr
        )

        try {
            setPieceStyle(
                typedArray.getBoolean(R.styleable.ChessView_isLightFilled, true),
                typedArray.getBoolean(R.styleable.ChessView_isDarkFilled, true),
                typedArray.getColor(R.styleable.ChessView_pieceLightColor, Color.WHITE),
                typedArray.getColor(R.styleable.ChessView_pieceDarkColor, Color.BLACK)
            )

            setSquareColors(
                typedArray.getColor(R.styleable.ChessView_lightSquareColor, lightSquareColor),
                typedArray.getColor(R.styleable.ChessView_darkSquareColor, darkSquareColor)
            )

            setSymbolStyle(
                SymbolStyle.fromInt(
                    typedArray.getInt(
                        R.styleable.ChessView_symbolStyle,
                        1
                    )
                )
            )
        } finally {
            typedArray.recycle() // always recycle manually
        }

        symbolPaint.style = Paint.Style.FILL
        symbolPaint.textAlign = Paint.Align.CENTER
        paint.textAlign = Paint.Align.CENTER
        symbolPaint.isAntiAlias = true


        setBackgroundColor(Color.WHITE)
    }


    override fun onDraw(canvas: Canvas) {

        val min = minOf(height, width) - 55
        val x = (width - min) / 2f
        val y = (height - min) / 2f
        val size = min / 8

        var isDarkSquare = false
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
                    } else if (isSelected && selectedRowNumber > 0) {
                        chessController.swapTo(selectedRowNumber - 1, rowNumber - 1)
                        isSelected = false
                        selectedRowNumber = -1
                        isInvalidate = true
                        invalidate()
                    } else if (!isInvalidate) {
                        isSelected = true
                        selectedRowNumber = rowNumber
                    }

                    selected = true
                }

                paint.style = Paint.Style.FILL
                paint.color = if (isDarkSquare) darkSquareColor else lightSquareColor
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

                val piece = chessController.get(rowNumber - 1)
                if (piece != null) {
                    drawSymbol(
                        canvas, size * 0.6f, piece, left + (size / 2), top + (size / 1.5f)
                    )
                }

                isDarkSquare = !isDarkSquare
            }
            isDarkSquare = !isDarkSquare
        }

        if (!selected) isSelected = false

        // Draw the border
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f

        val startX = x + min
        val startY = y + min

        canvas.drawRect(x, y, startX, startY, paint)

        paint.textSize = size / 2f
        paint.color = Color.BLACK
        canvas.drawText("Jummania Chess Game", width / 2f, y - size, paint)

        val halfSize = size / 2f

        paint.color = Color.RED

        val turnIndicatorY = if (isWhiteTurn) y - halfSize else startY + halfSize
        val leftX = width / 2f
        val topY = turnIndicatorY - halfSize / 2f
        var shouldReplace = false
        if (isWhiteTurn) {
            if (selectedRowNumber in 57..64 && chessController.isWhitePawn(
                    chessController.get(
                        selectedRowNumber - 1
                    )
                ) && bitmap != null
            ) {
                canvas.drawBitmap(bitmap!!, leftX, topY, paint)
                shouldReplace = true
            } else {
                canvas.drawCircle(
                    leftX, turnIndicatorY, 22f, paint
                )
            }
        } else {
            if (selectedRowNumber in 1..8 && chessController.isBlackPawn(
                    chessController.get(
                        selectedRowNumber - 1
                    )
                ) && bitmap != null
            ) {
                canvas.drawBitmap(bitmap!!, leftX, topY, paint)
                shouldReplace = true
            } else {
                canvas.drawCircle(
                    leftX, turnIndicatorY, 22f, paint
                )
            }
        }


        if (shouldReplace && bitmap != null && touchedX in leftX..leftX + bitmap!!.width && touchedY in topY..topY + bitmap!!.height) {
            val symbols = chessController.getPromotedSymbols(isWhiteTurn)
            var position = 0

            MaterialAlertDialogBuilder(context).setTitle("Promote Your Pawn")
                .setPositiveButton("Okay") { _, _ ->
                    val symbol = symbols[position]
                    chessController.set(selectedRowNumber - 1, symbol)
                    message("The Pawn Promoted to $symbol")
                    touchedX = 0f
                    touchedY = 0f
                    selectedRowNumber = -1
                    isInvalidate = true
                    invalidate()
                }.setNegativeButton("Cancel") { _, _ ->
                }.setSingleChoiceItems(
                    symbols, position
                ) { _, which ->
                    position = which
                }.show()
        }


    }

    private fun drawSymbol(
        canvas: Canvas, textSize: Float, piece: Piece, x: Float, y: Float
    ) {

        symbolPaint.textSize = textSize
        val symbol = piece.symbol

        val transformedSymbol = chessController.transform(symbol)
        val isLightPiece = chessController.isLightPiece(piece)

        if (isLightPiece) {
            if (!chessController.isLightFilled) {
                symbolPaint.color = chessController.pieceDarkColor
                canvas.drawText(transformedSymbol, x, y, symbolPaint)
            }
        } else if (!chessController.isDarkFilled) {
            symbolPaint.color = chessController.pieceLightColor
            canvas.drawText(transformedSymbol, x, y, symbolPaint)
        }

        symbolPaint.color = piece.color
        canvas.drawText(symbol, x, y, symbolPaint)

        if (isLightPiece) {
            if (chessController.isLightFilled) {
                symbolPaint.color = chessController.pieceDarkColor
                canvas.drawText(transformedSymbol, x, y, symbolPaint)
            }
        } else if (chessController.isDarkFilled) {
            symbolPaint.color = chessController.pieceLightColor
            canvas.drawText(transformedSymbol, x, y, symbolPaint)
        }
    }

    private fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
        return AppCompatResources.getDrawable(context, drawableId)?.toBitmap()
    }

    private fun message(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun setSquareColors(light: Int = lightSquareColor, dark: Int = darkSquareColor) {
        lightSquareColor = light
        darkSquareColor = dark
    }

    fun setPieceStyle(
        isLightFilled: Boolean,
        isDarkFilled: Boolean,
        lightColor: Int,
        darkColor: Int,
    ) {
        chessController =
            ChessController(context, isLightFilled, isDarkFilled, lightColor, darkColor)
    }

    fun setSymbolStyle(style: SymbolStyle) {
        fun setFont(id: Int): Typeface? {
            return ResourcesCompat.getFont(context, id)
        }

        val typeface = when (style) {
            SymbolStyle.CLASSIC -> setFont(R.font.chess_alpha)
            SymbolStyle.MERIDA -> setFont(R.font.chess_merida_unicode)
            SymbolStyle.SYMBOLA -> setFont(R.font.symbola)
            SymbolStyle.STANDARD -> null
        }

        symbolPaint.typeface = typeface
    }


    override fun performClick(): Boolean {
        playSoundEffect(SoundEffectConstants.CLICK)
        invalidate()
        isInvalidate = false
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            touchedX = event.x
            touchedY = event.y
            performClick()
            return true
        }
        return super.onTouchEvent(event)
    }
}