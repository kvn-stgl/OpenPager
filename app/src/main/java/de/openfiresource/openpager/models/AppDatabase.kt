package de.openfiresource.openpager.models

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters

import de.openfiresource.openpager.models.database.OperationMessageDao
import de.openfiresource.openpager.models.database.OperationRuleDao
import de.openfiresource.openpager.utils.RoomConverter

/**
 * The Room database that contains the Users table
 */
@Database(entities = [de.openfiresource.openpager.models.database.OperationMessage::class, de.openfiresource.openpager.models.database.OperationRule::class], version = 1)
@TypeConverters(RoomConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun operationMessageDao(): OperationMessageDao

    abstract fun operationRuleDao(): OperationRuleDao

}