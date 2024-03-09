package com.apero.rates.remote

import com.apero.rates.R
import com.apero.rates.model.UiText
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

/**
 * Created by KO Huyn on 07/03/2024.
 */
val remoteRate get() = RemoteRateConfiguration.getInstance()

class RemoteRateConfiguration : BaseRemoteConfiguration() {
    override fun getPrefsName(): String {
        return "apero_rate_prefs"
    }

    override fun sync(remoteConfig: FirebaseRemoteConfig) {
        remoteConfig.saveToLocal(ContentTagRating)
        remoteConfig.saveToLocal(StarVoteOnStore)
        remoteConfig.saveToLocal(ContentRate1Star)
        remoteConfig.saveToLocal(ContentRate2Star)
        remoteConfig.saveToLocal(ContentRate3Star)
        remoteConfig.saveToLocal(ContentRate4Star)
        remoteConfig.saveToLocal(ContentRate5Star)
    }

    val contentTagRating get() = ContentTagRating.get().getBy(application).split(",")
    val starVoteOnStore get() = StarVoteOnStore.get()
    val contentRate1Star get() = ContentRate1Star.get()
    val contentRate2Star get() = ContentRate2Star.get()
    val contentRate3Star get() = ContentRate3Star.get()
    val contentRate4Star get() = ContentRate4Star.get()
    val contentRate5Star get() = ContentRate5Star.get()

    private object ContentTagRating : RemoteKeys.UiTextKey(
        "content_tag_rating",
        R.string.str_rate_content_tag_rating
    )

    private object StarVoteOnStore : RemoteKeys.LongKey(
        "star_vote_on_store",
        4
    )

    private object ContentRate1Star : RemoteKeys.UiTextKey("content_rate_1_star", R.string.str_rate_content_rate_1_star)
    private object ContentRate2Star : RemoteKeys.UiTextKey("content_rate_2_star", R.string.str_rate_content_rate_2_star)
    private object ContentRate3Star :
        RemoteKeys.UiTextKey("content_rate_3_star", R.string.str_rate_content_rate_3_star)

    private object ContentRate4Star :
        RemoteKeys.UiTextKey("content_rate_4_star", R.string.str_rate_content_rate_4_star)

    private object ContentRate5Star :
        RemoteKeys.UiTextKey("content_rate_5_star", R.string.str_rate_content_rate_5_star)

    companion object {

        @Volatile
        private var _instance: RemoteRateConfiguration? = null

        @Synchronized
        @JvmStatic
        fun getInstance(): RemoteRateConfiguration = synchronized(this) {
            _instance ?: RemoteRateConfiguration().also { _instance = it }
        }
    }
}