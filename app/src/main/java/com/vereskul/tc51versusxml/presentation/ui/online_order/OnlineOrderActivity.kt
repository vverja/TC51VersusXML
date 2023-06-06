package com.vereskul.tc51versusxml.presentation.ui.online_order

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vereskul.tc51versusxml.R
import com.vereskul.tc51versusxml.databinding.ActivityOnlineOrderBinding

class OnlineOrderActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityOnlineOrderBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction()
            .replace(binding.onlineOrderContainer.id,OnlineOrderFragment.newInstance())
            .commit()
    }

    companion object{
        const val TAG = "OnlineOrderActivity"
        fun newInstance(context: Context):Intent{
            return Intent(context, OnlineOrderActivity::class.java)
        }
    }
}