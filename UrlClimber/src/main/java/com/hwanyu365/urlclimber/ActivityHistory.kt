package com.hwanyu365.urlclimber

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class ActivityHistory : AppCompatActivity() {

    private var mRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setSupportActionBar(findViewById(R.id.toolbar))

        mRecyclerView = findViewById(android.R.id.list)
    }
}