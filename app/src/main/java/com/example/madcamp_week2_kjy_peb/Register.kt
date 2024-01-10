package com.example.madcamp_week2_kjy_peb

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.example.madcamp_week2_kjy_peb.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    val api = RetroInterface.create()
    var img_name: String = "osz.png"

    private val mbtiOptions = arrayOf("mbti 선택", "ESTJ", "ESTP", "ESFJ", "ESFP", "ENTJ", "ENTP", "ENFJ", "ENFP", "ISTJ", "ISTP", "ISFJ", "ISFP", "INTJ", "INTP", "INFJ", "INFP")
    private val hobbyOptions = arrayOf("취미 선택", "게임", "독서", "댄스", "등산", "만화/애니메이션 감상", "미술/그림 그리기", "악기 연주", "음악 감상", "여행", "영화 감상", "요리", "보드 게임", "산책", "스포츠 관람", "프로그래밍/코딩", "헬스/운동")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if(intent.hasExtra("address")){
            binding.region.text = intent.getStringExtra("address")
            binding.map.visibility = View.INVISIBLE
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mbtiOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.mbtiSpinner.adapter = adapter

        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, hobbyOptions)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.hobbySpinner.adapter = adapter1


        binding.insert.setOnClickListener{
            getContent.launch("image/*")
        }
        binding.map.setOnClickListener{
            val intent = Intent(this, GoogleMap::class.java)
            intent.putExtra("prev", "Register")
            startActivityForResult(intent, 813)
        }
        binding.registerButton.setOnClickListener{
            binding.apply {
                val id = inputID.text.toString()
                val pw = inputPw.text.toString()
                val selectedMbtiString = mbtiSpinner.selectedItem.toString()
                val selectedHobbyString = hobbySpinner.selectedItem.toString()
                val selectedRegionString = binding.region.text.toString()

                if(id == "" || pw == "") {
                    Toast.makeText(applicationContext, "입력하지 않은 정보가 있습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val selectedMbtiInt = convertMbtiStringToInt(selectedMbtiString)
                val selectedHobbyInt = convertHobbyStringToInt(selectedHobbyString)

                val newUser = RegisterModel(binding.inputID.text.toString(), binding.inputPw.text.toString(), selectedMbtiInt, selectedHobbyInt, selectedRegionString)
                api.register(newUser).enqueue(object: retrofit2.Callback<RegisterResult>{
                    override fun onResponse(call: Call<RegisterResult>, response: Response<RegisterResult>) {
                        val result = response.body()?.message ?: return
                        if(result) {
                            val tempFile = File(cacheDir, img_name+binding.inputID.text.toString())
                            try {
                                tempFile.createNewFile()
                                val out = FileOutputStream(tempFile)
                                val drawable: Drawable? = binding.img.drawable
                                val bitmap: Bitmap? = drawable?.toBitmap()
                                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, out)
                                out.close()
                                Toast.makeText(applicationContext, "파일 저장 성공", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                Toast.makeText(applicationContext, "파일 저장 실패", Toast.LENGTH_SHORT).show()
                            }
                            Toast.makeText(applicationContext, "회원가입 성공", Toast.LENGTH_SHORT).show()
                        }
                        else
                            Toast.makeText(applicationContext, "회원가입 실패, 이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(call: Call<RegisterResult>, t: Throwable) {
                        Log.d("testt", t.message.toString())
                    }
                })

            }

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 813 && resultCode == Activity.RESULT_OK) {
            // GoogleMap 액티비티에서 반환된 주소 정보를 처리
            val selectedAddress = data?.getStringExtra("address")
            binding.region.text = selectedAddress
            binding.map.visibility = View.INVISIBLE
        }
    }


    private fun convertMbtiStringToInt(mbtiString: String): Int {
        return when (mbtiString) {
            "mbti 선택" -> 17
            "ESTJ" -> 1
            "ESTP" -> 2
            "ESFJ" -> 3
            "ESFP" -> 4
            "ENTJ" -> 5
            "ENTP" -> 6
            "ENFJ" -> 7
            "ENFP" -> 8
            "ISTJ" -> 9
            "ISTP" -> 10
            "ISFJ" -> 11
            "ISFP" -> 12
            "INTJ" -> 13
            "INTP" -> 14
            "INFJ" -> 15
            "INFP" -> 16
            else -> 17 // Default value or handle unknown MBTI options
        }
    }

    private fun convertHobbyStringToInt(hobbyString: String): Int {
        return when (hobbyString) {
            "취미 선택" -> 17
            "게임" -> 1
            "독서" -> 2
            "댄스" -> 3
            "등산" -> 4
            "만화/애니메이션 감상" -> 5
            "미술/그림 그리기" -> 6
            "악기 연주" -> 7
            "음악 감상" -> 8
            "여행" -> 9
            "영화 감상" -> 10
            "요리" -> 11
            "보드 게임" -> 12
            "산책" -> 13
            "스포츠 관람" -> 14
            "프로그래밍/코딩" -> 15
            "헬스/운동" -> 16
            else -> 17 // Default value or handle unknown hobby options
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
            binding.insert.visibility = View.INVISIBLE
        }
    }

}
