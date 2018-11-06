package de.openfiresource.openpager.models.database


import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface OperationRuleDao {

    @Query("SELECT * FROM operation_rule")
    fun allAsync(): LiveData<List<OperationRule>>

    @Query("SELECT * FROM operation_rule")
    fun all(): List<OperationRule>

    @Query("SELECT * FROM operation_rule WHERE id = :id LIMIT 1")
    fun findById(id: Long): OperationRule

    @Query("SELECT * FROM operation_rule WHERE id = :id LIMIT 1")
    fun findByIdAsync(id: Long): LiveData<OperationRule>

    @Insert
    fun insertOperationRule(operationRule: OperationRule): Long?

    @Update
    fun updateOperationRule(operationRule: OperationRule)

    @Delete
    fun deleteOperationRule(operationRule: OperationRule)
}
