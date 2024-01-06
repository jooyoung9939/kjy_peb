package com.example.madcamp_week2_kjy_peb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.madcamp_week2_kjy_peb.databinding.ActivityAllUserBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllUserBinding
    val api = RetroInterface.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        api.allUser().enqueue(object: Callback<ArrayList<User>> {
            override fun onResponse(
                call: Call<ArrayList<User>>,
                response: Response<ArrayList<User>>
            ) {
                val userList = response.body() ?: return
                val adapter = Adapter()
                binding.recyclerView.adapter = adapter
                binding.recyclerView.layoutManager = LinearLayoutManager(this@AllUserActivity)
                adapter.submitList(userList)
            }

            override fun onFailure(call: Call<ArrayList<User>>, t: Throwable) {
                Log.d("testt",t.message.toString())
            }
        })

    }
}