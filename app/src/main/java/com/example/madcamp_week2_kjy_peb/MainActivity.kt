package com.example.madcamp_week2_kjy_peb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.madcamp_week2_kjy_peb.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val api = RetroInterface.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            binding.apply {
                val id = inputID.text.toString()
                val pw = inputPw.text.toString()

                if(id == "" || pw == "") {
                    Toast.makeText(applicationContext, "입력하지 않은 정보가 있습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            val newUser = RegisterModel(binding.inputID.text.toString(), binding.inputPw.text.toString())
            api.register(newUser).enqueue(object: retrofit2.Callback<RegisterResult>{
                override fun onResponse(call: Call<RegisterResult>, response: Response<RegisterResult>) {
                    val result = response.body()?.message ?: return
                    if(result)
                        Toast.makeText(applicationContext, "회원가입 성공", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(applicationContext, "회원가입 실패, 이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<RegisterResult>, t: Throwable) {
                    Log.d("testt", t.message.toString())
                }
            })
        }
        binding.loginButton.setOnClickListener {
            binding.apply {
                val id = inputID.text.toString()
                val pw = inputPw.text.toString()

                if(id == "" || pw == "") {
                    Toast.makeText(applicationContext, "입력하지 않은 정보가 있습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val loginUser = LoginModel(binding.inputID.text.toString(), binding.inputPw.text.toString())
            api.login(loginUser).enqueue(object : Callback<LoginResult> {
                override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
                    val user_uid = response.body()?.UID ?: return
                    if (user_uid != -1) {
                        // 로그인 성공 시, 서버에서 반환한 accessToken을 추출
                        val token = response.body()?.accessToken ?: ""
                        Toast.makeText(applicationContext, "로그인 성공", Toast.LENGTH_SHORT).show()

                        // SecondActivity로 이동
                        val intent = Intent(this@MainActivity, SecondActivity::class.java)
                        intent.putExtra("id", binding.inputID.text.toString())
                        intent.putExtra("token", token)
                        startActivity(intent)

                        Log.d("testt", user_uid.toString())
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "로그인 실패, 아이디 또는 비밀번호를 확인해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                    Log.d("testt", t.message.toString())
                }
            })

        }
        binding.allUserButton.setOnClickListener {
            api.allUser().enqueue(object:Callback<ArrayList<User>>{
                override fun onResponse(
                    call: Call<ArrayList<User>>,
                    response: Response<ArrayList<User>>
                ) {
                    val userList = response.body() ?: return
                    val intent = Intent(this@MainActivity, AllUserActivity::class.java)
                    intent.putExtra("userList", userList)
                    startActivity(intent)
                }

                override fun onFailure(call: Call<ArrayList<User>>, t: Throwable) {
                    Log.d("testt",t.message.toString())
                }
            })
        }

    }
}