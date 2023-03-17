package com.example.lottomaker

import android.annotation.SuppressLint
import com.example.lottomaker.retrofit.LottoApi
import com.example.lottomaker.retrofit.RetrofitClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import retrofit2.Retrofit
import kotlin.random.Random

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    fun printNum(arrNumberList : ArrayList<ArrayList<Int>>) {
        var strNumberList = ""
        for(numberList in arrNumberList) {
            for(number in numberList) {
                strNumberList += if(numberList.last() == number) {
                    // 마지막 수
                    "$number\n"
                } else {
                    "$number, "
                }
            }
        }
        println(strNumberList)
    }
    @Test
    fun createWinningNumber() {
        // 랜덤 -> list
        val numberList = arrayListOf<Int>()

        while(numberList.size <= 6) {
            val number = Random.nextInt(46)
            if(!numberList.contains(number) && number > 0) {
                numberList.add(number)
            }
        }
        numberList.sort()

        for(num in numberList) {
            print("$num, ")
        }
    }

    @Test
    fun selectNumTest() {
        val selectNumList = arrayListOf<Int>()
        selectNumList.add(2)
        selectNumList.add(3)
        createWinningNumberPart(selectNumList, 4)
    }

    private fun createWinningNumberPart(selectNumberList: ArrayList<Int>, cnt: Int) {
        val arrNumberList = arrayListOf<ArrayList<Int>>()

        for(i: Int in 0 until cnt) {
            val numberList = arrayListOf<Int>()
            numberList.addAll(selectNumberList)
            while(numberList.size < 6) {
                val number = Random.nextInt(46)
                if(!numberList.contains(number) && number > 0) {
                    numberList.add(number)
                }
            }
            numberList.sort()
            arrNumberList.add(numberList)
        }

        printNum(arrNumberList)
    }

    @Test
    fun createMostChosenNumber() {
        // Array[46]
        val selectNumCntList = Array(46){0}

        for(i: Int in 0 until 6000) {
            val numberList = arrayListOf<Int>()
            while(numberList.size < 6) {
                val number = Random.nextInt(46)
                if(!numberList.contains(number) && number > 0) {
                    numberList.add(number)
                }
            }
            numberList.sort()
            for(number in numberList) {
                selectNumCntList[number] += 1
            }
        }
        val mostSelectNumberList = arrayListOf<Int>()

        for(i: Int in 0 until 6) {
            val maxCnt = selectNumCntList.max()
            val maxIndex = selectNumCntList.indexOf(maxCnt)
            mostSelectNumberList.add(maxIndex)
            selectNumCntList[maxIndex] = 0
        }
        mostSelectNumberList.sort()
    }


    private lateinit var client: Retrofit
    private lateinit var api: LottoApi

    @Before
    fun setup() {
        client = RetrofitClient.getInstance()
        api = client.create(LottoApi::class.java)
    }

    @SuppressLint("CheckResult")
    @Test
    fun retrofitTest() {
        api.getWinningNumber(100)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                println("${it.drwtNo1}, ${it.drwtNo2}, ${it.drwtNo3}, ${it.drwtNo4}, ${it.drwtNo5}, ${it.drwtNo6} + ${it.bnusNo}")
            },{
                it.printStackTrace()
            })
    }

    @Test
    fun getLastRound() {
        val url = "https://dhlottery.co.kr/gameResult.do?method=byWin"
        val doc = Jsoup.connect(url).timeout(10000).get()
        val contentData : Elements = doc.select("div.win_result h4 strong")
        println(contentData.text().replace("\\D".toRegex(), "").toInt())
    }
}
