package com.example.lottomaker.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "winning_number")
data class WinningNumber(
    @PrimaryKey
    @ColumnInfo(name = "round")
    val round: Int,

    @ColumnInfo(name = "winningNum1")
    val winningNum1: Int,

    @ColumnInfo(name = "winningNum2")
    val winningNum2: Int,

    @ColumnInfo(name = "winningNum3")
    val winningNum3: Int,

    @ColumnInfo(name = "winningNum4")
    val winningNum4: Int,

    @ColumnInfo(name = "winningNum5")
    val winningNum5: Int,

    @ColumnInfo(name = "winningNum6")
    val winningNum6: Int
)
