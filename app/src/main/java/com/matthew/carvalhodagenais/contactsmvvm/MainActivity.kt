package com.matthew.carvalhodagenais.contactsmvvm

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import com.matthew.carvalhodagenais.contactsmvvm.helpers.ImageDataHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_contact_add_edit.*
import java.lang.Exception

class MainActivity : AppCompatActivity(), ProfilePickerDialogFragment.ProfilePickerDialogListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Set initial fragment
        val listFragment = ContactsListFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(main_activity_frame_layout.id, listFragment)
            .commit()
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
