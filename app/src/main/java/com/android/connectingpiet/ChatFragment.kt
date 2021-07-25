package com.android.connectingpiet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatFragment : Fragment() {

    private lateinit var chatRecyclerView: RecyclerView
    private var chatroomID: String? = null

    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = this.arguments

        if (bundle != null) {
            chatroomID = bundle.getString("ChatroomID")
        }

        chatRecyclerView = view.findViewById(R.id.chat_rv)

        setUpRecyclerView()

        // Example of using generics
        /*val test = Test()
        val example = Example<Test>(test)*/

        val enterMessage: EditText = view.findViewById(R.id.enter_message)
        val sendMessage: ImageView = view.findViewById(R.id.send_message)

        sendMessage.setOnClickListener {
            if (enterMessage.text.isBlank()) {
                return@setOnClickListener
            }

            val chatText = enterMessage.text.toString()

            val firestore = FirebaseFirestore.getInstance().collection("Chatrooms").document(chatroomID.toString())
                    .collection("Messages")

            val chat = Chat(chatText, UserUtils.user!!, System.currentTimeMillis(), chatroomID.toString())

            firestore.document().set(chat).addOnCompleteListener {
                if (it.isSuccessful) {
                    chatRecyclerView.scrollToPosition(chatRecyclerView.adapter?.itemCount!! - 1)
                    enterMessage.text.clear()
                } else {
                    Toast.makeText(context, it.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        val firestore = FirebaseFirestore.getInstance()
        val query = firestore.collection("Chatrooms").document(chatroomID.toString()).collection("Messages")
                .orderBy("time", Query.Direction.ASCENDING)

        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Chat>().setQuery(query, Chat::class.java).build()

        chatAdapter = ChatAdapter(recyclerViewOptions)

        chatRecyclerView.adapter = chatAdapter
        chatRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun onStart() {
        super.onStart()
        chatAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        chatAdapter.stopListening()
    }
}
