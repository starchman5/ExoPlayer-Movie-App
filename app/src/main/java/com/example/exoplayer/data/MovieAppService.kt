package com.example.exoplayer.data

import com.example.exoplayer.data.model.MoviesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface MovieAppService {

    companion object {
        const val ENDPOINT = "http://api.themoviedb.org/3/"
    }

    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("api_key") apiKey: String?, @Query("page") page: Int? = null): Response<MoviesResponse>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("api_key") apiKey: String?, @Query("page") page: Int? = null): Response<MoviesResponse>


}