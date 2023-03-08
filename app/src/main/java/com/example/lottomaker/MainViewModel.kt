package com.example.lottomaker

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.lottomaker.retrofit.LottoApi
import com.example.lottomaker.retrofit.RetrofitClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.random.Random

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _arrWinningNumberList = MutableLiveData<ArrayList<ArrayList<Int>>>()
    val arrWinningNumberList: LiveData<ArrayList<ArrayList<Int>>> = _arrWinningNumberList

    private val _winningNumberList = MutableLiveData<ArrayList<Int>>()
    val winningNumberList: LiveData<ArrayList<Int>> = _winningNumberList

    // cnt만큼 당첨 번호 생성
    fun createWinningNumberAll(cnt: Int) {
        // 랜덤 -> list
        val arrNumberList = arrayListOf<ArrayList<Int>>()

        for(i: Int in 0 until cnt) {
            val numberList = arrayListOf<Int>()
            while(numberList.size < 6) {
                val number = Random.nextInt(46)
                if(!numberList.contains(number) && number > 0) {
                    numberList.add(number)
                }
            }
            numberList.sort()
            arrNumberList.add(numberList)
        }

        _arrWinningNumberList.value = arrNumberList
    }

    fun createWinningNumberPart(selectNumberList: ArrayList<Int>, cnt: Int) {
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

        _arrWinningNumberList.value = arrNumberList
    }

    fun createMostChosenNumber() {
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
        _winningNumberList.value = mostSelectNumberList
    }

    @SuppressLint("CheckResult")
    fun retrofitTest() {
        val client = RetrofitClient.getInstance()
        val api = client.create(LottoApi::class.java)

        val mostSelectNumberList = arrayListOf<Int>()

        api.getWinningNumber(100)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mostSelectNumberList.add(it.drwtNo1)
                mostSelectNumberList.add(it.drwtNo2)
                mostSelectNumberList.add(it.drwtNo3)
                mostSelectNumberList.add(it.drwtNo4)
                mostSelectNumberList.add(it.drwtNo5)
                mostSelectNumberList.add(it.drwtNo6)
                mostSelectNumberList.add(it.bnusNo)
                _winningNumberList.value = mostSelectNumberList
            },{
                it.printStackTrace()
            })
    }
}