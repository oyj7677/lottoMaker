package com.example.lottomaker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelectNumberDialogViewModel: ViewModel() {

    private val _selectNumbers = ListLiveDAta<Int>()
    val selectNumbers: LiveData<ArrayList<Int>> = _selectNumbers

    fun clickNumber(num: Int) {

        if(_selectNumbers.contains(num)) {
            // 존재함 -> 삭제
            _selectNumbers.remove(num)

        } else {
            // 존재하지 않음 -> 추가
            if(_selectNumbers.size() < 6) {
                _selectNumbers.add(num)
            }
        }
    }
}