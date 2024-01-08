package com.example.madcamp_week2_kjy_peb

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_week2_kjy_peb.databinding.ActivitySecondBinding

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding
    private lateinit var token: String
    val api = RetroInterface.create()

    private val mbtiOptions = arrayOf("mbti 선택", "ESTJ", "ESTP", "ESFJ", "ESFP", "ENTJ", "ENTP", "ENFJ", "ENFP", "ISTJ", "ISTP", "ISFJ", "ISFP", "INTJ", "INTP", "INFJ", "INFP")
    private val hobbyOptions = arrayOf("취미 선택", "게임", "독서", "댄스", "등산", "만화/애니메이션 감상", "미술/그림 그리기", "악기 연주", "음악 감상", "여행", "영화 감상", "요리", "보드 게임", "산책", "스포츠 관람", "프로그래밍/코딩", "헬스/운동")
    private val regionOptions = arrayOf("지역 선택", "서울특별시", "인천광역시", "부산광역시", "대구광역시", "대전광역시", "광주광역시", "울산광역시", "경기도", "경상북도", "경상남도", "충청북도", "충청남도", "전라북도", "전라남도", "강원도", "제주특별자치도", "세종특별자치도")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val id = intent.getStringExtra("id")

        token = intent.getStringExtra("token") ?: ""
        binding.textView.text = "$id 님,"

        binding.myInfoButton.setOnClickListener {
            // 토큰을 이용하여 사용자 정보를 요청
            getUserInfo()
        }


        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mbtiOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.mbtiSpinner2.adapter = adapter

        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, hobbyOptions)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.hobbySpinner2.adapter = adapter1

        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, regionOptions)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.regionSpinner2.adapter = adapter2

        binding.matchButton.setOnClickListener {
            binding.apply {

            val selectedMbtiString = mbtiSpinner2.selectedItem.toString()
                val selectedHobbyString = hobbySpinner2.selectedItem.toString()
                val selectedRegionString = regionSpinner2.selectedItem.toString()


            val selectedMbtiInt = convertMbtiStringToInt(selectedMbtiString)
                val selectedHobbyInt = convertHobbyStringToInt(selectedHobbyString)
                val selectedRegionInt = convertRegionStringToInt(selectedRegionString)

            // 서버로 MBTI 전송 및 매칭된 사용자 정보 가져오기
            getMatchedUsers(selectedMbtiInt, selectedHobbyInt, selectedRegionInt)
            }
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
        val dialogView: View = View.inflate(this, R.layout.profile_image, null)
        val ivPic: ImageView = dialogView.findViewById(R.id.imaged)
        try{
            val imgpath = cacheDir.toString()+"/"+"osz.png${userInfo.users_id}"
            val bm: Bitmap = BitmapFactory.decodeFile(imgpath)
            ivPic.setImageBitmap(bm)
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "파일 로드 실패", Toast.LENGTH_SHORT).show()
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle("사용자 정보")
            .setView(dialogView)
            .setMessage("ID: ${userInfo.users_id}\nPW: ${userInfo.users_pw}\nUID: ${userInfo.UID}\nMBTI: ${userInfo.users_mbti}\nHOBBY: ${userInfo.users_hobby}\nREGION: ${userInfo.users_region}")
            .setPositiveButton("확인", null)
            .create()

        dialog.show()
    }

    private fun getMatchedUsers(selectedMbti: Int, selectedHobby: Int, selectedRegion: Int) {
        api.matchedUser(selectedMbti, selectedHobby, selectedRegion).enqueue(object : Callback<ArrayList<User>> {
            override fun onResponse(call: Call<ArrayList<User>>, response: Response<ArrayList<User>>) {
                if (response.isSuccessful) {
                    val matchedUsers = response.body()
                    if (!matchedUsers.isNullOrEmpty()) {
                        // 매칭된 사용자 정보를 다이얼로그로 표시
                        showMatchedUsersDialog(matchedUsers)
                    } else {
                        Toast.makeText(applicationContext, "매칭된 사용자가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "매칭된 사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<User>>, t: Throwable) {
                Log.d("testt", t.message.toString())
            }
        })
    }

    private fun showMatchedUsersDialog(matchedUsers: ArrayList<User>) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_matched_users, null)

        // RecyclerView 설정
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.matchedUsersRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MatchedUsersAdapter(matchedUsers)
        recyclerView.adapter = adapter

        // 다이얼로그 생성
        val dialog = AlertDialog.Builder(this)
            .setTitle("매칭된 사용자 목록")
            .setView(dialogView)
            .setPositiveButton("확인", null)
            .create()

        dialog.show()
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
}