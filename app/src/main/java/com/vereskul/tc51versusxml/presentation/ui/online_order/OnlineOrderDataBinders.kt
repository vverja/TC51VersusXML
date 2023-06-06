package com.vereskul.tc51versusxml.presentation.ui.online_order

import android.widget.EditText
import androidx.databinding.BindingAdapter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@BindingAdapter("setLocalDateTime")
fun localDateAdapter(text: EditText, date: LocalDateTime?){
    text.setText(date?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))

}