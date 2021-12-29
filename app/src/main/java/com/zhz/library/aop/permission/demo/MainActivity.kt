package com.zhz.library.aop.permission.demo

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.zhz.library.aop.permission.annotation.NeedPermission

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.mTextView).setOnClickListener {
            test()
        }
    }

    @NeedPermission(value = [Manifest.permission.CAMERA])
    fun test() {
        Log.d("zhuanghz", "test: ~~~~~~~~~~~")
    }
}