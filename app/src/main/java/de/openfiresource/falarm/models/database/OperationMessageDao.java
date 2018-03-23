package de.openfiresource.falarm.models.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface OperationMessageDao {

    @Query("SELECT * FROM operation_message")
    public Flowable<List<OperationMessage>> getAllAsync();

    @Query("SELECT * FROM operation_message")
    public List<OperationMessage> getAll();

    @Query("SELECT * FROM operation_message WHERE id = :id LIMIT 1")
    public OperationMessage findById(long id);

    @Insert
    public void insertOperationMessage(OperationMessage operationMessage);

    @Update
    public void updateOperationMessage(OperationMessage operationMessage);

    @Delete
    public void deleteOperationMessage(OperationMessage operationMessage);
}
