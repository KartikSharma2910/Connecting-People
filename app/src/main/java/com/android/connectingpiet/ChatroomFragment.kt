package com.android.connectingpiet

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.ConnectingYou.ChatroomAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatroomFragment : Fragment() {

    private lateinit var chatroomRecyclerView: RecyclerView
    private var chatroomAdapter: ChatroomAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chatroom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatroomRecyclerView = view.findViewById(R.id.chatrooms_rv)

        setUpRecyclerView()

        val createChatroom: FloatingActionButton = view.findViewById(R.id.create_chatroom)

        createChatroom.setOnClickListener {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)

            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            val editText = EditText(context)
            editText.layoutParams = params
            editText.setPadding(0, 20, 0, 20) // TODO: We'll have to convert dp to pixels

            alertDialog.setTitle("Create Chatroom")
            alertDialog.setMessage("Enter the name of the new chatroom that you want to create:")

            alertDialog.setView(editText)

            // Way to create views programmatically
            /* val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            params.marginStart = dpToPixels(16, context) // Implement dpToPixels method
            val textView = TextView(context)
            textView.layoutParams = params
            parentView.setView(textView) // parentView is defined earlier */

            var textEntered = ""

            alertDialog.setPositiveButton("Create") { _, _ ->
                textEntered = editText.text.toString()
                val document = FirebaseFirestore.getInstance().collection("Chatrooms").document()
                val chatroom = Chatroom(textEntered, document.id)
                document.set(chatroom)
            }

            alertDialog.setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }

            alertDialog.show()
        }
    }

    private fun setUpRecyclerView() {
        val firestore = FirebaseFirestore.getInstance()
        val query = firestore.collection("Chatrooms").orderBy("name", Query.Direction.ASCENDING)

        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Chatroom>().setQuery(query, Chatroom::class.java).build()

        chatroomAdapter = activity?.let {
            ChatroomAdapter(recyclerViewOptions, it)
        }

        chatroomRecyclerView.adapter = chatroomAdapter
        chatroomRecyclerView.layoutManager = LinearLayoutManager(activity)
    }

    override fun onStart() {
        super.onStart()
        chatroomAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        chatroomAdapter?.stopListening()
    }
}