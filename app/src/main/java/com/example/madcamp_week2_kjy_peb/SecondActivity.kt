package com.example.madcamp_week2_kjy_peb

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.madcamp_week2_kjy_peb.databinding.ActivitySecondBinding // 추가된 import

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding
    private lateinit var token: String

    val api = RetroInterface.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val id = intent.getStringExtra("id")

        token = intent.getStringExtra("token") ?: ""
        binding.textView.text = "$id 님 안녕하세요."

        binding.myInfoButton.setOnClickListener {
            // 토큰을 이용하여 사용자 정보를 요청
            getUserInfo()
        }
    }

    private fun getUserInfo() {
        api.getMyInfo("Bearer $token").enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val userInfo = response.body()
                    if (userInfo != null) {
                        // 사용자 정보를 받아와서 처리 (예: AlertDialog로 표시)
                        showUserInfoDialog(userInfo)
                    } else {
                        Toast.makeText(applicationContext, "사용자 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("testt", t.message.toString())
            }
        })
    }

    private fun showUserInfoDialog(userInfo: User) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("사용자 정보")
            .setMessage("ID: ${userInfo.users_id}\nPW: ${userInfo.users_pw}\nUID: ${userInfo.UID}")
            .setPositiveButton("확인", null)
            .create()

        dialog.show()

    }
}