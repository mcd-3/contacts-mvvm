package com.matthew.carvalhodagenais.contactsmvvm

import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.matthew.carvalhodagenais.contactsmvvm.broadcastreceivers.DateChangedBroadcastReceiver
import com.matthew.carvalhodagenais.contactsmvvm.data.entities.Contact
import com.matthew.carvalhodagenais.contactsmvvm.viewmodels.ContactListViewModel
import com.matthew.carvalhodagenais.contactsmvvm.viewmodels.ContactListViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_contact_add_edit.*

class MainActivity : AppCompatActivity(), ProfilePickerDialogFragment.ProfilePickerDialogListener {

    private lateinit var receiver: DateChangedBroadcastReceiver
    private lateinit var sharedPrefs: SharedPreferences


    companion object {
        var themeIsChanged: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPrefs =
            getSharedPreferences(BaseApp.SHARED_PREFERENCES, Context.MODE_PRIVATE)

        //Set the theme
        if (sharedPrefs.getInt(BaseApp.THEME_PREFERENCE, BaseApp.THEME_LIGHT) == BaseApp.THEME_DARK) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.LightTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Set initial fragment
        val listFragment: ContactsListFragment = ContactsListFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(main_activity_frame_layout.id, listFragment, ContactsListFragment.FRAGMENT_TAG)
            .commit()
    }

    override fun onStart() {
        //Recreate view if theme was changed
        if (themeIsChanged) {
            themeIsChanged = false
            recreate()
        }

        super.onStart()
        val viewModel = ViewModelProviders.of(this,
            ContactListViewModelFactory(this.application)).get(ContactListViewModel::class.java)
        viewModel.getAllContacts().observe(this, Observer<List<Contact>> {
            val list = it.toMutableList()
            receiver = DateChangedBroadcastReceiver(list)
            registerReceiver(receiver,
                IntentFilter("android.intent.action.DATE_CHANGED"))
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.contact_list_menu, menu)
        return true
    }

    override fun changeProfileBitmap(bitmap: Bitmap) {
        try {
            contact_profile_image_view.setImageBitmap(bitmap)
        } catch (e: Exception) { //might not be in ContactAddEditFragment
            e.printStackTrace()
        }
    }
}
