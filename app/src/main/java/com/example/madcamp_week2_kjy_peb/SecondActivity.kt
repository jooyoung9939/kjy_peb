package com.example.madcamp_week2_kjy_peb

import android.content.ContentResolver
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.madcamp_week2_kjy_peb.databinding.ActivityMainBinding
import com.example.madcamp_week2_kjy_peb.databinding.ActivitySecondBinding
import java.io.ByteArrayOutputStream
import java.io.InputStream
import android.util.Base64
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding
    val api = RetroInterface.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val id = intent.getStringExtra("id")
        val uid = intent.getIntExtra("UID", 0)
        binding.textView.text = "$id 님 안녕하세요."
    }
}