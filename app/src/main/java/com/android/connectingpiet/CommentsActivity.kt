package com.android.connectingpiet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class CommentsActivity : AppCompatActivity() {
    private var commentsAdapter: CommentsAdapter? = null
    private lateinit var commentsRecyclerView: RecyclerView

    private var postId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        if (intent.hasExtra("PostID")) {
            postId = intent.getStringExtra("PostID")
        }

        commentsRecyclerView = findViewById(R.id.comments_rv)

        setUpRecyclerView()

        val commentEditText: EditText = findViewById(R.id.enter_comment)
        val sendIcon: ImageView = findViewById(R.id.send_comment)

        sendIcon.setOnClickListener {
            val commentText = commentEditText.text.toString()

            if (TextUtils.isEmpty(commentText)) {
                Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val firestore = FirebaseFirestore.getInstance()
            val comment = UserUtils.user?.let { user ->
                Comment(commentText, user, System.currentTimeMillis())
            }

            postId?.let { postId ->
                comment?.let { comment ->
                    firestore.collection("Posts").document(postId).collection("Comments")
                        .document().set(comment)
                }
            }

            commentEditText.text.clear()
        }
    }

    private fun setUpRecyclerView() {
        val firestore = FirebaseFirestore.getInstance()
        val query = postId?.let {
            firestore.collection("Posts").document(it).collection("Comments")
        }

        val recyclerViewOptions = query?.let {
            FirestoreRecyclerOptions.Builder<Comment>().setQuery(it, Comment::class.java).build()
        }

        commentsAdapter = recyclerViewOptions?.let {
            CommentsAdapter(it)
        }

        commentsRecyclerView.adapter = commentsAdapter
        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        commentsAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        commentsAdapter?.stopListening()
    }
}