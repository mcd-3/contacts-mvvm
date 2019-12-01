package com.matthew.carvalhodagenais.contactsmvvm

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matthew.carvalhodagenais.contactsmvvm.adapters.ContactsRecyclerAdapter
import com.matthew.carvalhodagenais.contactsmvvm.data.entities.Contact
import com.matthew.carvalhodagenais.contactsmvvm.viewmodels.ContactListViewModel
import com.matthew.carvalhodagenais.contactsmvvm.viewmodels.ContactListViewModelFactory
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_contact_list.*
import kotlinx.android.synthetic.main.fragment_contact_list.view.*
import java.lang.Exception
import java.util.*

class ContactsListFragment : Fragment() {

    companion object {
        fun newInstance() = ContactsListFragment()

        private const val REQUEST_CALL = 1
    }

    private lateinit var rootView: View
    private lateinit var viewModel: ContactListViewModel
    private lateinit var contactAdapter: ContactsRecyclerAdapter

    // For keeping contact's phone number to call when requesting permissions
    private var numberBuffer: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_contact_list, container, false)
        viewModel = ViewModelProviders.of(activity!!,
            ContactListViewModelFactory(activity!!.application)).get(ContactListViewModel::class.java)
        setHasOptionsMenu(true)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val recyclerView =
            rootView.contact_recycler_view as RecyclerView

        contactAdapter = ContactsRecyclerAdapter()

        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity!!.applicationContext)
            adapter = contactAdapter
        }

        viewModel.getAllContacts().observe(viewLifecycleOwner, Observer<List<Contact>> {
            contactAdapter.submitList(it)
            contactAdapter.setSearchableList(it)
            contactAdapter.setItemOnClickListener(itemOnClick)
        })

        ItemTouchHelper(recyclerViewTouchHelper).attachToRecyclerView(recyclerView)

        add_contact_fab.setOnClickListener(addContactFabOnClick)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        activity!!.menuInflater.inflate(R.menu.contact_list_menu, menu)
        val searchItem = menu.findItem(R.id.contact_list_menu_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(searchQueryTextListener)
        searchItem.setOnActionExpandListener(searchExpandListener)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.contact_list_menu_settings -> {
            startActivity(Intent(context, SettingsActivity::class.java))
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                swipeCall(numberBuffer)
            } else {
                Toast.makeText(context, "Call permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Launches a Call Intent given a phone number.
     * Asks permission first if permission is not given
     *
     * @param number String
     */
    private fun swipeCall(number: String) {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {
            numberBuffer = number
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL)
        } else {
            numberBuffer = ""
            if (number != "") {
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${number}"))
                startActivity(intent)
            } else {
                Toast.makeText(context, "No phone number available", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Launches a Text Intent given a phone number
     *
     * @param number String
     */
    private fun swipeText(number: String) {
        try {
            if (number != "") {
                val intent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null))
                intent.putExtra("sms_body", "")
                startActivity(intent)
            } else {
                Toast.makeText(context, "No phone number available", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "No available messaging app", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * OnClickListener for a recyclerview item: changes fragment to show the details of the
     * clicked item
     */
    private var itemOnClick = object: ContactsRecyclerAdapter.ItemOnClickListener {
        override fun onItemClick(contact: Contact) {
            viewModel.setSelectedContact(contact)
            val transaction = activity!!
                .supportFragmentManager.beginTransaction().apply {
                setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right)
                replace(this@ContactsListFragment.id, ContactDetailFragment.newInstance())
                addToBackStack(null)
            }
            transaction.commit()
        }
    }

    /**
     * Replaces current fragment with ContactAddEditFragment to add a new contact
     */
    private var addContactFabOnClick = View.OnClickListener {
        val transaction = activity!!
            .supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top,
                R.anim.enter_from_top, R.anim.exit_to_bottom)
            replace(this@ContactsListFragment.id,
                ContactAddEditFragment.newInstance(ContactAddEditFragment.ADD_REQUEST))
            addToBackStack(null)
        }
        transaction.commit()
    }

    /**
     * OnQueryTextListener for the search menu item.
     * Allows a user to search for a specific contact
     */
    private var searchQueryTextListener = object: SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String?): Boolean {
            contactAdapter.filter.filter(newText)
            return false
        }
        override fun onQueryTextSubmit(query: String?): Boolean {
            return false
        }
    }

    /**
     * OnActionExpandListener for the search menu item.
     * Checks to see if SearchView is expanded, and disables the FloatingActionButton if it is
     */
    private var searchExpandListener = object: MenuItem.OnActionExpandListener {
        override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
            add_contact_fab.setOnClickListener(null)
            add_contact_fab.hide()
            return true
        }
        override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
            add_contact_fab.setOnClickListener(addContactFabOnClick)
            add_contact_fab.show()
            return true
        }
    }

    /**
     * ItemTouchHelper used to swipe RecyclerView items
     * and create background/child views under each item
     */
    private var recyclerViewTouchHelper = object: ItemTouchHelper.SimpleCallback(
        0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val contact = contactAdapter.getContact(viewHolder.adapterPosition)
            if (direction == ItemTouchHelper.RIGHT) {
                swipeCall(viewModel.getFirstAvailableNumber(contact))
            } else {
                swipeText(viewModel.getFirstAvailableNumber(contact))
            }
            contactAdapter.notifyDataSetChanged() //reload so item is not invisible
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            RecyclerViewSwipeDecorator.Builder(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            ).addSwipeLeftBackgroundColor(resources.getColor(R.color.text_swipe, null))
                .addSwipeRightBackgroundColor(resources.getColor(R.color.call_swipe, null))
                .setSwipeLeftLabelColor(Color.WHITE)
                .setSwipeRightLabelColor(Color.WHITE)
                .addSwipeLeftLabel(resources.getString(R.string.swipe_left_label))
                .addSwipeRightLabel(resources.getString(R.string.swipe_right_label))
                .addSwipeLeftActionIcon(R.drawable.ic_sms_swipe)
                .addSwipeRightActionIcon(R.drawable.ic_phone_swipe)
                .create()
                .decorate()

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }
}
