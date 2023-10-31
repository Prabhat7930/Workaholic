package com.example.workaholic.activity

import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.workaholic.R
import com.example.workaholic.databinding.ActivityCardDetailsBinding
import com.example.workaholic.utils.Constants

class CardDetailsActivity : BaseActivity() {

    private lateinit var binding : ActivityCardDetailsBinding

    private lateinit var myCardName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorAccent)
        }

        if (intent.hasExtra(Constants.CARD_NAME)) {
            myCardName = intent.getStringExtra(Constants.CARD_NAME).toString()
        }

        binding.etCardNameDetails.setText(myCardName)

        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCardDetail)

        var actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24_white)
            actionBar.title = resources.getString(R.string.update_card)
        }

        binding.toolbarCardDetail.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }
}