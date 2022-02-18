package com.example.trackerapp.presentation.fragment.walk_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trackerapp.R
import com.example.trackerapp.databinding.FragmentWalkListBinding
import com.example.trackerapp.presentation.base.BaseFragment
import com.example.trackerapp.presentation.fragment.tracker.TrackerFragment
import com.example.trackerapp.utils.ImageManager
import javax.inject.Inject
import kotlin.reflect.KClass

class WalkListFragment : BaseFragment<
        WalkListViewModel,
        WalkListViewModel.Factory,
        FragmentWalkListBinding
        >() {

    private lateinit var adapter: WalksAdapter
    override val viewModelClass: KClass<WalkListViewModel> = WalkListViewModel::class
    @Inject
    lateinit var imageManager: ImageManager

    override fun createViewBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): FragmentWalkListBinding = FragmentWalkListBinding.inflate(inflater, parent, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setClickListeners()
        setObserve()
        viewModel.requestList()
    }

    private fun setAdapter() {
        val recyclerView = binding.walkListRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = WalksAdapter(imageManager)
        recyclerView.adapter = adapter
    }

    private fun setClickListeners() {
        binding.startNewWalkButton.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<TrackerFragment>(R.id.fragmentContainer)
                addToBackStack(null)
            }
        }

        binding.retryButton.setOnClickListener {
            viewModel.requestList()
        }
    }

    private fun setObserve() {
        viewModel.showEmptyState.observe(viewLifecycleOwner) { isEmptyStateVisible ->
            showEmptyState(isEmptyStateVisible)
        }

        viewModel.showErrorState.observe(viewLifecycleOwner) { isErrorStateVisible ->
            showErrorState(isErrorStateVisible)
        }

        viewModel.showLoader.observe(viewLifecycleOwner) { isLoaderVisible ->
            showLoader(isLoaderVisible)
        }

        viewModel.walksList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun showEmptyState(isEmptyStateVisible: Boolean) {
        binding.emptyStateTextView.visibility = if (isEmptyStateVisible) View.VISIBLE else View.GONE
    }

    private fun showErrorState(isErrorStateVisible: Boolean) {
        binding.errorStateLayout.visibility = if (isErrorStateVisible) View.VISIBLE else View.GONE
    }

    private fun showLoader(isLoaderVisible: Boolean) {
        binding.progressBarLayout.visibility = if (isLoaderVisible) View.VISIBLE else View.GONE
    }
}