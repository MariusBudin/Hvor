package com.mariusbudin.hvor.data.venues

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mariusbudin.hvor.data.venues.model.VenueEntity

@Dao
interface VenuesDao {

    @Query("SELECT * FROM venues")
    fun getAll(): LiveData<List<VenueEntity>>

    @Query("SELECT * FROM venues WHERE id = :id")
    fun get(id: Int): LiveData<VenueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(values: List<VenueEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: VenueEntity)
}