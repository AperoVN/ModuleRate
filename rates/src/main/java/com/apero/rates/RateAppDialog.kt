package com.apero.rates

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.apero.rates.adapter.RatingAdapter
import com.apero.rates.base.BaseDialogBinding
import com.apero.rates.databinding.RateDialogRateAppBinding
import com.apero.rates.feedback.FeedbackActivity
import com.apero.rates.launcher.RequestForResultManager
import com.apero.rates.remote.remoteRate
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

/**
 * Created by KO Huyn on 18/10/2023.
 */
internal class RateAppDialog : BaseDialogBinding<RateDialogRateAppBinding>() {
    private val ratingAdapter by lazy { RatingAdapter() }
    private var invokeSuccess: (isRated: Boolean) -> Unit = {}
    private val launcherForResultFeedback =
        RequestForResultManager(this, ActivityResultContracts.StartActivityForResult())

    private val ratingState = MutableStateFlow<Int?>(null)
    private var isRateSatisfied = false
    override fun getLayoutBinding(inflater: LayoutInflater): RateDialogRateAppBinding {
        return RateDialogRateAppBinding.inflate(inflater)
    }

    override fun updateUI(savedInstanceState: Bundle?) {
        track("popup_rate_view")
        if (isRateSatisfied) {
            ratingState.update { 5 }
        } else {
            ratingState.update { null }
        }
        binding.rcvRating.layoutManager = GridLayoutManager(context, ratingAdapter.itemCount)
        binding.rcvRating.adapter = ratingAdapter
        ratingAdapter.setOnRatingChangeListener { rating ->
            ratingState.update { rating }
        }
        ratingState.onEach { rating ->
            ratingAdapter.setRate(rating ?: 0)
            when (rating) {
                1 -> "lottie/rate_1_star.json"
                2 -> "lottie/rate_2_star.json"
                3 -> "lottie/rate_3_star.json"
                4 -> "lottie/rate_4_star.json"
                5 -> "lottie/rate_5_star.json"
                else -> "lottie/rate_none.json"
            }.let { setLottieAnimation(it) }
            binding.tvTitle.text = when (rating) {
                in 1..3 -> getString(R.string.str_rate_oh_no)
                in 4..5 -> getString(R.string.str_rate_we_like_you_too)
                else -> getString(R.string.str_rate_rate_app_title)
            }
            binding.tvMessage.text = when (rating) {
                1 -> remoteRate.contentRate1Star.getBy(requireContext())
                2 -> remoteRate.contentRate2Star.getBy(requireContext())
                3 -> remoteRate.contentRate3Star.getBy(requireContext())
                4 -> remoteRate.contentRate4Star.getBy(requireContext())
                5 -> remoteRate.contentRate5Star.getBy(requireContext())
                else -> getString(R.string.str_rate_rating_description_default)
            }
            binding.btnRate.text = when (rating) {
                in 1..remoteRate.starVoteOnStore.dec().toInt() -> getString(R.string.str_rate_rate_us)
                in remoteRate.starVoteOnStore.toInt()..5 -> getString(R.string.str_rate_rate_on_google_play)
                else -> getString(R.string.str_rate_rate_us)
            }
            binding.btnRate.isEnabled = rating != null
            binding.btnRate.alpha = if (rating != null) 1f else 0.5f
        }.launchIn(lifecycleScope)
        binding.btnDismiss.setOnClickListener {
            track("popup_rate_click_x")
            invokeSuccess(false)
        }
        binding.btnRate.setOnClickListener {
            val rate = ratingAdapter.getRating()
            track("popup_rate_click_button","star" to rate)
            if (rate > 0) {
                if (rate > 3) {
                    reviewApp {
                        if (it) {
                        }
                        invokeSuccess(it)
                    }
                } else {
                    val context = context
                    if (context != null) {
                        launcherForResultFeedback.startForResult(
                            FeedbackActivity.getIntent(
                                context,
                                hasActivityResult = true,
                            )
                        ) {
                            invokeSuccess(true)
                        }
                    }
                }
            }
        }
    }

    fun setOnRateSuccessfully(invokeSuccess: (isRated: Boolean, dialog: RateAppDialog) -> Unit) =
        apply {
            this.invokeSuccess = { isRated ->
                invokeSuccess(isRated, this)
            }
        }

    fun setRateSatisfied(isSatisfied: Boolean) = apply {
        this.isRateSatisfied = isSatisfied
    }

    private fun setLottieAnimation(assetName: String) {
        binding.lavSmile.setAnimation(assetName)
        binding.lavSmile.repeatMode = LottieDrawable.RESTART
        binding.lavSmile.repeatCount = LottieDrawable.INFINITE
        binding.lavSmile.playAnimation()
    }

    private fun reviewApp(invokeSuccess: (isRated: Boolean) -> Unit) {
        val activity = activity ?: return
        val manager: ReviewManager = ReviewManagerFactory.create(activity)
        val request: Task<ReviewInfo> = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                track("rate_google_view")
                val reviewInfo: ReviewInfo = task.result
                val flow: Task<Void> =
                    manager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener {
                    track("rate_success")
                    invokeSuccess(true)
                }.addOnFailureListener {
                    invokeSuccess(false)
                }
            } else {
                // There was some problem, continue regardless of the result.
                Log.e("ReviewError", "" + task.exception.toString())
                invokeSuccess(false)
            }
        }.addOnFailureListener { invokeSuccess(false) }
    }
}
