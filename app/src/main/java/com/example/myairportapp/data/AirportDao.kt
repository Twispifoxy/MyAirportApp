package com.example.myairportapp.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AirportDao {

    @Query("""
        SELECT * FROM airport
        WHERE iata_code LIKE :query OR name LIKE :query
        ORDER BY passengers DESC
    """)
    suspend fun searchAirports(query: String): List<AirportEntity>

    @Query("SELECT * FROM airport ORDER BY passengers DESC")
    suspend fun getAllAirportsSorted(): List<AirportEntity>

    @Query("SELECT * FROM airport WHERE iata_code = :iataCode LIMIT 1")
    suspend fun getAirportByCode(iataCode: String): AirportEntity?
}