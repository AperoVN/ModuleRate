package com.apero.rates.model

import android.net.Uri

internal sealed interface ItemMedia {
    data class Image(val uri: Uri) : ItemMedia
    object Add : ItemMedia
}