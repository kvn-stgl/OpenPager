package de.openfiresource.openpager.models.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import io.reactivex.Flowable

@Dao
interface OperationMessageDao {

    @Query("SELECT * FROM operation_message ORDER BY timestamp DESC")
    fun allAsync(): LiveData<List<OperationMessage>>

    @Query("SELECT * FROM operation_message ORDER BY timestamp DESC")
    fun all(): List<OperationMessage>

    @Query("SELECT * FROM operation_message WHERE id = :id LIMIT 1")
    fun findById(id: Long): OperationMessage

    @Query("SELECT * FROM operation_message WHERE id = :id LIMIT 1")
    fun findByIdAsync(id: Long): Flowable<OperationMessage>

    @Insert
    fun insertOperationMessage(operationMessage: OperationMessage): Long

    @Update
    fun updateOperationMessage(operationMessage: OperationMessage)

    @Delete
    fun deleteOperationMessage(operationMessage: OperationMessage)
}
