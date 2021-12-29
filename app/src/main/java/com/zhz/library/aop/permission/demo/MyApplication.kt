package com.zhz.library.aop.permission.demo

import android.app.Application
import com.blankj.utilcode.util.Utils

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}