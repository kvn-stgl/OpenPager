package de.openfiresource.falarm.models;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import de.openfiresource.falarm.models.database.OperationMessageDao;
import de.openfiresource.falarm.models.database.OperationRuleDao;
import de.openfiresource.falarm.utils.RoomConverter;

/**
 * The Room database that contains the Users table
 */
@Database(entities = {
        de.openfiresource.falarm.models.database.OperationMessage.class,
        de.openfiresource.falarm.models.database.OperationRule.class
}, version = 1)
@TypeConverters({RoomConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract OperationMessageDao operationMessageDao();

    public abstract OperationRuleDao operationRuleDao();

}