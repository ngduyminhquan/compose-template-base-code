package com.project.composeproject.ui.screen.home.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.composeproject.domain.repository.ChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val channelRepository: ChannelRepository
) : ViewModel() {

    fun createSourceFromFile(file: Uri) {
        viewModelScope.launch {
            channelRepository.createChannelSourceFromFile(file)
        }
    }

    fun createSourceFromUrl(url: String) {
        viewModelScope.launch {
            channelRepository.createChannelSourceFromUrl(url)
        }
    }
}
