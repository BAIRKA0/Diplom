package com.example.uchet.viewModels

import androidx.lifecycle.ViewModel
import com.example.uchet.Graph
import com.example.uchet.Repository

class SettingViewModel(
    private val repository: Repository = Graph.repository
): ViewModel() {
    fun updateAdminPassword(){

    }
}