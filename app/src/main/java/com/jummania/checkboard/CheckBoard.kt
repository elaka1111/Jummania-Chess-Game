package com.jummania.checkboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
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
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val min = minOf(height, width) - 55
        val x = (width - min) / 2f
        val y = (height - min) / 2f
        val size = min / 8

        var count = 0

        for (s in 0 until 8) {
            val mY = y + (size * s)
            val mBottom = y + size + (size * s)

            for (i in 0 until 8) {
                paint.color = if (count % 2 == 0) Color.BLACK else Color.WHITE
                canvas.drawRect(x + (size * i), mY, x + size + (size * i), mBottom, paint)
                count += 1
            }
            count += 1
        }

        // Draw the border
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f

        val startX = x + min
        val startY = y + min

        canvas.drawRect(x, y, startX, startY, paint)

        // Draw title
        paint.style = Paint.Style.FILL
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 55f
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.CENTER

        canvas.drawText("Check Board Pattern", width / 2f, y - 30, paint)
    }

}