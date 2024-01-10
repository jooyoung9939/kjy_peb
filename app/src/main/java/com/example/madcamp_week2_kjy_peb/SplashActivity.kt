package com.example.madcamp_week2_kjy_peb

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logoImageView = findViewById<ImageView>(R.id.appLogo)

        // Rotate the logo 360 degrees with a duration of 1000 milliseconds
        val rotationAnimator = ObjectAnimator.ofFloat(logoImageView, "rotation", 0f, 360f)
        rotationAnimator.duration = 2000
        rotationAnimator.interpolator = AccelerateDecelerateInterpolator()
        rotationAnimator.start()

        Handler().postDelayed(Runnable {
            val i = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(i)
            finish()
        }, 3000)
    }
}