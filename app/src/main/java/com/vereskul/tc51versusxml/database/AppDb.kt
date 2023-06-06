package com.vereskul.tc51versusxml.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vereskul.tc51versusxml.database.entities.GoodsEntity
import com.vereskul.tc51versusxml.database.entities.SupplierOrderEntity
import com.vereskul.tc51versusxml.database.entities.UsersEntity

@Database(
    entities = [
        UsersEntity::class,
        SupplierOrderEntity::class,
        GoodsEntity::class],
    version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDb:RoomDatabase() {
    abstract fun getSupplierOrdersDAO():SupplierOrdersDAO
    abstract fun getGoodsDAO():GoodsDAO
    abstract fun getUsersDAO():UsersDAO
    companion object{
        private const val DB_NAME = "main.db"
        @Volatile private var db:AppDb? = null
        private val LOCK = Any();
        fun getInstance(context: Context): AppDb{
            synchronized(LOCK) {
                db?.let { return it }
                val instance = Room.databaseBuilder(
                    context,
                    AppDb::class.java,
                    DB_NAME
                ).build()
                db = instance
                return instance
            }
        }
    }
}