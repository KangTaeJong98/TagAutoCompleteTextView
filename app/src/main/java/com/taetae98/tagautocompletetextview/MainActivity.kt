package com.taetae98.tagautocompletetextview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.taetae98.module.view.TagAutoCompleteTextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view = findViewById<AutoCompleteTextView>(R.id.tag_view)

        view.setAdapter(
            ArrayAdapter(
                this, android.R.layout.simple_spinner_dropdown_item, arrayOf(
                    "@taetae98", "@kangtaejong98"
                )
            )
        )
    }
}