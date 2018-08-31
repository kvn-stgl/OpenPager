package de.openfiresource.falarm.models.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface OperationMessageDao {

    @Query("SELECT * FROM operation_message ORDER BY timestamp DESC")
    LiveData<List<OperationMessage>> getAllAsync();

    @Query("SELECT * FROM operation_message ORDER BY timestamp DESC")
    List<OperationMessage> getAll();

    @Query("SELECT * FROM operation_message WHERE id = :id LIMIT 1")
    OperationMessage findById(long id);

    @Query("SELECT * FROM operation_message WHERE id = :id LIMIT 1")
    Flowable<OperationMessage> findByIdAsync(long id);

    @Insert
    long insertOperationMessage(OperationMessage operationMessage);

    @Update
    void updateOperationMessage(OperationMessage operationMessage);

    @Delete
    void deleteOperationMessage(OperationMessage operationMessage);
}
