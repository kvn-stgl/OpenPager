package de.openfiresource.falarm.models.database;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface OperationRuleDao {

    @Query("SELECT * FROM operation_rule")
    LiveData<List<OperationRule>> getAllAsync();

    @Query("SELECT * FROM operation_rule")
    List<OperationRule> getAll();

    @Query("SELECT * FROM operation_rule WHERE id = :id LIMIT 1")
    OperationRule findById(long id);

    @Query("SELECT * FROM operation_rule WHERE id = :id LIMIT 1")
    LiveData<OperationRule> findByIdAsync(long id);

    @Insert
    Long insertOperationRule(OperationRule operationRule);

    @Update
    void updateOperationRule(OperationRule operationRule);

    @Delete
    void deleteOperationRule(OperationRule operationRule);
}
