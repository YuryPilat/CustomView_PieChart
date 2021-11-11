package com.example.piechartview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import org.w3c.dom.Text
import java.beans.IndexedPropertyChangeEvent
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    private lateinit var pieChartView : PieChartView
    private lateinit var textView : TextView
    private lateinit var editText: EditText
    private lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textview)
        editText = findViewById(R.id.ed_txt)
        button = findViewById(R.id.button_submit)
        pieChartView = findViewById(R.id.pieChart)

        checkForEmpty()

        editText.doOnTextChanged { text, start, before, count ->
            button.isEnabled = !text.isNullOrEmpty()
        }

        button.setOnClickListener {
            if(textView.isVisible) textView.visibility = GONE
            pieChartView.setPieData(parseValuesFromET(editText.text.toString()))
        }
    }

    private fun checkForEmpty() {
        if (pieChartView.pieIsEmpty()) {
            textView.text = getString(R.string.no_data)
            button.isEnabled = false
        } else {
            textView.visibility = GONE
            button.isEnabled = true
        }
    }

    private fun parseValuesFromET(text: String) : ArrayList<Int> {
        val delim = ","
        val valuesList = ArrayList<Int>()
        val stringValuesArray = Pattern.compile(delim).split(text)
        (stringValuesArray.indices).forEach {
            valuesList.add(stringValuesArray[it].toInt())
        }
        return valuesList
    }
}