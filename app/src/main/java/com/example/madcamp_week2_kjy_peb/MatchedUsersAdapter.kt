package com.example.madcamp_week2_kjy_peb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MatchedUsersAdapter(private val matchedUsers: ArrayList<User>) :
    RecyclerView.Adapter<MatchedUsersAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userIdTextView: TextView = itemView.findViewById(R.id.matchedUserIdTextView)
        val mbtiTextView: TextView = itemView.findViewById(R.id.matchedMbtiTextView)
        val hobbyTextView: TextView = itemView.findViewById(R.id.matchedHobbyTextView)
        val regionTextView: TextView = itemView.findViewById(R.id.matchedRegionTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_matched_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = matchedUsers[position]

        holder.userIdTextView.text = "ID: ${user.users_id}"
        holder.mbtiTextView.text = "MBTI: ${user.users_mbti}"
        holder.hobbyTextView.text = "Hobby: ${user.users_hobby}"
        holder.regionTextView.text = "Region: ${user.users_region}"
    }

    override fun getItemCount(): Int {
        return matchedUsers.size
    }
}