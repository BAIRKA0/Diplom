package com.example.uchet.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uchet.Graph
import com.example.uchet.Repository
import com.example.uchet.entities.Acc
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: Repository = Graph.repository
) :ViewModel() {

    val value = repository.cardValue
    val page = repository.page
    fun changeValue(s:String){
        repository.changeCardValue(s)
    }
    fun changePage(s: String){
        repository.changePage(s)
    }

    init {
        viewModelScope.launch {
            if(repository.getAccRow()==0){
                repository.insertAcc(Acc("l","p","123"))
            }
        }
    }

    suspend fun auth(login: String,password: String): Boolean{
        if(repository.checkAcc(login, password)>0){
            return true
        } else return false
    }
}