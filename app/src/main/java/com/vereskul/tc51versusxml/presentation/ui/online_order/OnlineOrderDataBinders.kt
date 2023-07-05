package com.vereskul.tc51versusxml.presentation.ui.online_order

import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@BindingAdapter("setLocalDateTime")
fun localDateAdapter(text: EditText, date: LocalDateTime?){
    text.setText(date?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss")))
}
@BindingAdapter("setLocalDateTime")
fun localDateAdapterTextView(text: TextView, date: LocalDateTime?){
    text.text = date?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss"))
}

@InverseBindingAdapter(attribute = "app:setLocalDateTime", event = "android:textAttrChanged")
fun getDateAdapter(editText: EditText): LocalDateTime{
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
    return LocalDateTime.parse(editText.text.toString(), dateTimeFormatter)
}

@BindingAdapter("setDouble")
fun setDouble(text: EditText, number: Double?){
    text.setText(number?.toString())
}

@BindingAdapter("setDouble")
fun setDouble(text: TextView, number: Double?){
    text.text = number?.toString()
}

@InverseBindingAdapter(attribute = "app:setDouble", event = "android:textAttrChanged")
fun getDouble(editText: EditText): Double = try {
        editText.text.toString().toDouble()
    }catch (e: Exception){
        0.0
    }
