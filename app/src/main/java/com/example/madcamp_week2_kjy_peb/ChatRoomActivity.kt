package com.example.madcamp_week2_kjy_peb

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date


class ChatRoomActivity : AppCompatActivity() {
    private lateinit var chating_Text: EditText
    private lateinit var chat_Send_Button: Button
    private lateinit var chat_recyclerview: RecyclerView
    private lateinit var token: String
    private lateinit var room: String
    private lateinit var participants: ArrayList<String>

    var users_id: String = ""
    val api = RetroInterface.create()

    private var hasConnection: Boolean = false
    private var thread2: Thread? = null
    private var startTyping = false
    private var time = 2

    private var mSocket: Socket = IO.socket("http://143.248.232.211:3000")

    var arrayList = arrayListOf<ChatModel>()
    val mAdapter: ChatAdapter = ChatAdapter(this, arrayList)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)

        token = intent.getStringExtra("token") ?: ""
        room = intent.getStringExtra("room") ?: ""
        participants = intent.getStringArrayListExtra("participants") ?: arrayListOf()

        if (participants != null) {
            Log.d("participants", participants.toString())
        } else {
            Log.e("participants", "participants is null")
        }

        Log.d("ChatRoomActivity", "onCreate: room - $room")
        api.getMyInfo("Bearer $token").enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val userInfo = response.body()
                    if (userInfo != null) {
                        users_id = userInfo.users_id
                        mAdapter.setUserId(users_id)
                        mAdapter.notifyDataSetChanged()
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

        chat_recyclerview = findViewById(R.id.chat_recyclerview)
        chat_recyclerview.adapter = mAdapter
        val lm = LinearLayoutManager(this)
        chat_recyclerview.layoutManager = lm
        chat_recyclerview.setHasFixedSize(true)
        chat_Send_Button = findViewById(R.id.chat_Send_Button)
        chating_Text = findViewById(R.id.chating_Text)

        if(savedInstanceState != null){
            hasConnection = savedInstanceState.getBoolean("hasConnection")
        }
        if(hasConnection){

        } else{
            mSocket.connect()
            mSocket.on("connect user", onNewUser)
            mSocket.on("chat message", onNewMessage)
            mSocket.on(Socket.EVENT_CONNECT) {
                Log.d("Socket", "Connected")
            }

            mSocket.on(Socket.EVENT_CONNECT_ERROR) { args ->
                val e = args[0] as Exception
                Log.e("Socket", "Connect error: ${e.message}")
            }

            val userId = JSONObject()
            try{
                userId.put("username", users_id+"Connected")
                userId.put("roomName", room)
                Log.e("username", users_id+"Connected")

                mSocket.emit("connect user", userId)
            } catch (e: JSONException){
                e.printStackTrace()
            }

        }

        hasConnection = true

        chat_Send_Button.setOnClickListener{
            sendMessage()
        }
    }

    internal var onNewMessage: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val name: String
            val script: String
            val profile_image: String
            val date_time: String
            val roomName: String
            try{
                Log.e("asdasd", data.toString())
                name = data.getString("name")
                script = data.getString("script")
                profile_image = data.getString("profile_image")
                date_time = data.getString("date_time")
                roomName = data.getString("roomName")
                Log.e("ReceivedMessage", "Name: $name, Script: $script, Profile Image: $profile_image, Date Time: $date_time")
                val format = ChatModel(name, script, profile_image, date_time, roomName)
                mAdapter.addItem(format)
                mAdapter.notifyDataSetChanged()
                Log.e("new me", name)
            } catch (e: Exception) {
                return@Runnable
            }
        })
    }

    internal var onNewUser: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread(Runnable {
            val length = args.size

            if(length == 0){
                return@Runnable
            }
            var username = args[0].toString()
            try{
                val `object` = JSONObject(username)
                username = `object`.getString("username")
            } catch (e:JSONException){
                e.printStackTrace()
            }
        })
    }

    fun sendMessage(){
        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyyy-MM-dd\nhh:mm:ss")
        val getTime = sdf.format(date)
        val message = chating_Text.text.toString().trim({it<=' '})
        if(TextUtils.isEmpty(message)){
            return
        }
        Log.d("ChatRoomActivity", "sendMessage: Message - $message")
        chating_Text.setText("")
        Log.d("ChatRoomActivity", "sendMessage: room - $room")
        val jsonObject = JSONObject()
        try{
            jsonObject.put("name", users_id)
            jsonObject.put("script", message)
            jsonObject.put("profile_image", "example")
            jsonObject.put("date_time", getTime)
            jsonObject.put("roomName", room)
        } catch (e:JSONException){
            e.printStackTrace()
        }
        Log.e("챗룸", "sendMessage: Message sent")
        mSocket.emit("chat message", jsonObject)
        Log.e("sendmmm", users_id)
    }
    class ChatAdapter(val context: Context, val arrayList: ArrayList<ChatModel>):
        RecyclerView.Adapter<RecyclerView.ViewHolder> (){
        private var users_id: String? = null
        fun setUserId(users_id: String){
            this.users_id = users_id
        }
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
                try {
                    var img_path: String = context.cacheDir.absolutePath + "/" + "osz.png"+arrayList.get(i).name // 내부 저장소에 저장되어 있는 이미지 경로
                    var bm = BitmapFactory.decodeFile(img_path)
                    (viewHolder as Holder2).chat_You_Image?.setImageBitmap(bm) // 내부 저장소에 저장된 이미지를 이미지뷰에 셋
                } catch (e: java.lang.Exception) {
                }
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
            return if (arrayList.get(position).name == users_id) {
                1
            } else {
                2
            }
        }
    }
}