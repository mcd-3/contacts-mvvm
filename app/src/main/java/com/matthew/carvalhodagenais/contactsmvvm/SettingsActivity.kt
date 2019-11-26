package com.matthew.carvalhodagenais.contactsmvvm

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        settings_delete_all_relative_layout.setOnClickListener(deleteAllOnClick)
    }

    //TODO: dialog prompt, then delete all contacts
    private val deleteAllOnClick = View.OnClickListener {

    }
}
