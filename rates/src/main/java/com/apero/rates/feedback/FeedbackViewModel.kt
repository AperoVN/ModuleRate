package com.apero.rates.feedback

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.apero.rates.R
import com.apero.rates.model.ItemMedia
import com.apero.rates.model.ItemSuggestion
import com.apero.rates.model.UiText
import com.apero.rates.remote.remoteRate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Created by KO Huyn on 19/10/2023.
 */
internal class FeedbackViewModel : ViewModel() {
    private val _listSuggestion: MutableStateFlow<List<ItemSuggestion>> =
        MutableStateFlow(initListSuggestion())
    private val _contentLength: MutableStateFlow<Int> =
        MutableStateFlow(0)

    val listSuggestion: StateFlow<List<ItemSuggestion>> = _listSuggestion.asStateFlow()
    val contentLength: StateFlow<Int> = _contentLength.asStateFlow()

    private val _listMedia: MutableStateFlow<List<ItemMedia>> =
        MutableStateFlow(listOf())
    val listMedia: StateFlow<List<ItemMedia>> = _listMedia.asStateFlow()

    fun setClickSuggestion(item: ItemSuggestion) {
        _listSuggestion.update { list ->
            list.map { if (it == item) it.copy(isSelected = it.isSelected.not()) else it.copy() }
        }
    }

    fun updateMedia(images: List<Uri>) {
        _listMedia.update { list ->
            val listOld = list.filterNot { it is ItemMedia.Add }
            val listNew = images.map { uri -> ItemMedia.Image(uri) }
            (listOld + listNew).toSet().toList() + listOf(ItemMedia.Add)
        }
    }

    fun removeMedia(image: ItemMedia) {
        _listMedia.update { it.filterNot { item -> item == image } }
    }

    private fun initListSuggestion(): List<ItemSuggestion> {
        return remoteRate.contentTagRating.map { ItemSuggestion(UiText.from(it)) }
    }

    fun updateContentLength(length: Int) {
        _contentLength.update { length }
    }
}