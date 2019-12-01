package com.matthew.carvalhodagenais.contactsmvvm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.matthew.carvalhodagenais.contactsmvvm.data.entities.Contact

/* A DAO has all the necessary methods to interact with the database
 * Each entity has it's own DAO
 * Remember to add "@Dao"
 */
@Dao
interface ContactDao {

    @Insert
    fun insert(contact: Contact)

    @Update
    fun update(contact: Contact)

    @Delete
    fun delete(contact: Contact)

    @Query("DELETE FROM contact_table")
    fun deleteAllContacts()

    @Query("SELECT * FROM contact_table ORDER BY last_name")
    fun getAllContacts(): LiveData<List<Contact>>

    @Query("SELECT * FROM contact_table WHERE id = :id")
    fun getContactById(id: Int): LiveData<Contact>

    @Query("SELECT birthday FROM contact_table")
    fun getBirthdays(): LiveData<List<Long>>
}