package com.example.lottomaker.retrofit

import com.example.lottomaker.LottoData
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LottoApi {

    @GET("/common.do")
    fun getWinningNumber(
        @Query("drwNo") drwNum: Int,
        @Query("method") method: String = "getLottoNumber"
    ): Single<LottoData>
}