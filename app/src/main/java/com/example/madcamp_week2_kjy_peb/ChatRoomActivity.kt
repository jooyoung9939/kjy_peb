package com.example.madcamp_week2_kjy_peb

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date


class ChatRoomActivity : AppCompatActivity() {
    private lateinit var chating_Text: EditText
    private lateinit var chat_Send_Button: Button
    private lateinit var chat_recyclerview: RecyclerView
    var arrayList = arrayListOf<ChatModel>()
    val mAdapter: ChatAdapter = ChatAdapter(this, arrayList)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        chat_recyclerview = findViewById(R.id.chat_recyclerview)
        chat_recyclerview.adapter = mAdapter
        val lm = LinearLayoutManager(this)
        chat_recyclerview.layoutManager = lm
        chat_recyclerview.setHasFixedSize(true)
        chat_Send_Button = findViewById(R.id.chat_Send_Button)
        chating_Text = findViewById(R.id.chating_Text)

        chat_Send_Button.setOnClickListener{
            sendMessage()
        }
    }

    fun sendMessage(){
        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val getTime = sdf.format(date)
        val item = ChatModel("users_id", chating_Text.text.toString(), "example", getTime)
        mAdapter.addItem(item)
        mAdapter.notifyDataSetChanged()
        chating_Text.setText("")
    }
    class ChatAdapter(val context: Context, val arrayList: ArrayList<ChatModel>):
        RecyclerView.Adapter<RecyclerView.ViewHolder> (){


        fun addItem(item: ChatModel) {//아이템 추가
            if (arrayList != null) {
                arrayList.add(item)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view: View
            //getItemViewType 에서 뷰타입 1을 리턴받았다면 내채팅레이아웃을 받은 Holder를 리턴
            if(viewType == 1){
                view = LayoutInflater.from(context).inflate(R.layout.item_my_chat, parent, false)
                return Holder(view)
            }
            //getItemViewType 에서 뷰타입 2을 리턴받았다면 상대채팅레이아웃을 받은 Holder2를 리턴
            else{
                view = LayoutInflater.from(context).inflate(R.layout.item_your_chat, parent, false)
                return Holder2(view)
            }
        }

        override fun getItemCount(): Int {
            return arrayList.size

        }

        override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
            //onCreateViewHolder에서 리턴받은 뷰홀더가 Holder라면 내채팅, item_my_chat의 뷰들을 초기화 해줌
            if (viewHolder is Holder) {
                (viewHolder as Holder).chat_Text?.setText(arrayList.get(i).script)
                (viewHolder as Holder).chat_Time?.setText(arrayList.get(i).date_time)
            }
            //onCreateViewHolder에서 리턴받은 뷰홀더가 Holder2라면 상대의 채팅, item_your_chat의 뷰들을 초기화 해줌
            else if(viewHolder is Holder2) {
                (viewHolder as Holder2).chat_You_Image?.setImageResource(R.mipmap.ic_launcher)
                (viewHolder as Holder2).chat_You_Name?.setText(arrayList.get(i).name)
                (viewHolder as Holder2).chat_Text?.setText(arrayList.get(i).script)
                (viewHolder as Holder2).chat_Time?.setText(arrayList.get(i).date_time)
            }

        }

        //내가친 채팅 뷰홀더
        inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            //친구목록 모델의 변수들 정의하는부분
            val chat_Text = itemView?.findViewById<TextView>(R.id.chat_Text)
            val chat_Time = itemView?.findViewById<TextView>(R.id.chat_Time)
        }

        //상대가친 채팅 뷰홀더
        inner class Holder2(itemView: View) : RecyclerView.ViewHolder(itemView) {

            //친구목록 모델의 변수들 정의하는부분
            val chat_You_Image = itemView?.findViewById<ImageView>(R.id.chat_You_Image)
            val chat_You_Name = itemView?.findViewById<TextView>(R.id.chat_You_Name)
            val chat_Text = itemView?.findViewById<TextView>(R.id.chat_Text)
            val chat_Time = itemView?.findViewById<TextView>(R.id.chat_Time)


        }

        override fun getItemViewType(position: Int): Int {//여기서 뷰타입을 1, 2로 바꿔서 지정해줘야 내채팅 너채팅을 바꾸면서 쌓을 수 있음

            //내 아이디와 arraylist의 name이 같다면 내꺼 아니면 상대꺼
            return if (arrayList.get(position).name == "users_id") {
                1
            } else {
                2
            }
        }
    }
}