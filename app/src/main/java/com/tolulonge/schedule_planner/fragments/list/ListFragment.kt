package com.tolulonge.schedule_planner.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import com.tolulonge.schedule_planner.R
import com.tolulonge.schedule_planner.data.model.ToDoData
import com.tolulonge.schedule_planner.data.viewmodel.ToDoViewModel
import com.tolulonge.schedule_planner.databinding.FragmentListBinding
import com.tolulonge.schedule_planner.fragments.SharedViewModel
import com.tolulonge.schedule_planner.fragments.list.adapter.ListAdapter
import com.tolulonge.schedule_planner.utils.hideKeyboard
import com.tolulonge.schedule_planner.utils.observeOnce

class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels()

    private val mAdapter: ListAdapter by lazy { ListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mSharedViewModel = sharedViewModel
        setHasOptionsMenu(true)
        hideKeyboard(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listLayout.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_updateFragment)
        }
        setupRecyclerView()

        mToDoViewModel.getAllData.observe(viewLifecycleOwner) {
            sharedViewModel.checkIfDatabaseEmpty(it)
            mAdapter.setData(it)
            binding.recyclerView.scheduleLayoutAnimation()
        }

    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = mAdapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }.also { swipeToDelete(it) }
    }

    private fun swipeToDelete(recyclerView: RecyclerView){
        val swipeToDeleteCallback = object : SwipeToDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = mAdapter.dataList[viewHolder.adapterPosition]
                mToDoViewModel.deleteItem(deletedItem)
                mAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                restoreDeletedData(viewHolder.itemView, deletedItem)

            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    private fun restoreDeletedData(view: View, deletedItem: ToDoData){
        val snackBar = Snackbar.make(
            view,"Deleted '${deletedItem.title}'",Snackbar.LENGTH_LONG
        )
        snackBar.setAction("Undo"){
            mToDoViewModel.insertData(deletedItem)
        }
        snackBar.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_delete_all -> {
                confirmRemoval()
            }
            R.id.menu_priority_high -> {
                mToDoViewModel.sortByHighPriority.observe(viewLifecycleOwner){
                    mAdapter.setData(it)
                }
            }
            R.id.menu_priority_low -> {
                mToDoViewModel.sortByLowPriority.observe(viewLifecycleOwner){
                    mAdapter.setData(it)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun confirmRemoval() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_,_ ->
            mToDoViewModel.deleteAll()
            Toast.makeText(requireContext(), "Successfully Removed", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No"){_,_ ->}
        builder.setTitle("Delete Everything?")
        builder.setMessage("Are you sure you want to remove everything?")
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            searchThroughDatabase(it)
        }
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        p0?.let {
            searchThroughDatabase(it)
        }
        return true
    }

    private fun searchThroughDatabase(queryParam: String) {
        val searchQuery = "%$queryParam%"

        mToDoViewModel.searchDatabase(searchQuery).observeOnce(viewLifecycleOwner){list->
            list?.let {
                mAdapter.setData(it)
            }
        }
    }
}