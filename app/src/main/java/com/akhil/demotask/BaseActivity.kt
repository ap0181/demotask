package com.akhil.demotask

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.akhil.demotask.customView.DialogProgress


open class BaseActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "BaseActivity"
    }

    var mProgressDialogs: DialogProgress? = null

    fun showLoading(ctx: Context?, message: String?) {
        try {
            hideLoading()
            mProgressDialogs =
                DialogProgress(ctx, R.style.progress_dialog_text_style, message!!)
            mProgressDialogs?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showLoading() {
        try {
            hideLoading()
            mProgressDialogs =
                DialogProgress(this, R.style.progress_dialog_text_style)
            mProgressDialogs?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hideLoading() {
        try {
            if (mProgressDialogs != null && mProgressDialogs!!.isShowing) {
                mProgressDialogs?.dismiss()
            }
        } catch (e: Exception) {
        }
    }
}