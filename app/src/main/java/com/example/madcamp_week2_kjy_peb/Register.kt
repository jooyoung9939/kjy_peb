package com.example.madcamp_week2_kjy_peb

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Response
import java.io.File
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import android.widget.ArrayAdapter
import androidx.core.graphics.drawable.toBitmap
import com.example.madcamp_week2_kjy_peb.databinding.ActivityRegisterBinding
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

import java.io.InputStream

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    val api = RetroInterface.create()
    var img_name: String = "osz.png"

    private val mbtiOptions = arrayOf("mbti 선택", "ESTJ", "ESTP", "ESFJ", "ESFP", "ENTJ", "ENTP", "ENFJ", "ENFP", "ISTJ", "ISTP", "ISFJ", "ISFP", "INTJ", "INTP", "INFJ", "INFP")
    private val hobbyOptions = arrayOf("취미 선택", "게임", "독서", "댄스", "등산", "만화/애니메이션 감상", "미술/그림 그리기", "악기 연주", "음악 감상", "여행", "영화 감상", "요리", "보드 게임", "산책", "스포츠 관람", "프로그래밍/코딩", "헬스/운동")
    private val regionOptions = arrayOf("지역 선택", "서울특별시", "인천광역시", "부산광역시", "대구광역시", "대전광역시", "광주광역시", "울산광역시", "경기도", "경상북도", "경상남도", "충청북도", "충청남도", "전라북도", "전라남도", "강원도", "제주특별자치도", "세종특별자치도")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mbtiOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.mbtiSpinner.adapter = adapter

        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, hobbyOptions)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.hobbySpinner.adapter = adapter1

        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, regionOptions)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.regionSpinner.adapter = adapter2

        binding.insert.setOnClickListener{
            getContent.launch("image/*")
        }
        binding.registerButton.setOnClickListener{
            binding.apply {
                val id = inputID.text.toString()
                val pw = inputPw.text.toString()
                val selectedMbtiString = mbtiSpinner.selectedItem.toString()
                val selectedHobbyString = hobbySpinner.selectedItem.toString()
                val selectedRegionString = regionSpinner.selectedItem.toString()

                if(id == "" || pw == "") {
                    Toast.makeText(applicationContext, "입력하지 않은 정보가 있습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val selectedMbtiInt = convertMbtiStringToInt(selectedMbtiString)
                val selectedHobbyInt = convertHobbyStringToInt(selectedHobbyString)
                val selectedRegionInt = convertRegionStringToInt(selectedRegionString)

                val newUser = RegisterModel(binding.inputID.text.toString(), binding.inputPw.text.toString(), selectedMbtiInt, selectedHobbyInt, selectedRegionInt)
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

    private fun convertMbtiStringToInt(mbtiString: String): Int {
        return when (mbtiString) {
            "mbti 선택" -> 0
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
            else -> 0 // Default value or handle unknown MBTI options
        }
    }

    private fun convertHobbyStringToInt(hobbyString: String): Int {
        return when (hobbyString) {
            "취미 선택" -> 0
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
            else -> 0 // Default value or handle unknown hobby options
        }
    }

    private fun convertRegionStringToInt(regionString: String): Int {
        return when (regionString) {
            "지역 선택" -> 0
            "서울특별시" -> 1
            "인천광역시" -> 2
            "부산광역시" -> 3
            "대구광역시" -> 4
            "대전광역시" -> 5
            "광주광역시" -> 6
            "울산광역시" -> 7
            "경기도" -> 8
            "경상북도" -> 9
            "경상남도" -> 10
            "충청북도" -> 11
            "충청남도" -> 12
            "전라북도" -> 13
            "전라남도" -> 14
            "강원도" -> 15
            "제주특별자치도" -> 16
            "세종특별자치도" -> 17
            else -> 0 // Default value or handle unknown region options
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
        }
    }

}
