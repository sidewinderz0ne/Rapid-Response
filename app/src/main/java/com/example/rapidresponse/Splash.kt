package com.example.rapidresponse

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val thread: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(1500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    startActivity(Intent(this@Splash, MainActivity::class.java))
                    finish()
                }
            }
        }

        thread.start()
        val logoSRS = findViewById<ImageView>(R.id.logoSRS)
        Glide.with(this).load(R.drawable.logo_srs_ijo).into(logoSRS)
        val imageView = findViewById<ImageView>(R.id.splash)
        Glide.with(this).load(R.drawable.ic_launcher_rapidresponse).into(imageView)

        YoYo.with(Techniques.FadeIn)
                .duration(1000)
                .repeat(0)
                .playOn(findViewById(R.id.anim))

        val appLinkIntent = intent
        val appLinkAction = appLinkIntent.action
        val appLinkData = appLinkIntent.data
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}