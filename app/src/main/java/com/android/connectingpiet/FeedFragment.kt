package com.android.connectingpiet

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class FeedFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FeedAdapter

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab: FloatingActionButton = view.findViewById(R.id.create_post)

        fab.setOnClickListener {
            val intent = Intent(activity, CreatePostActivity::class.java)
            startActivity(intent)
        }

        recyclerView = view.findViewById(R.id.feed_recycler_view)

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val firestore = FirebaseFirestore.getInstance()
        val query = firestore.collection("Posts")

        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        context?.let {
            adapter = FeedAdapter(recyclerViewOptions, it)
        }

        if (this::adapter.isInitialized) {
            recyclerView.adapter = adapter
        }

        recyclerView.layoutManager = LinearLayoutManager(activity)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}