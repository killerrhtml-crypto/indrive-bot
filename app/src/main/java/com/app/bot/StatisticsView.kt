package com.app.bot

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class StatisticsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 35f
        color = Color.parseColor("#38BDF8") // Azul cian profesional
    }

    private val paintBackgroundCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 35f
        color = Color.parseColor("#334155")
    }

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 50f
        textAlign = Paint.Align.CENTER
    }

    private val paintBar = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#10B981") // Verde profesional
    }

    var percentage: Float = 78.5f // Porcentaje dinámico del bot

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        // 1. Dibujar Círculo de Porcentajes (Gráfica Circular)
        val radius = width / 4f
        val centerX = width / 2f
        val centerY = height / 3f
        val rect = RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        canvas.drawArc(rect, 0f, 360f, false, paintBackgroundCircle)
        val sweepAngle = (percentage / 100f) * 360f
        canvas.drawArc(rect, -90f, sweepAngle, false, paintCircle)
        canvas.drawText("$percentage%", centerX, centerY + 15f, paintText)

        // 2. Dibujar Gráfica de Barras Dinámica en la parte inferior
        val barTop = height * 0.65f
        val barWidth = width * 0.15f
        val spacing = width * 0.08f
        val values = listOf(0.4f, 0.7f, 0.9f, 0.6f, 0.85f)

        for (i in values.indices) {
            val left = spacing + i * (barWidth + spacing)
            val barHeightVal = (height - barTop - 50f) * values[i]
            val rectBar = RectF(left, height - 50f - barHeightVal, left + barWidth, height - 50f)
            canvas.drawRoundRect(rectBar, 15f, 15f, paintBar)
        }
    }
}
