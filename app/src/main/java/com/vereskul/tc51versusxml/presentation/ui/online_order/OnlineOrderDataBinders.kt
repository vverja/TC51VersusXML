package com.vereskul.tc51versusxml.presentation.ui.online_order

import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@BindingAdapter("setLocalDateTime")
fun localDateAdapter(text: EditText, date: LocalDateTime?){
    text.setText(date?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss")))
}

@InverseBindingAdapter(attribute = "app:setLocalDateTime", event = "android:textAttrChanged")
fun getDateAdapter(editText: EditText): LocalDateTime{
    val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
    return LocalDateTime.parse(editText.text.toString(), dateTimeFormatter)
}
