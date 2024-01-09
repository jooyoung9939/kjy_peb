package com.example.madcamp_week2_kjy_peb

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import android.view.View
import android.widget.Toast

class EditUserInfoActivity : AppCompatActivity() {
    private lateinit var token: String
    val api = RetroInterface.create()

    private lateinit var etNewPassword: EditText
    private lateinit var mbtiSpinner: Spinner
    private lateinit var hobbySpinner: Spinner
    private lateinit var regionSpinner: Spinner
    private lateinit var btnSave: Button

    private val mbtiOptions = arrayOf("mbti 선택", "ESTJ", "ESTP", "ESFJ", "ESFP", "ENTJ", "ENTP", "ENFJ", "ENFP", "ISTJ", "ISTP", "ISFJ", "ISFP", "INTJ", "INTP", "INFJ", "INFP")
    private val hobbyOptions = arrayOf("취미 선택", "게임", "독서", "댄스", "등산", "만화/애니메이션 감상", "미술/그림 그리기", "악기 연주", "음악 감상", "여행", "영화 감상", "요리", "보드 게임", "산책", "스포츠 관람", "프로그래밍/코딩", "헬스/운동")
    private val regionOptions = arrayOf("지역 선택", "서울특별시", "인천광역시", "부산광역시", "대구광역시", "대전광역시", "광주광역시", "울산광역시", "경기도", "경상북도", "경상남도", "충청북도", "충청남도", "전라북도", "전라남도", "강원도", "제주특별자치도", "세종특별자치도")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_info)

        token = intent.getStringExtra("token") ?: ""

        etNewPassword = findViewById(R.id.etNewPassword)
        mbtiSpinner = findViewById(R.id.editMbtiSpinner)
        hobbySpinner = findViewById(R.id.editHobbySpinner)
        regionSpinner = findViewById(R.id.editRegionSpinner)
        btnSave = findViewById(R.id.btnSave)

        // Set up spinners with options
        val mbtiAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mbtiOptions)
        mbtiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mbtiSpinner.adapter = mbtiAdapter

        val hobbyAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, hobbyOptions)
        hobbyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        hobbySpinner.adapter = hobbyAdapter

        val regionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, regionOptions)
        regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        regionSpinner.adapter = regionAdapter

        btnSave.setOnClickListener {
            saveUserInfo()
        }
    }

    private fun saveUserInfo() {
        val newPassword = etNewPassword.text.toString()
        val newMbti = mbtiSpinner.selectedItem.toString()
        val newHobby = hobbySpinner.selectedItem.toString()
        val newRegion = regionSpinner.selectedItem.toString()

        val newMbtiInt = convertMbtiStringToInt(newMbti)
        val newHobbyInt = convertHobbyStringToInt(newHobby)
        val newRegionInt = convertRegionStringToInt(newRegion)

        // 서버에 정보를 전송하는 메소드 호출
        editMyInfo(newPassword, newMbtiInt, newHobbyInt, newRegionInt)
    }

    private fun editMyInfo(newPassword: String, newMbti: Int, newHobby: Int, newRegion: Int) {
        val editModel = EditModel(newPassword, newMbti, newHobby, newRegion)

        val call: Call<EditResult> = api.edit_my_info("Bearer $token", editModel)

        call.enqueue(object : Callback<EditResult> {
            override fun onResponse(call: Call<EditResult>, response: Response<EditResult>) {
                if (response.isSuccessful) {
                    val editResult: EditResult? = response.body()
                    if (editResult != null) {
                        // 성공적인 응답 처리
                        Toast.makeText(applicationContext, "정보가 수정되었습니다.", Toast.LENGTH_SHORT)
                            .show()
                        finish() // 수정 완료 후 액티비티 종료
                    } else {
                        // 응답이 null인 경우 또는 필요한 정보가 없는 경우에 대한 처리
                        Toast.makeText(applicationContext, "서버 응답이 올바르지 않습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    // 서버 응답이 실패한 경우에 대한 처리
                    Toast.makeText(applicationContext, "서버 응답 실패", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EditResult>, t: Throwable) {
                // 통신 실패 시에 대한 처리
                Log.e("editMyInfo", "통신 실패: ${t.message}")
                Toast.makeText(applicationContext, "통신 실패", Toast.LENGTH_SHORT).show()
            }
        })
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

    private fun convertRegionStringToInt(regionString: String): Int {
        return when (regionString) {
            "지역 선택" -> 18
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
            else -> 18 // Default value or handle unknown region options
        }
    }
}