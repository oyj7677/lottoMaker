package com.example.lottomaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lottomaker.util.ActivityUtils

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var fragment = supportFragmentManager.findFragmentById(R.id.contentFrame) as MainFragment?
        if(fragment == null) {
            fragment = MainFragment.newInstance()
            ActivityUtils.addFragmentToActivity(supportFragmentManager, fragment, R.id.contentFrame)
        }
    }
}