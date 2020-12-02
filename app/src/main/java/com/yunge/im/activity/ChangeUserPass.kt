package com.yunge.im.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.yunge.im.R

class ChangeUserPass : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_user_pass)
        findViewById<View>(R.id.back).setOnClickListener { finish() }
    }
}