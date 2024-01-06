package com.example.madcamp_week2_kjy_peb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.madcamp_week2_kjy_peb.databinding.ActivityMainBinding
import com.example.madcamp_week2_kjy_peb.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val id = intent.getStringExtra("id")
        binding.textView.text = "$id 님 안녕하세요."


    }
}