package com.oyj.lottomaker

import androidx.lifecycle.MutableLiveData


class ListLiveDAta<T> : MutableLiveData<ArrayList<T>>() {

    init {
        value = arrayListOf<T>()
    }

    fun add(item: T) {
        val items = value
        items!!.add(item)
        value = items
    }

    fun addAll(list: List<T>?) {
        val items = value
        items!!.addAll(list!!)
        value = items
    }

    fun clear(notify: Boolean) {
        val items = value
        items!!.clear()
        if(notify) {
            value = items
        }
    }

    fun remove(item: T) {
        val items = value
        items!!.remove(item)
        value = items
    }

    fun notifyChange() {
        val items = value
        value = items
    }

    fun contains(item: T): Boolean {
        val items = value
        return items!!.contains(item)
    }

    fun size(): Int {
        val items = value
        return items!!.size
    }
}