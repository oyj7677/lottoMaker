package com.oyj.lottomaker.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface LottoDao {
    @Insert
    fun insertWinningNumber(winningNumber: WinningNumber)

    @Query("SELECT * FROM winning_number")
    fun getWinningNumberAll(): List<WinningNumber>

    @Query("SELECT * FROM winning_number where round=:round")
    fun getWinningNumberWithRound(round: Int): WinningNumber?

    @Query("SELECT max(round) FROM winning_number")
    fun getLastRound(): Int?

    @Transaction
    fun insertWinningNumberList(winningNumberList: ArrayList<WinningNumber>) {
        winningNumberList.forEach { insertWinningNumber(it) }
    }
}