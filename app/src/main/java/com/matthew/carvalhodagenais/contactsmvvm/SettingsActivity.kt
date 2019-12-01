package com.matthew.carvalhodagenais.contactsmvvm

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.matthew.carvalhodagenais.contactsmvvm.viewmodels.ContactListViewModel
import com.matthew.carvalhodagenais.contactsmvvm.viewmodels.ContactListViewModelFactory
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPrefs =
            getSharedPreferences(BaseApp.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        settings_notification_switch.isChecked =
            sharedPrefs.getBoolean(BaseApp.NOTIFICATION_PREFERENCE, true)

        val sharedPreferences =
            getSharedPreferences(BaseApp.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        settings_delete_all_relative_layout.setOnClickListener(deleteAllOnClick)
        settings_birthday_notification_relative_layout.setOnClickListener(notificationOnClickListener)
        settings_notification_switch.setOnCheckedChangeListener(notificationSwitchOnCheckedListener)
    }

    /**
     * View Listener for deleting all contacts
     */
    private val deleteAllOnClick = View.OnClickListener {
        val builder = AlertDialog.Builder(this)
            .setTitle(getString(R.string.settings_delete_all_alert_title))
            .setMessage(getString(R.string.settings_delete_all_description))
            .setPositiveButton(getString(R.string.alert_delete)) {dialog, which ->
                val viewModel = ViewModelProviders.of(this,
                    ContactListViewModelFactory(this.application)
                ).get(ContactListViewModel::class.java)
                viewModel.deleteAllContacts()
                Toast.makeText(
                    applicationContext,
                    getString(R.string.toast_all_contacts_deleted),
                    Toast.LENGTH_LONG
                ).show()
            }
            .setNegativeButton(getString(R.string.alert_cancel),null)
            builder.show()
    }

    /**
     * View Listener to turn on/off the birthday notifications
     */
    private val notificationOnClickListener = View.OnClickListener {
        settings_notification_switch.isChecked = !(settings_notification_switch.isChecked)
    }

    /**
     * Switch Listener to turn on/off the birthday notifications
     */
    private val notificationSwitchOnCheckedListener =
        CompoundButton.OnCheckedChangeListener {_, isChecked ->
            val sharedPrefs = getSharedPreferences(BaseApp.SHARED_PREFERENCES, Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.putBoolean(BaseApp.NOTIFICATION_PREFERENCE, isChecked)
            editor.apply()
    }
}
