package com.app.registros.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {

    @Query("SELECT * FROM local_mediciones WHERE userId = :userId AND syncStatus != 'PENDING_DELETE' ORDER BY fechaHora DESC")
    fun getAllMeasurementsFlow(userId: String): Flow<List<MeasurementEntity>>

    @Query("SELECT * FROM local_mediciones WHERE id = :id LIMIT 1")
    suspend fun getMeasurementById(id: String): MeasurementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(measurement: MeasurementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(measurements: List<MeasurementEntity>)

    @Update
    suspend fun update(measurement: MeasurementEntity)

    @Query("UPDATE local_mediciones SET syncStatus = 'PENDING_DELETE' WHERE id = :id")
    suspend fun markForDeletion(id: String)

    @Query("DELETE FROM local_mediciones WHERE id = :id")
    suspend fun deletePermanently(id: String)

    @Query("SELECT * FROM local_mediciones WHERE userId = :userId AND syncStatus != 'SYNCED'")
    suspend fun getPendingSyncMeasurements(userId: String): List<MeasurementEntity>

    @Query("UPDATE local_mediciones SET syncStatus = 'SYNCED' WHERE id = :id")
    suspend fun markAsSynced(id: String)
}
