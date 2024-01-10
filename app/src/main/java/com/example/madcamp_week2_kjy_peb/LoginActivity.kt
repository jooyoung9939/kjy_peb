package com.example.madcamp_week2_kjy_peb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.madcamp_week2_kjy_peb.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    val api = RetroInterface.create()
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, Register::class.java)
            startActivity(intent)
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
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
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
            val intent = Intent(this@LoginActivity, AllUserActivity::class.java)
            startActivity(intent)
        }

        // 구글 로그인 옵션 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("648978352693-anm2jkrauoa94q7vp7h338mdcm97vp7s.apps.googleusercontent.com") // R.string.server_client_id는 구글 API 콘솔에서 발급받은 서버 클라이언트 ID에 대한 리소스 ID입니다.
            .requestEmail()
            .build()

        // 구글 로그인 클라이언트 설정
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // 구글 로그인 버튼 클릭 시 이벤트 처리
        binding.googleSignInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            Log.d("GoogleSignIn", "Starting Google Sign-In: $signInIntent")
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

    }

    // onActivityResult 메서드 추가
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task == null) {
                Log.w("GoogleSignIn", "GoogleSignInAccount task is null")
            }
            handleSignInResult(task)
        }
    }

    // handleSignInResult 메서드 추가
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            Log.d("GoogleSignIn", "signInResult: success")

            // Send ID token to server
            account?.idToken?.let {
                Log.d("GoogleSignIn", "ID Token found: $it")
                sendIdTokenToServer(it)
            } ?: Log.w("GoogleSignIn", "ID Token is null")
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            Log.w("GoogleSignIn", "signInResult: failed code=${e.statusCode}")
        }
    }

    private fun sendIdTokenToServer(idToken: String) {
        Log.d("testt", "Sending ID Token to server: $idToken")
        api.sendGoogleIdToken("Bearer $idToken", idToken).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    // 서버에서의 처리가 성공적으로 이루어졌을 때의 동작
                    Toast.makeText(applicationContext, "ID Token 전송 및 처리 성공", Toast.LENGTH_SHORT).show()
                } else {
                    // 서버에서의 처리가 실패했을 때의 동작
                    Toast.makeText(applicationContext, "서버에서의 처리 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                // 통신 실패 시 동작
                Log.d("testt", t.message.toString())
                Toast.makeText(applicationContext, "통신 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }
}