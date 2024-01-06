package com.example.madcamp_week2_kjy_peb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Response

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.madcamp_week2_kjy_peb.databinding.ActivityRegisterBinding
import com.google.gson.GsonBuilder
import retrofit2.Callback
import java.io.ByteArrayOutputStream
import java.io.InputStream

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    val api = RetroInterface.create()
    var imageString: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.insert.setOnClickListener{
            getContent.launch("image/*")
        }
        binding.registerButton.setOnClickListener{
            binding.apply {
                val id = inputID.text.toString()
                val pw = inputPw.text.toString()

                if(id == "" || pw == "") {
                    Toast.makeText(applicationContext, "입력하지 않은 정보가 있습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            val newUser = RegisterModel(binding.inputID.text.toString(), binding.inputPw.text.toString(), imageString)
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
    }

    private val getContent = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri: android.net.Uri? ->
        // 이미지를 선택한 후 처리할 코드
        if (uri != null) {
            // 선택한 이미지의 Uri를 이용하여 작업 수행
            val instream: InputStream? = contentResolver.openInputStream(uri)
            val imageBitmap: android.graphics.Bitmap = BitmapFactory.decodeStream(instream)
            instream?.close()
            binding.img.setImageBitmap(imageBitmap)
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 50, baos)
            val bytes: ByteArray = baos.toByteArray()
            imageString = Base64.encodeToString(bytes, Base64.DEFAULT)
        }
    }

}