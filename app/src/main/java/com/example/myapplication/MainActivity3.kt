package com.example.myapplication

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity3 : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var drawingView: DrawingView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        imageView = findViewById(R.id.imageView)
        drawingView = findViewById(R.id.drawingView)

        // Set up animation buttons to animate the ImageView
        findViewById<Button>(R.id.btnRotate).setOnClickListener {
            ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        }

        findViewById<Button>(R.id.btnScale).setOnClickListener {
            ObjectAnimator.ofFloat(imageView, "scaleX", 1f, 1.5f, 1f).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
            ObjectAnimator.ofFloat(imageView, "scaleY", 1f, 1.5f, 1f).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        }

        findViewById<Button>(R.id.btnTranslate).setOnClickListener {
            ObjectAnimator.ofFloat(imageView, "translationX", 0f, 100f, 0f).apply {
                duration = 1000
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        }

        // Clear the drawing canvas when requested
        findViewById<Button>(R.id.btnClearDrawing).setOnClickListener {
            drawingView.clearDrawing()
        }
    }
}