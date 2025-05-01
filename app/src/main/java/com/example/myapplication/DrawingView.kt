package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    // Paint for drawing lines
    private val pathPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }

    // List of points drawn on the canvas
    private val pathPoints = mutableListOf<Pair<Float, Float>>()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in 1 until pathPoints.size) {
            val (x1, y1) = pathPoints[i - 1]
            val (x2, y2) = pathPoints[i]
            canvas.drawLine(x1, y1, x2, y2, pathPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                pathPoints.add(Pair(event.x, event.y))
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun clearDrawing() {
        pathPoints.clear()
        invalidate()
    }
}