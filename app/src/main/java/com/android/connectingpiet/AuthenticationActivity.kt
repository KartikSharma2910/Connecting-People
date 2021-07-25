package com.android.connectingpiet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        supportFragmentManager.beginTransaction()
            .replace(R.id.auth_fragment_container, fragment_login())
            .commit()
    }
}