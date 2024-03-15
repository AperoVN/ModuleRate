package com.apero.rates.feedback

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.apero.rates.ModuleRate
import com.apero.rates.R
import com.apero.rates.launcher.RequestForResultManager
import com.apero.rates.base.BindingActivity
import com.apero.rates.databinding.RateActivityFeedbackBinding
import com.apero.rates.ext.DeviceUtils
import com.apero.rates.ext.disableAdResumeByClickAction
import com.apero.rates.feedback.adapter.MediaAdapter
import com.apero.rates.feedback.adapter.SuggestionAdapter
import com.apero.rates.model.ItemMedia
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by KO Huyn on 19/10/2023.
 */
internal class FeedbackActivity : BindingActivity<RateActivityFeedbackBinding>() {

    companion object {
        private const val MIN_TEXT = 6
        private const val ARG_HAS_ACTIVITY_RESULT = "arg_has_activity_result"
        fun start(context: Context) {
            val intent = Intent(context, FeedbackActivity::class.java)
            context.startActivity(intent)
        }

        fun getIntent(context: Context, hasActivityResult: Boolean = false): Intent {
            return Intent(context, FeedbackActivity::class.java).apply {
                putExtra(ARG_HAS_ACTIVITY_RESULT, hasActivityResult)
            }
        }
    }

    private val suggestionAdapter by lazy { SuggestionAdapter() }
    private val mediaAdapter by lazy { MediaAdapter() }
    private val viewModel by viewModels<FeedbackViewModel>()
    private val photoForResult =
        RequestForResultManager(this, ActivityResultContracts.PickMultipleVisualMedia())

    private val hasActivityResult
        get() = intent?.getBooleanExtra(ARG_HAS_ACTIVITY_RESULT, false) ?: false

    override fun getStatusBarColor(): Int {
        return R.color.clr_rate_background
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): RateActivityFeedbackBinding {
        return RateActivityFeedbackBinding.inflate(layoutInflater)
    }

    override fun updateUI(savedInstanceState: Bundle?) {
        track("feedback_scr_view")
        initView()
        handleClick()
        handleObserver()
    }

    private fun initView() {
        binding.rvOptions.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvOptions.adapter = suggestionAdapter
        binding.edtFeedback.hint = getString(R.string.str_rate_feedback_content_hint, 6)
        binding.rvMedia.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMedia.adapter = mediaAdapter
    }

    private fun handleClick() {
        binding.imgBack.setOnClickListener { onBackPressed() }
        binding.txtUpload.setOnClickListener {
            disableAdResumeByClickAction()
            selectMedia()
        }
        mediaAdapter.setOnAddMediaListener {
            selectMedia()
        }
        mediaAdapter.setOnRemoveMediaListener { item ->
            viewModel.removeMedia(item)
        }
        suggestionAdapter.setOnClickListener { item, _ ->
//            viewModel.setClickSuggestion(item)
            var currentText = binding.edtFeedback.text?.toString() ?: ""
            val labelSelected = item.label.getBy(this).toString()
            currentText = if (currentText.isEmpty()) labelSelected else "$currentText\n${labelSelected.trim()}"
            binding.edtFeedback.setText(currentText.trim())
            binding.edtFeedback.setSelection(binding.edtFeedback.length())
        }
        binding.txtSubmit.setOnClickListener {
            val listSuggestion = viewModel.listSuggestion.value.filter { it.isSelected }
                .map { it.label.getBy(this).toString() }
            val listImage =
                viewModel.listMedia.value.filterIsInstance<ItemMedia.Image>().map { it.uri }
            val textFeedback = binding.edtFeedback.text?.toString() ?: ""
            if (textFeedback.isNotEmpty() && textFeedback.length < MIN_TEXT) {
                showMessage(getString(R.string.str_rate_feedback_content_require, MIN_TEXT))
            } else {
                track("feedback_scr_click_submit")
                sendEmail(listSuggestion, listImage, textFeedback)
            }
        }
        binding.edtFeedback.doOnTextChanged { str, _, _, _ ->
            viewModel.updateContentLength(str?.trim()?.length ?: 0)
        }
//        binding.layoutContent.setOnTouchListener { _, _ ->
//            hideKeyboard(binding.edtFeedback)
//            true
//        }
    }

    private fun selectMedia() {
        track("feedback_scr_click_upload_photo")
        photoForResult.startForResult(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) { result ->
            viewModel.updateMedia(result)
        }
    }

    override fun onBackPressed() {
        if (hasActivityResult) {
            setResult(Activity.RESULT_CANCELED)
            finish()
        } else {
            super.onBackPressed()
        }
    }

    private fun handleObserver() {
        viewModel.listSuggestion.onEach {
            suggestionAdapter.items = it
        }.launchIn(lifecycleScope)

        viewModel.listMedia
            .combine(viewModel.contentLength) { list, length ->
                list.filterIsInstance<ItemMedia.Image>().isNotEmpty() || length >= MIN_TEXT
            }.flowWithLifecycle(lifecycle)
            .distinctUntilChanged()
            .onEach { isEnableSubmit ->
                binding.txtSubmit.isEnabled = isEnableSubmit
                binding.txtSubmit.alpha = if (isEnableSubmit) 1.0f else 0.5f
            }
            .launchIn(lifecycleScope)

        viewModel.listMedia.flowWithLifecycle(lifecycle).onEach {
            val oldCount = mediaAdapter.itemCount
            mediaAdapter.submitList(it) {
                if (it.size > oldCount) {
                    binding.rvMedia.scrollToPosition(it.size - 1)
                }
            }
        }.launchIn(lifecycleScope)

        viewModel.listMedia
            .flowWithLifecycle(lifecycle)
            .map { it.filterNot { it is ItemMedia.Add }.isEmpty() }
            .distinctUntilChanged()
            .onEach { isEmptyData ->
                binding.txtUpload.isVisible = isEmptyData
                binding.rvMedia.isVisible = !isEmptyData
            }
            .launchIn(lifecycleScope)
    }

    private fun sendEmail(
        listSuggestion: List<String>,
        listImage: List<Uri>,
        textFeedback: String
    ) {
        disableAdResumeByClickAction()
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels

        val infoSystem =
            "App v${ModuleRate.VERSION_NAME}(${ModuleRate.VERSION_CODE}), Model: ${Build.MODEL}," +
                    " OS: ${Build.VERSION.SDK_INT}, Screen size: $screenWidth x $screenHeight, RAM: ${
                        DeviceUtils.getRAMDeviceInfo(
                            this
                        )
                    }, "
        val content =
            "$textFeedback\ntag:${listSuggestion.joinToString(separator = ",")} \n$infoSystem"
        kotlin.runCatching {
            val mailIntent = Intent()
            mailIntent.action = Intent.ACTION_SEND_MULTIPLE
            mailIntent.type = "message/rfc822"
            mailIntent.setPackage("com.google.android.gm")
            mailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("trustedapp.help@gmail.com"))
            mailIntent.putExtra(Intent.EXTRA_SUBJECT, "${ModuleRate.APP_NAME} Feedback")
            mailIntent.putExtra(Intent.EXTRA_TEXT, content)
            if (listImage.isNotEmpty()) {
                mailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(listImage))
            }
            track("feedback_success", "content" to content)
            var jobCheckResult :Job? = null
            lifecycleScope.launch {
                val flagOneShot = AtomicBoolean(false)
                lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    if (flagOneShot.compareAndSet(false, true)) {
                        yield()
                        setResult(Activity.RESULT_OK)
                        finish()
                        jobCheckResult?.cancel()
                    }
                }
            }.let { jobCheckResult = it }
            startActivity(mailIntent)
        }
    }
}
