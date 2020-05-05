package com.insta.videouploader

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.insta.videouploader.adapter.RVAdapter
import com.insta.videouploader.util.DotsIndicatorDecoration
import com.android.billingclient.api.BillingClient


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        val sharedPref = this.getSharedPreferences("first_run", Context.MODE_PRIVATE)

        if (sharedPref.getBoolean("is_first", true)) {
            setContentView(R.layout.activity_splash)

            val recyclerView = findViewById<RecyclerView>(R.id.cardsRecyclerView)
            recyclerView.isNestedScrollingEnabled = false
            recyclerView.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            var adapter =
                RVAdapter(this, mutableListOf(R.drawable.s1, R.drawable.s2, R.drawable.s3, R.drawable.s4), recyclerView)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter
            val radius = 24
            val dotsHeight = 24
            val color = ContextCompat.getColor(this, R.color.splash)
            recyclerView.addItemDecoration(
                DotsIndicatorDecoration(
                    radius,
                    32,
                    dotsHeight,
                    color,
                    color
                )
            )

            PagerSnapHelper().attachToRecyclerView(recyclerView)

        } else {
            val intent = Intent(this, InfoPage::class.java)
            startActivity(intent)
            this.finish()
        }
    }

}
