package de.openfiresource.falarm.models.database;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface OperationRuleDao {

    @Query("SELECT * FROM operation_rule")
    public Flowable<List<OperationRule>> getAllAsync();

    @Query("SELECT * FROM operation_rule")
    public List<OperationRule> getAll();

    @Query("SELECT * FROM operation_rule WHERE id = :id LIMIT 1")
    public OperationRule findById(long id);

    @Insert
    public void insertOperationRule(OperationRule operationRule);

    @Update
    public void updateOperationRule(OperationRule operationRule);

    @Delete
    public void deleteOperationRule(OperationRule operationRule);
}
