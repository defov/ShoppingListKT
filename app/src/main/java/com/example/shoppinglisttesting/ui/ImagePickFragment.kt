package com.example.shoppinglisttesting.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shoppinglisttesting.R
import com.example.shoppinglisttesting.adapters.ImageAdapter
import com.example.shoppinglisttesting.other.Constants
import com.example.shoppinglisttesting.other.Status
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_image_pick.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class ImagePickFragment @Inject constructor(
    val imageAdapter: ImageAdapter
): Fragment(R.layout.fragment_image_pick) {

    lateinit var viewModel: ShoppingViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ShoppingViewModel::class.java)
        setupRecyclerView()
        subscribeToObservers()

        var job: Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(Constants.SEARCH_TIME_DELAY)
                editable?.let {
                    viewModel.searchForImage(it.toString())
                }
            }
        }

        imageAdapter.setOnItemClickListener {
            viewModel.setCurrentImageUrl(it)
            findNavController().popBackStack()
        }
    }

    override fun onDetach() {
        imageAdapter.images = listOf()
        super.onDetach()
    }

    private fun subscribeToObservers() {
        viewModel.images.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled()?.let { result ->
                when(result.status) {
                    Status.SUCCESS -> {
                        Log.d("TAG", "subscribeToObservers: opa")
                        val urls = result.data?.hits?.map { imageResult -> imageResult.previewURL }
                        imageAdapter.images = urls ?: listOf()
                        progressBar.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        Snackbar.make(
                            requireActivity().rootLayout,
                            result.message ?: "An unknown error occured.",
                            Snackbar.LENGTH_LONG
                        ).show()
                        progressBar.visibility = View.GONE
                    }
                    Status.LOADING -> {
                        progressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun setupRecyclerView() {
        rvImages.apply {
            adapter = imageAdapter
            layoutManager = GridLayoutManager(requireContext(), Constants.GRID_SPAN_COUNT)
        }
    }
}