package com.app.rickandmortyapp.main.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.rickandmortyapp.main.data.network.response.Character
import retrofit2.HttpException
import java.io.IOException


class RickAndMortyPagingSource(private val service: RetrofitService) :
    PagingSource<Int, Character>() {

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        return try {
            val nextPage = params.key ?: 1
            val response = service.getCharacters(nextPage)
            val characters = response.results
            LoadResult.Page(
                data = characters,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (response.info.next.isNullOrEmpty()) null else nextPage + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}
