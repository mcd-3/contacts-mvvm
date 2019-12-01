package com.matthew.carvalhodagenais.contactsmvvm.data

import android.content.Context
import android.os.AsyncTask
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.matthew.carvalhodagenais.contactsmvvm.data.dao.ContactDao
import com.matthew.carvalhodagenais.contactsmvvm.data.entities.Contact

@Database(entities = [Contact::class], version = 2)
abstract class ContactDatabase: RoomDatabase() {

    abstract fun contactDao(): ContactDao

    companion object {
        //Initialise DB here
        private var instance: ContactDatabase? = null

        fun getInstance(context: Context): ContactDatabase? {
            if (instance == null) {
                synchronized(ContactDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ContactDatabase::class.java,
                        "contact_database"
                    ).fallbackToDestructiveMigration()
                        .addCallback(roomCallBack)
                        .build()
                }
            }
            return instance
        }

        private val roomCallBack = object: RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                PopulateDBAsyncTask(instance).execute()
            }
        }


        private class PopulateDBAsyncTask(database: ContactDatabase?): AsyncTask<Unit, Unit, Unit>() {
            private val dao = database?.contactDao()
            override fun doInBackground(vararg params: Unit?) {
                dao?.insert(Contact("Matt", "C-D", "mcd@email.com",
                    "5145551111", "", "", "111 Example Street", null, null))
            }
        }
    }
}