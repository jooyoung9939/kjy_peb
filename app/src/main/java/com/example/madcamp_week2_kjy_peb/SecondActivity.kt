package com.example.madcamp_week2_kjy_peb

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_week2_kjy_peb.databinding.ActivitySecondBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SecondActivity : Fragment() {
    private lateinit var binding: ActivitySecondBinding
    private val api = RetroInterface.create()

    private val mbtiOptions = arrayOf("mbti 선택", "ESTJ", "ESTP", "ESFJ", "ESFP", "ENTJ", "ENTP", "ENFJ", "ENFP", "ISTJ", "ISTP", "ISFJ", "ISFP", "INTJ", "INTP", "INFJ", "INFP")
    private val hobbyOptions = arrayOf("취미 선택", "게임", "독서", "댄스", "등산", "만화/애니메이션 감상", "미술/그림 그리기", "악기 연주", "음악 감상", "여행", "영화 감상", "요리", "보드 게임", "산책", "스포츠 관람", "프로그래밍/코딩", "헬스/운동")


    companion object {
        private const val ARG_ID  = "id"
        private const val ARG_TOKEN  = "token"

        // newInstance 메서드를 통해 매개변수 전달
        fun newInstance(id: String, token: String): SecondActivity {
            val fragment = SecondActivity()
            val args = Bundle()
            args.putString(ARG_ID, id)
            args.putString(ARG_TOKEN , token)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivitySecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textView.text = "${arguments?.getString(ARG_ID)} 님,"

        binding.myInfoButton.setOnClickListener {
            // 토큰을 이용하여 사용자 정보를 요청
            getUserInfo()
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mbtiOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.mbtiSpinner2.adapter = adapter

        val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, hobbyOptions)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.hobbySpinner2.adapter = adapter1

        binding.matchButton.setOnClickListener {
            binding.apply {
                api.getMyInfo("Bearer ${arguments?.getString(ARG_TOKEN)}").enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            val userInfo = response.body()
                            if (userInfo != null) {
                                // 사용자 정보를 받아와서 처리 (예: AlertDialog로 표시)
                                val selectedRegionString = userInfo.users_region
                                Log.d("whu", selectedRegionString)
                                val selectedMbtiString = mbtiSpinner2.selectedItem.toString()
                                val selectedHobbyString = hobbySpinner2.selectedItem.toString()

                                val selectedMbtiInt = convertMbtiStringToInt(selectedMbtiString)
                                val selectedHobbyInt = convertHobbyStringToInt(selectedHobbyString)

                                Log.e("여기서도?", "region string : $selectedRegionString")
                                // 서버로 MBTI 전송 및 매칭된 사용자 정보 가져오기
                                getMatchedUsers(selectedMbtiInt, selectedHobbyInt, selectedRegionString)
                            } else {
                                Toast.makeText(requireContext(), "사용자 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Log.d("testt", t.message.toString())
                    }
                })
            }
        }
    }

    private fun getUserInfo() {
        api.getMyInfo("Bearer ${arguments?.getString(ARG_TOKEN)}").enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val userInfo = response.body()
                    if (userInfo != null) {
                        // 사용자 정보를 받아와서 처리 (예: AlertDialog로 표시)
                        showUserInfoDialog(userInfo)
                    } else {
                        Toast.makeText(requireContext(), "사용자 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.d("testt", t.message.toString())
            }
        })
    }

    private fun showUserInfoDialog(userInfo: User) {
        val dialogView: View = View.inflate(requireContext(), R.layout.profile_image, null)
        val imaged: ImageView = dialogView.findViewById(R.id.imaged)
        try {
            var img_path: String = requireContext().cacheDir.toString() + "/" + "osz.png" + userInfo.users_id // 내부 저장소에 저장되어 있는 이미지 경로
            var bm = BitmapFactory.decodeFile(img_path)
            imaged.setImageBitmap(bm) // 내부 저장소에 저장된 이미지를 이미지뷰에 셋
        } catch (e: java.lang.Exception) {
            Toast.makeText(requireContext(), "파일 로드 실패", Toast.LENGTH_SHORT).show()
        }
        dialogView.layoutParams = ViewGroup.LayoutParams(
            resources.getDimensionPixelSize(R.dimen.profile_dialog_width),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val userIdTextView: TextView = dialogView.findViewById(R.id.userIdTextView)
        val mbtiTextView: TextView = dialogView.findViewById(R.id.mbtiTextView)
        val hobbyTextView: TextView = dialogView.findViewById(R.id.hobbyTextView)
        val regionTextView: TextView = dialogView.findViewById(R.id.regionTextView)

        // 설정된 데이터로 TextView 업데이트
        userIdTextView.text = "ID: ${userInfo.users_id}"
        mbtiTextView.text = "MBTI: ${userInfo.users_mbti}"
        hobbyTextView.text = "Hobby: ${userInfo.users_hobby}"
        regionTextView.text = "Region: ${userInfo.users_region}"

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("사용자 정보")
            .setView(dialogView)
            .setPositiveButton("확인", null)
            .setNegativeButton("내 정보 수정") { _, _ ->
                showEditDialog()
            }
            .create()

        dialog.show()
    }

    private fun showEditDialog() {
        val intent = Intent(requireContext(), EditUserInfoActivity::class.java)
        intent.putExtra("token", arguments?.getString(ARG_TOKEN))
        startActivity(intent)
    }




    private fun getMatchedUsers(selectedMbti: Int, selectedHobby: Int, selectedRegion: String) {
        Log.e("지역 정보 넘어왔는지", "selectedRegion = $selectedRegion")

        api.matchedUser(selectedMbti, selectedHobby, selectedRegion).enqueue(object : Callback<ArrayList<User>> {
            override fun onResponse(call: Call<ArrayList<User>>, response: Response<ArrayList<User>>) {
                if (response.isSuccessful) {
                    val matchedUsers = response.body()
                    if (!matchedUsers.isNullOrEmpty()) {
                        // 매칭된 사용자 정보를 다이얼로그로 표시
                        showMatchedUsersDialog(matchedUsers)
                    } else {
                        Toast.makeText(requireContext(), "매칭된 사용자가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "매칭된 사용자 정보를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
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
        recyclerView.layoutManager = LinearLayoutManager(requireContext()).also { it.orientation = LinearLayoutManager.HORIZONTAL }
        val adapter = MatchedUsersAdapter(matchedUsers)
        adapter.setOnButtonClickListener(object : MatchedUsersAdapter.OnButtonClickListener {
            override fun onButtonClick(position: Int, userId: String) {
                val intent = Intent(requireContext(), ChatRoomActivity::class.java)
                intent.putExtra("token", arguments?.getString(ARG_TOKEN))
                var room: String
                if (userId != null) {
                    // userId가 null이 아닌 경우 compareTo를 사용하여 비교
                    room = if (userId.compareTo(arguments?.getString(ARG_ID) ?: "") > 0) {
                        "$userId" + "_${arguments?.getString(ARG_ID)}"
                    } else {
                        "${arguments?.getString(ARG_ID)}_$userId"
                    }
                } else {
                    // userId가 null인 경우 처리
                    room = "${arguments?.getString(ARG_ID)}_${userId ?: ""}"
                }
                intent.putExtra("room", room)
                Log.e("cjattasdf", "WJU - $room")

                val participants = arrayListOf(userId, arguments?.getString(ARG_ID))
                intent.putExtra("participants", participants)
                Log.d("participants", participants.toString())

                startActivity(intent)
            }
        })
        recyclerView.adapter = adapter
        // 다이얼로그 생성
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("매칭된 사용자 목록")
            .setView(dialogView)
            .setPositiveButton("확인", null)
            .create()

        dialog.show()
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

}
