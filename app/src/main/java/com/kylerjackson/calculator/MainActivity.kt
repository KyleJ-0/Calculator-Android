package com.kylerjackson.calculator

import androidx.fragment.app.FragmentActivity
import android.os.Bundle
import android.util.Log

class MainActivity : FragmentActivity(), InputPad.InputPadListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onButtonClick(text:String){
        val displayFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as DisplayFragment

        displayFragment.changeText(text)
    }

}