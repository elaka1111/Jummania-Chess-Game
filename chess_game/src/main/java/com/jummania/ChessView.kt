package com.jummania

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import android.widget.Toast
import androidx.annotation.Keep
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

@Keep
class ChessView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private val symbolPaint = Paint()

    private var isSelected: Boolean = false
    private var isInvalidate: Boolean = false
    private var enableStroke: Boolean = true
    private var enableSoundEffect: Boolean = true

    companion object {
        var isWhiteTurn = true
    }

    private val bitmap by lazy { getBitmapFromVectorDrawable(context, R.drawable.transform) }

    private lateinit var chessController: ChessController

    private var touchedX: Float = 0f
    private var touchedY: Float = 0f

    private var selectedRowNumber = -1
    private var darkSquareColor: Int = "#8e4f19".toColorInt()
    private var lightSquareColor: Int = "#fadeaf".toColorInt()
    private var strokeLightColor: Int = Color.WHITE
    private var strokeDarkColor: Int = Color.BLACK

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
                        R.styleable.ChessView_symbolStyle, 1
                    )
                ), typedArray.getBoolean(R.styleable.ChessView_useBoldSymbol, false)
            )

            setEnableStroke(
                typedArray.getBoolean(R.styleable.ChessView_enableStroke, enableStroke),
                typedArray.getColor(
                    R.styleable.ChessView_strokeLightColor, chessController.pieceDarkColor
                ),
                typedArray.getColor(
                    R.styleable.ChessView_strokeDarkColor, chessController.pieceLightColor
                )
            )

            setSoundEffectEnabled(
                typedArray.getBoolean(
                    R.styleable.ChessView_enableSoundEffect, true
                )
            )
        } finally {
            typedArray.recycle() // always recycle manually
        }

        symbolPaint.style = Paint.Style.FILL
        symbolPaint.textAlign = Paint.Align.CENTER
        paint.textAlign = Paint.Align.CENTER
        symbolPaint.isAntiAlias = true

        setBackgroundColor((background as? ColorDrawable)?.color ?: Color.WHITE)
    }


    override fun onDraw(canvas: Canvas) {
        val minSize = minOf(height, width) - 55
        val boardLeft = (width - minSize) / 2f
        val boardTop = (height - minSize) / 2f
        val squareSize = minSize / 8f

        var rowIndex = 0
        var touchedInside = false

        for (row in 0 until 8) {
            val top = boardTop + row * squareSize
            val bottom = top + squareSize
            var isDarkSquare = row % 2 != 0

            for (col in 0 until 8) {
                val left = boardLeft + col * squareSize
                val right = left + squareSize

                rowIndex++

                // Check touch
                if (touchedX in left..right && touchedY in top..bottom) {
                    touchedInside = handleTouch(rowIndex)
                }

                // Draw square
                drawSquare(canvas, left, top, right, bottom, isDarkSquare)

                // Highlight selected square
                if (selectedRowNumber == rowIndex && touchedInside && isSelected) {
                    drawSelection(canvas, left, top, right, bottom, squareSize)
                }

                // Draw piece
                val piece = chessController.get(rowIndex - 1)
                if (piece != null) {
                    drawSymbol(
                        canvas,
                        squareSize * 0.6f,
                        piece,
                        left + squareSize / 2,
                        top + squareSize / 1.5f
                    )
                }

                isDarkSquare = !isDarkSquare
            }
        }

        if (!touchedInside) isSelected = false

        drawBoardBorder(canvas, boardLeft, boardTop, minSize)
        drawText(canvas, boardTop, squareSize, "Player 1")
        drawText(canvas, boardTop + minSize * 1.3f, squareSize, "Player 2")
        drawTurnIndicator(canvas, boardTop, minSize, squareSize)

        checkPawnPromotion(boardTop, minSize, squareSize)
    }

    private fun handleTouch(rowNumber: Int): Boolean {
        return when {
            selectedRowNumber == rowNumber && isSelected -> {
                isSelected = false
                selectedRowNumber = -1
                true
            }

            isSelected && selectedRowNumber > 0 -> {
                chessController.swapTo(selectedRowNumber - 1, rowNumber - 1)
                isSelected = false
                selectedRowNumber = -1
                isInvalidate = true
                invalidate()
                true
            }

            !isInvalidate -> {
                isSelected = true
                selectedRowNumber = rowNumber
                true
            }

            else -> false
        }
    }

    private fun drawSquare(
        canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float, isDark: Boolean
    ) {
        paint.style = Paint.Style.FILL
        paint.color = if (isDark) darkSquareColor else lightSquareColor
        canvas.drawRect(left, top, right, bottom, paint)
    }

    private fun drawSelection(
        canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float, size: Float
    ) {
        paint.style = Paint.Style.STROKE
        val padding = size * 0.05f
        paint.strokeWidth = padding
        paint.color = Color.RED
        canvas.drawRect(left + padding, top + padding, right - padding, bottom - padding, paint)
    }

    private fun drawBoardBorder(canvas: Canvas, x: Float, y: Float, min: Int) {
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        canvas.drawRect(x, y, x + min, y + min, paint)
    }

    private fun drawText(canvas: Canvas, y: Float, size: Float, text: String) {
        paint.textSize = size / 2f
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        canvas.drawText(text, width / 2f, y - size, paint)
    }

    private fun drawTurnIndicator(canvas: Canvas, y: Float, min: Int, size: Float) {
        val indicatorY = if (isWhiteTurn) y - size / 2 else y + min + size / 2
        val centerX = width / 2f
        val topY = indicatorY - size / 4

        paint.color = Color.RED

        val isPawnReady =
            (isWhiteTurn && selectedRowNumber in 57..64 && chessController.isWhitePawn(
                chessController.get(selectedRowNumber - 1)
            )) || (!isWhiteTurn && selectedRowNumber in 1..8 && chessController.isBlackPawn(
                chessController.get(selectedRowNumber - 1)
            ))

        if (isPawnReady && bitmap != null) {
            canvas.drawBitmap(bitmap!!, centerX, topY, paint)
        } else {
            canvas.drawCircle(centerX, indicatorY, 22f, paint)
        }
    }

    private fun checkPawnPromotion(y: Float, min: Int, size: Float) {
        val centerX = width / 2f
        val indicatorY = if (isWhiteTurn) y - size / 2 else y + min + size / 2
        val topY = indicatorY - size / 4

        val isPawnReady =
            (isWhiteTurn && selectedRowNumber in 57..64 && chessController.isWhitePawn(
                chessController.get(selectedRowNumber - 1)
            )) || (!isWhiteTurn && selectedRowNumber in 1..8 && chessController.isBlackPawn(
                chessController.get(selectedRowNumber - 1)
            ))

        if (isPawnReady && bitmap != null && touchedX in centerX..centerX + bitmap!!.width && touchedY in topY..topY + bitmap!!.height) {
            val symbols = chessController.getPromotedSymbols(isWhiteTurn)
            var selectedSymbolIndex = 0

            MaterialAlertDialogBuilder(context).setTitle("Promote Your Pawn")
                .setSingleChoiceItems(symbols, selectedSymbolIndex) { _, which ->
                    selectedSymbolIndex = which
                }.setPositiveButton("Okay") { _, _ ->
                    val symbol = symbols[selectedSymbolIndex]
                    chessController.set(selectedRowNumber - 1, symbol)
                    message("The Pawn Promoted to $symbol")
                    touchedX = 0f
                    touchedY = 0f
                    selectedRowNumber = -1
                    isInvalidate = true
                    invalidate()
                }.setNegativeButton("Cancel", null).show()
        }
    }


    private fun drawSymbol(
        canvas: Canvas, textSize: Float, piece: Piece, x: Float, y: Float
    ) {
        symbolPaint.textSize = textSize
        val symbol = piece.symbol
        val isLightPiece = chessController.isLightPiece(piece)

        if (enableStroke) {
            val transformedSymbol = chessController.transform(symbol)

            val shouldDrawBackground = if (isLightPiece) {
                !chessController.isLightFilled
            } else {
                !chessController.isDarkFilled
            }

            if (shouldDrawBackground) {
                symbolPaint.color = if (isLightPiece) strokeLightColor else strokeDarkColor
                canvas.drawText(transformedSymbol, x, y, symbolPaint)
            }

            // Draw the actual piece symbol
            symbolPaint.color = piece.color
            canvas.drawText(symbol, x, y, symbolPaint)

            val shouldDrawTopLayer = if (isLightPiece) {
                chessController.isLightFilled
            } else {
                chessController.isDarkFilled
            }

            if (shouldDrawTopLayer) {
                symbolPaint.color = if (isLightPiece) strokeLightColor else strokeDarkColor
                canvas.drawText(transformedSymbol, x, y, symbolPaint)
            }

        } else {
            // Stroke is disabled, just draw the piece symbol
            symbolPaint.color = piece.color
            canvas.drawText(symbol, x, y, symbolPaint)
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

    fun setSymbolStyle(style: SymbolStyle, useBoldSymbol: Boolean) {
        fun setFont(id: Int): Typeface? {
            return ResourcesCompat.getFont(context, id)
        }

        val typeface = when (style) {
            SymbolStyle.CLASSIC -> setFont(R.font.chess_alpha)
            SymbolStyle.MERIDA -> setFont(R.font.chess_merida_unicode)
            SymbolStyle.SYMBOLA -> setFont(R.font.symbola)
            SymbolStyle.STANDARD -> null
        }

        symbolPaint.typeface =
            Typeface.create(typeface, if (useBoldSymbol) Typeface.BOLD else Typeface.NORMAL)
    }

    fun setEnableStroke(enable: Boolean, lightColor: Int, darkColor: Int) {
        enableStroke = enable
        strokeLightColor = lightColor
        strokeDarkColor = darkColor
    }

    fun setSoundEffectEnabled(enable: Boolean) {
        enableSoundEffect = enable
    }


    override fun performClick(): Boolean {
        if (enableSoundEffect) {
            playSoundEffect(SoundEffectConstants.CLICK)
        }
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