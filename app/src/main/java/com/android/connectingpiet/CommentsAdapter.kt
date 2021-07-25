package com.android.connectingpiet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.thunder413.datetimeutils.DateTimeStyle
import com.github.thunder413.datetimeutils.DateTimeUtils

class CommentsAdapter(options: FirestoreRecyclerOptions<Comment>): FirestoreRecyclerAdapter<Comment, CommentsAdapter.CommentViewHolder>(options) {

    class CommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.comment_text)
        val author: TextView = itemView.findViewById(R.id.comment_author)
        val time: TextView = itemView.findViewById(R.id.comment_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int, model: Comment) {
        holder.text.text = model.text
        holder.author.text = model.author.name

        val date = DateTimeUtils.formatDate(model.time)
        val dateFormatted = DateTimeUtils.formatWithStyle(date, DateTimeStyle.LONG)

        holder.time.text = dateFormatted
    }
}