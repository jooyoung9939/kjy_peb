package com.example.madcamp_week2_kjy_peb

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatListFragment : Fragment() {
    private val api = RetroInterface.create()

    companion object {
        private const val ARG_ID = "id"
        private const val ARG_TOKEN = "token"

        // newInstance 메서드를 통해 매개변수 전달
        fun newInstance(id: String, token: String): ChatListFragment {
            val fragment = ChatListFragment()
            val args = Bundle()
            args.putString(ARG_ID, id)
            args.putString(ARG_TOKEN, token)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_chatlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 초기화
        val chatRecyclerView = view.findViewById<RecyclerView>(R.id.chatRecyclerView)
        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // TODO: 서버에서 채팅 데이터를 받아와서 RecyclerView에 표시
        val token = arguments?.getString(ARG_TOKEN)
        if (token != null) {
            loadChatData(token)
        }
    }

    private fun loadChatData(token: String) {
        val id = arguments?.getString(ARG_ID)
        if (id != null) {
            api.getChats("Bearer $token").enqueue(object : Callback<List<ChatModel>> {
                override fun onResponse(call: Call<List<ChatModel>>, response: Response<List<ChatModel>>) {
                    if (response.isSuccessful) {
                        val chats = response.body()
                        if (chats != null) {
                            displayChats(chats)
                        } else {
                            // 서버로부터 받은 채팅 데이터가 null인 경우 처리
                            Log.e("ChatListFragment", "Failed to get chat data from the server")
                        }
                    } else {
                        // 서버 응답이 실패한 경우 처리
                        Log.e("ChatListFragment", "Failed to get chat data from the server. Error code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<ChatModel>>, t: Throwable) {
                    // 통신 실패 시 처리
                    Log.e("ChatListFragment", "Failed to communicate with the server. Error message: ${t.message}")
                }
            })
        }
    }

    private fun displayChats(chats: List<ChatModel>) {
        val chatRecyclerView = requireView().findViewById<RecyclerView>(R.id.chatRecyclerView)
        val adapter = ChatListAdapter(chats)
        chatRecyclerView.adapter = adapter
    }

    // ChatListAdapter 클래스 추가
    private inner class ChatListAdapter(private val chats: List<ChatModel>) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chatlist, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val chat = chats[position]
            // Chat 데이터를 ViewHolder에 바인딩
            holder.chatList_You_Name.text = chat.roomName
            holder.chatList_Text.text = chat.script
            holder.chatList_Time.text = chat.date_time
        }

        override fun getItemCount(): Int = chats.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            // ViewHolder에서 사용할 View들 정의
            val chatList_You_Name = itemView.findViewById<TextView>(R.id.chatList_You_Name)
            val chatList_Text = itemView.findViewById<TextView>(R.id.chatList_Text)
            val chatList_Time = itemView.findViewById<TextView>(R.id.chatList_Time)
        }
    }
}

