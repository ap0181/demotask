package com.akhil.demotask.util

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import com.akhil.demotask.R

class InternetAlertDialog private constructor(context: Context) : Dialog(context) {

    companion object {
        private var instance: InternetAlertDialog? = null

        fun getInstance(context: Context): InternetAlertDialog {
            if (instance == null) {
                instance = InternetAlertDialog(context)
            }
            return instance as InternetAlertDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogview = LayoutInflater.from(context)
            .inflate(R.layout.dialog_internet_alert, null, false)
        setContentView(dialogview)
        setCancelable(false)

        findViewById<Button>(R.id.okButton).setOnClickListener{
            dismiss()
        }
    }

}