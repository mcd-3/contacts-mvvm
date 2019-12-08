package com.matthew.carvalhodagenais.contactsmvvm

import android.app.DatePickerDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.matthew.carvalhodagenais.contactsmvvm.data.entities.Contact
import com.matthew.carvalhodagenais.contactsmvvm.helpers.ImageDataHelper
import com.matthew.carvalhodagenais.contactsmvvm.viewmodels.ContactListViewModel
import com.matthew.carvalhodagenais.contactsmvvm.viewmodels.ContactListViewModelFactory
import kotlinx.android.synthetic.main.fragment_contact_add_edit.*
import java.util.*

class ContactAddEditFragment: Fragment() {

    private lateinit var viewModel: ContactListViewModel
    private var birthdayToSave: Calendar? = null

    companion object {
        const val REQUEST_CODE: String = "com.matthew.carvalhodagenais" +
                ".contactsmvvm.ContactAddEditFragment.REQUEST_CODE"
        const val PROFILE_DIALOG_TAG = "com.matthew.carvalhodagenais" +
        ".contactsmvvm.ContactAddEditFragment.PROFILE_DIALOG"

        const val ADD_REQUEST: Int = 1
        const val EDIT_REQUEST: Int = 2

        const val FRAGMENT_TAG = "ContactAddEditFragment"

        fun newInstance(requestCode: Int): ContactAddEditFragment {
            val fragment = ContactAddEditFragment()
            val args = Bundle()
            args.putInt(REQUEST_CODE, requestCode) //Add int here
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_add_edit, container, false)
        viewModel = ViewModelProviders.of(activity!!,
            ContactListViewModelFactory(activity!!.application)).get(ContactListViewModel::class.java)
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val request = arguments!!.getInt(REQUEST_CODE)

        if (request == ADD_REQUEST) {
            when((1..4).random()) {
                1 -> contact_profile_image_view.setImageResource(R.drawable.default_profile_1)
                2 -> contact_profile_image_view.setImageResource(R.drawable.default_profile_2)
                3 -> contact_profile_image_view.setImageResource(R.drawable.default_profile_3)
                4 -> contact_profile_image_view.setImageResource(R.drawable.default_profile_4)
            }
        } else if (request == EDIT_REQUEST) { //Give all UI elements values
            val contact = viewModel.getSelectedContact()
            first_name_edit_text.setText(contact.value?.first_name)
            last_name_edit_text.setText(contact.value?.last_name)
            email_edit_text.setText(contact.value?.email)
            home_phone_edit_text.setText(contact.value?.phone_home)
            cell_phone_edit_text.setText(contact.value?.phone_cell)
            work_phone_edit_text.setText(contact.value?.phone_work)
            address_edit_text.setText(contact.value?.address)
            val cal = createBirthday(contact.value?.birthday)
            if ( cal != null) {
                changeDateTextStyle(false)
                cal.timeInMillis = contact.value?.birthday?.time!!
                birthday_date_text_view.text = "${cal.get(Calendar.DAY_OF_MONTH)}/" +
                    "${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.YEAR)}"
            }
            contact_profile_image_view.setImageBitmap(viewModel.getContactProfileImage(contact.value!!))
        }

        birthday_date_picker_button.setOnClickListener(pickDateClickListener)
        birthday_date_clear_button.setOnClickListener(clearDateClickListener)
        contact_profile_image_clickable.setOnClickListener(profileImageViewOnClick)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        activity!!.menuInflater.inflate(R.menu.contact_add_edit_menu, menu)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.contact_add_edit_menu_save -> {
            saveAndFinish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * Saves a contact into the database for either add or edit requests
     */
    private fun saveAndFinish() {

        //Save image and get the name
        val bitmap: Bitmap = (contact_profile_image_view.drawable as BitmapDrawable).bitmap
        val imageName: String = ImageDataHelper.saveImage(bitmap, context!!.applicationContext)

        val contact = Contact(
            first_name_edit_text.text.toString(),
            last_name_edit_text.text.toString(),
            email_edit_text.text.toString(),
            home_phone_edit_text.text.toString(),
            cell_phone_edit_text.text.toString(),
            work_phone_edit_text.text.toString(),
            address_edit_text.text.toString(),
            birthdayToSave?.time,
            imageName
        )

        if (arguments!!.getInt(REQUEST_CODE) == ADD_REQUEST) {
            viewModel.addContact(contact)
        } else {
            contact.id = viewModel.getSelectedContact().value!!.id
            viewModel.deleteContactProfileImage(viewModel.getSelectedContact().value!!)
            viewModel.update(contact)
        }

        val transaction = activity!!
            .supportFragmentManager.beginTransaction().apply {
            replace(this@ContactAddEditFragment.id,
                ContactsListFragment.newInstance(),
                ContactsListFragment.FRAGMENT_TAG)
        }

        transaction.commit()
    }

    /**
     * Creates a Calendar using a year, month, and day
     *
     * @param year int
     * @param month int
     * @param day int
     * @return Calendar
     */
    private fun createBirthday(year: Int, month: Int, day: Int): Calendar {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)
        return cal
    }

    /**
     * Creates a Calendar using a Date object
     *
     * @param date Date
     * @return Calendar
     */
    private fun createBirthday(date: Date?): Calendar? {
        if ( date == null ) {
            return null
        }
        val cal = Calendar.getInstance()
        cal.set(Calendar.DATE, date.time.toInt())
        return cal
    }

    /**
     * Changes the text style and text color of the Birthday Date
     *
     * @param isEmpty Boolean
     */
    private fun changeDateTextStyle(isEmpty: Boolean) {
        if (isEmpty) {
            birthday_date_text_view.text = getString(R.string.contact_add_edit_birthday_default)
            birthday_date_text_view.setTypeface(null, Typeface.ITALIC)
            birthday_date_text_view.setTextColor(Color.GRAY)
        } else {
            birthday_date_text_view.typeface = Typeface.DEFAULT_BOLD
            birthday_date_text_view.setTextColor(Color.BLACK)
        }
    }

    /**
     * Opens a new ProfilePickerDialog so that the user can select an image for a profile picture
     */
    private fun openDialog() {
        val profileDialog = ProfilePickerDialogFragment()
        profileDialog.show(fragmentManager!!, PROFILE_DIALOG_TAG)
    }

    /**
     * OnClickListener to set a birthday
     */
    private val pickDateClickListener = View.OnClickListener {
        val cal = Calendar.getInstance()
        val datePicker = DatePickerDialog(context!!,
            DatePickerDialog.OnDateSetListener{_, mYear, mMonth, mDay ->
                birthday_date_text_view.text = "${mDay}/${mMonth + 1}/${mYear}"
                changeDateTextStyle(false)
                birthdayToSave = createBirthday(mYear, mMonth, mDay)
            }, cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH))
        datePicker.show()
    }

    /**
     * OnClickListener for the "clear date" button
     * Clears the dateToSave and the birthday text
     */
    private val clearDateClickListener = View.OnClickListener {
        birthdayToSave = null
        changeDateTextStyle(true)
    }

    /**
     * OnClickListener for the profile ImageView
     * Opens a ProfilePicker dialog to pick an image for the contact's profile image
     */
    private val profileImageViewOnClick = View.OnClickListener {
        openDialog()
    }
}