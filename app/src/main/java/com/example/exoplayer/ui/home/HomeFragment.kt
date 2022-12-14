package com.example.exoplayer.ui.home

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.exoplayer.data.model.Movie
import com.example.exoplayer.databinding.FragmentHomeBinding
import com.example.exoplayer.util.State
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import android.view.*
import android.view.MenuInflater
import com.example.exoplayer.MainActivity
import com.example.exoplayer.R
import com.example.exoplayer.ui.home.adapter.HomeAdpater
import com.example.exoplayer.ui.home.adapter.RecyclerViewHomeClickListener
import com.example.exoplayer.ui.home.viewmodel.HomeViewModel


@AndroidEntryPoint
class HomeFragment: Fragment(), RecyclerViewHomeClickListener {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private val homeAdapter: HomeAdpater by lazy { HomeAdpater(requireContext(), this@HomeFragment) }
    val MOVIE_DB_API_TOKEN = "e3a91cacf427557ee3c46dd39b7076e4"
    var totalPages = 0
    var counter = 1
    var present_state = POPULAR

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.recyclerView.apply {
            adapter = homeAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!recyclerView.canScrollVertically(1)) { //1 for down
                        if(counter <= totalPages){
                            if (present_state.equals(POPULAR)) {
                                homeViewModel.fetchPopular(MOVIE_DB_API_TOKEN)
                            } else {
                                homeViewModel.fetchTopMovies(MOVIE_DB_API_TOKEN)
                            }
                            ++counter
                        }
                    }
                }
            })
        }

        homeViewModel.fetchPopular(MOVIE_DB_API_TOKEN)
        (activity as MainActivity?)!!.changeTitle("Popular Movies")
        observeUI()
        observeTop()

        return binding.root
    }

    private fun observeUI() {
        homeViewModel.moviePopular.observe(viewLifecycleOwner) {
            when (it) {
                is State.Success -> {
                    binding.progress.visibility = View.GONE
                    val value = it.data!!
                    totalPages = value.totalPages
                    val data = value.movies
                    homeAdapter.submitList(data!!)
                }
                is State.Error -> {
                    binding.progress.visibility = View.GONE
                    it.message?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    }
                }
                is State.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
            }

        }
    }

    private fun observeTop() {
        homeViewModel.movieTop.observe(viewLifecycleOwner) {
            when (it) {
                is State.Success -> {
                    binding.progress.visibility = View.GONE
                    val value = it.data!!
                    totalPages = value.totalPages
                    val data = value.movies
                    homeAdapter.submitList(data!!)
                }
                is State.Error -> {
                    binding.progress.visibility = View.GONE
                    it.message?.let { message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    }
                }
                is State.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                }
            }

        }
    }

    override fun clickOnItem(data: Movie, card: View) {

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.most_rated ->{
                (activity as MainActivity?)!!.changeTitle("Top Movies")
                present_state = TOP_RATED
                homeViewModel.fetchTopMovies(MOVIE_DB_API_TOKEN)
                true
            }
            R.id.now_playing->{
                (activity as MainActivity?)!!.changeTitle("Popular Movies")
                present_state = POPULAR
                homeViewModel.fetchPopular(MOVIE_DB_API_TOKEN)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val POPULAR = "popular"
        const val TOP_RATED = "top_rated"
    }
}