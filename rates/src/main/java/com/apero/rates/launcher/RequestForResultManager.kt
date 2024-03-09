package com.apero.rates.launcher

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

/**
 * Created by KO Huyn on 19/10/2023.
 */
internal class RequestForResultManager<I, O> {
    private var activity: AppCompatActivity? = null
    private var fragment: Fragment? = null
    private var onCallbackResult: (O) -> Unit = {}
    private val callback: ActivityResultCallback<O> =
        ActivityResultCallback { result -> onCallbackResult(result) }

    private var launcher: ActivityResultLauncher<I>? = null

    constructor (
        activity: AppCompatActivity,
        contract: ActivityResultContract<I, O>,
    ) : super() {
        this.activity = activity
        launcher = activity.registerForActivityResult(contract, callback)
    }

    constructor(fragment: Fragment, contract: ActivityResultContract<I, O>) : super() {
        this.fragment = fragment
        launcher = fragment.registerForActivityResult(contract, callback)
    }


    fun startForResult(input: I, callback: (O) -> Unit) {
        this.onCallbackResult = callback
        launcher?.launch(input)
    }
}