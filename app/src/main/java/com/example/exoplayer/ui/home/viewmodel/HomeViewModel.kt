package com.example.exoplayer.ui.home.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exoplayer.data.model.Movie
import com.example.exoplayer.util.State
import com.example.exoplayer.data.model.MoviesResponse
import com.example.exoplayer.ui.home.repository.HomeRepository
import com.example.exoplayer.util.hasInternetConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val moviePopular: MutableLiveData<State<MoviesResponse>> = MutableLiveData()
    var movieListResponse: MoviesResponse? = null
    var moviePage = 1

    val movieTop: MutableLiveData<State<MoviesResponse>> = MutableLiveData()
    var movieTopListResponse: MoviesResponse? = null
    var movieTopPage = 1

    /*fun fetchPopular(apikey: String){
        moviePopular.postValue(State.Loading())
        viewModelScope.launch {
            try {
                if (hasInternetConnection(context)) {
                    val response = homeRepository.fetchPopular(apikey)
                    moviePopular.postValue(State.Success(response.body()!!))
                } else
                    moviePopular.postValue(State.Error("No Internet Connection"))
            } catch (ex: Exception) {
                when (ex) {
                    is IOException -> moviePopular.postValue(State.Error("Network Failure " +  ex.localizedMessage))
                    else -> moviePopular.postValue(State.Error("Conversion Error"))
                }
            }
        }
    }
*/

    fun fetchPopular(apikey: String) = viewModelScope.launch {
        safeMovieCall(apikey, moviePage)
    }

    private suspend fun safeMovieCall(apikey: String, page: Int){
        moviePopular.postValue(State.Loading())
        try{
            if(hasInternetConnection(context)){
                val response = homeRepository.fetchPopular(apikey, page)
                moviePopular.postValue(handleOrderResponse(response))
            }
            else
                moviePopular.postValue(State.Error("No Internet Connection"))
        }
        catch (ex: Exception){
            when(ex){
                is IOException -> moviePopular.postValue(State.Error("Network Failure"))
                else -> moviePopular.postValue(State.Error("Conversion Error"))
            }
        }
    }

    private fun handleOrderResponse(response: Response<MoviesResponse>): State<MoviesResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                moviePage++
                if (movieListResponse == null)
                    movieListResponse = resultResponse
                else {
                    val oldMovies = movieListResponse!!.movies as ArrayList<Movie>?
                    val newMovies = resultResponse.movies!! as ArrayList<Movie>?
                    oldMovies!!.addAll(newMovies!!)
                }
                return State.Success(movieListResponse ?: resultResponse)
            }
        }
        return State.Error(response.message())
    }

    fun fetchTopMovies(apikey: String) = viewModelScope.launch {
        safeTopMovieCall(apikey, movieTopPage)
    }

    private suspend fun safeTopMovieCall(apikey: String, page: Int){
        moviePopular.postValue(State.Loading())
        try{
            if(hasInternetConnection(context)){
                val response = homeRepository.fetchTopRated(apikey, page)
                moviePopular.postValue(handleTopOrderResponse(response))
            }
            else
                moviePopular.postValue(State.Error("No Internet Connection"))
        }
        catch (ex: Exception){
            when(ex){
                is IOException -> moviePopular.postValue(State.Error("Network Failure"))
                else -> moviePopular.postValue(State.Error("Conversion Error"))
            }
        }
    }

    private fun handleTopOrderResponse(response: Response<MoviesResponse>): State<MoviesResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                movieTopPage++
                if (movieTopListResponse == null)
                    movieTopListResponse = resultResponse
                else {
                    val oldMovies = movieTopListResponse!!.movies as ArrayList<Movie>?
                    val newMovies = resultResponse.movies!! as ArrayList<Movie>?
                    oldMovies!!.addAll(newMovies!!)
                }
                return State.Success(movieTopListResponse ?: resultResponse)
            }
        }
        return State.Error(response.message())
    }
}