package com.hm.camerademo

import android.app.Application
import android.content.Context

class App : Application() {


    companion object {

        private var mContext: Application? = null

        @JvmStatic
        val instance: Context?
            get() = mContext
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
    }


}
