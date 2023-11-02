package com.app.rickandmortyapp.main.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.app.rickandmortyapp.main.data.network.RetrofitClient
import com.app.rickandmortyapp.main.data.network.RetrofitService
import com.app.rickandmortyapp.main.data.network.RickAndMortyPagingSource
import com.app.rickandmortyapp.main.data.network.response.Character
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AppViewModel : ViewModel() {
    private val retrofitInstance: RetrofitService = RetrofitClient.apiService

    private val _changeList = MutableLiveData(false)
    val changeList: LiveData<Boolean> = _changeList

    private val _activeSearchbar = MutableLiveData(false)
    val activeSearchBar: LiveData<Boolean> = _activeSearchbar

    private val _search = MutableLiveData<String>()
    val search: LiveData<String> = _search

    private val _searchedCharacters = MutableLiveData<List<Character>>()
    val searchedCharacters: LiveData<List<Character>> = _searchedCharacters

    val characters: Flow<PagingData<Character>> = Pager(PagingConfig(pageSize = 20)) {
        RickAndMortyPagingSource(retrofitInstance)
    }.flow.cachedIn(viewModelScope)




     fun getSearchedCharacters(search: String){
         _search.value = search
         CoroutineScope(Dispatchers.IO).launch {
             try {
                 val response = retrofitInstance.searchCharacter(search)
                 val call = response.results

                 withContext(Dispatchers.Main) {
                     if (call.isNotEmpty()) {
                         _searchedCharacters.value = call
                     } else {
                          _searchedCharacters.value = emptyList()
                     }
                 }
             }catch (e: Exception){
                 //mostrar mensaje de error
             }
         }
     }

    fun onButtonClick() {
        _changeList.value = !_changeList.value!!
    }

    fun onActiveSearchBar() {
        _activeSearchbar.value = !_activeSearchbar.value!!
    }


    //splashScreen
    private val mutableStateFlow = MutableStateFlow(true)
    val isLoading = mutableStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            delay(3000)
            mutableStateFlow.value = false
        }
    }

}
