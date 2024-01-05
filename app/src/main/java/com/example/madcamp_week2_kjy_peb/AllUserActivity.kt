package com.example.madcamp_week2_kjy_peb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.madcamp_week2_kjy_peb.databinding.ActivityAllUserBinding

class AllUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val users = intent.getSerializableExtra("userList") as ArrayList<User>
        val adapter = Adapter()
        binding.recyclerView.adapter = adapter
        adapter.submitList(users)

    }
}