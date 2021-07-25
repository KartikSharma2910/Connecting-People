package com.android.connectingpiet

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SearchFragment : Fragment() {

    private lateinit var searchAdapter: SearchAdapter
    private lateinit var searchRecyclerView: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = view.findViewById(R.id.search_toolbar)
        toolbar.title = "Search Users"

        (activity as? MainActivity)?.setSupportActionBar(toolbar)
        (activity as? MainActivity)?.supportActionBar?.show()

        setHasOptionsMenu(true)

        val firestore = FirebaseFirestore.getInstance()
        val query = firestore.collection("users").whereNotEqualTo("id",
                FirebaseAuth.getInstance().currentUser?.uid)

        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<User>().setQuery(query, User::class.java).build()

        // Read about pagination

        context?.let {
            searchAdapter = SearchAdapter(recyclerViewOptions, it)
        }

        searchRecyclerView = view.findViewById(R.id.search_rv)

        searchRecyclerView.adapter = searchAdapter
        searchRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.search_menu, menu)

        val searchView = SearchView(requireContext())
        menu.findItem(R.id.action_search).actionView = searchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchRecyclerView.visibility = View.VISIBLE

                val firestore = FirebaseFirestore.getInstance()
                val newQuery = firestore.collection("users").whereEqualTo("name", query)
                        .whereNotEqualTo("id", UserUtils.user?.id)

                val newRecyclerViewOptions = FirestoreRecyclerOptions.Builder<User>().setQuery(newQuery,
                        User::class.java).build()

                searchAdapter.updateOptions(newRecyclerViewOptions)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchRecyclerView.visibility = View.INVISIBLE
                return false
            }

        })
    }

    override fun onStart() {
        super.onStart()
        searchAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        searchAdapter.stopListening()
    }
}