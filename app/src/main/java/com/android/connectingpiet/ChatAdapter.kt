package com.android.connectingpiet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ChatAdapter(options: FirestoreRecyclerOptions<Chat>): FirestoreRecyclerAdapter<Chat, ChatAdapter.ChatViewHolder>(options) {

    companion object {
        const val MSG_BY_SELF = 0
        const val MSG_BY_OTHER = 1
    }

    class ChatViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val chatText: TextView = itemView.findViewById(R.id.chat_text)
        val authorName: TextView = itemView.findViewById(R.id.chat_author)
    }

    override fun getItemViewType(position: Int): Int {
        if (getItem(position).author.id == UserUtils.user?.id) {
            return MSG_BY_SELF
        } else {
            return MSG_BY_OTHER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        var view: View? = null

        if (viewType == MSG_BY_SELF) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_self_chat, parent, false)
        } else {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_other_chat, parent, false)
        }

        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: Chat) {
        holder.chatText.text = model.text
        holder.authorName.text = model.author.name
    }
}