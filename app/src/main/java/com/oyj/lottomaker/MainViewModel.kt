package com.oyj.lottomaker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.oyj.lottomaker.model.LottoData
import com.oyj.lottomaker.model.LottoDatabase
import com.oyj.lottomaker.model.WinningNumber
import com.oyj.lottomaker.retrofit.LottoApi
import com.oyj.lottomaker.retrofit.RetrofitClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import kotlin.random.Random

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = LottoDatabase.getDatabase(application)
    private val lottoDao = db.lottoDao()
    private var compositeDisposable = CompositeDisposable()

    private val _arrWinningNumberList = MutableLiveData<ArrayList<ArrayList<Int>>>()
    val arrWinningNumberList: LiveData<ArrayList<ArrayList<Int>>> = _arrWinningNumberList

    private val _winningNumberList = MutableLiveData<ArrayList<Int>>()
    val winningNumberList: LiveData<ArrayList<Int>> = _winningNumberList

    val numberGroupCnt = MutableLiveData<String>()

    private val _selectNumber = MutableLiveData<ArrayList<Int>>()
    val selectNumber: LiveData<ArrayList<Int>> = _selectNumber

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    // cnt만큼 당첨 번호 생성
    fun createWinningNumberAll() {

        val arrNumberList = arrayListOf<ArrayList<Int>>()
        val cnt = numberGroupCnt.value?.toInt() ?: 1

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

    fun createWinningNumberPart(selectNumberList: ArrayList<Int>) {
        val arrNumberList = arrayListOf<ArrayList<Int>>()
        val cnt = numberGroupCnt.value?.toInt() ?: 1

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

    fun initDatabase() {
        // db에 방송 최근 회차 까지 있나 확인.
        // case1. db의 최근 회차 ~ 방송 최근 회차 (db가 비어 있다면 1 ~ 방송 최근 회차 까지 진행)
        // case2. 최신으로 업데이트 되어있다면 패스!
        val single = Single.create{ it.onSuccess(getLastRound())}
        // db에 있는 마지막 라운드
        val postRound = lottoDao.getLastRound() ?: 0

        // todo 다이얼로그, 터치 방지, 에러 표시
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ currentRound ->
                if(postRound != currentRound) {
                    saveAllWinningNumber(postRound, currentRound)
                }
            },{
                it.printStackTrace()
            }).also { compositeDisposable.add(it) }
    }

    // 모든 회차의 정보 db저장.
    private fun saveAllWinningNumber(postRound: Int, currentRound: Int) {
        val client = RetrofitClient.getInstance()
        val api = client.create(LottoApi::class.java)
        val arr = arrayListOf<Observable<LottoData>>()
        val winningNumberList = arrayListOf<WinningNumber>()
        var roundCnt = postRound + 1

        for(i: Int in roundCnt .. currentRound) {
            arr.add(api.getWinningNumber(i).toObservable())
        }

        Observable.fromIterable(arr)
            .concatMap { Observable.defer{it} }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                winningNumberList.add(
                    WinningNumber(
                    round = roundCnt++,
                    winningNum1 = it.drwtNo1,
                    winningNum2 = it.drwtNo2,
                    winningNum3 = it.drwtNo3,
                    winningNum4 = it.drwtNo4,
                    winningNum5 = it.drwtNo5,
                    winningNum6 = it.drwtNo6,
                )
                )
            },{
                it.printStackTrace()
            },{
                println(roundCnt)
                lottoDao.insertWinningNumberList(winningNumberList)
            }).also { compositeDisposable.add(it) }
    }

    private fun getLastRound(): Int {
        val url = "https://dhlottery.co.kr/gameResult.do?method=byWin"
        val doc = Jsoup.connect(url).timeout(10000).get()
        val contentData : Elements = doc.select("div.win_result h4 strong")

        return contentData.text().replace("\\D".toRegex(), "").toInt()
    }
}