package com.vereskul.tc51versusxml.data.util

fun checkBarcode(barcode: String): Boolean {
    try {
        val charArray = barcode.toCharArray()
        if (charArray.size!=13) return false
        val charList = charArray.toMutableList()
        val lastDigit = charList.removeAt(charList.size - 1).digitToInt()

        val digitsList = charList.map {
            it.digitToInt()
        }
        val evenValue = digitsList.filterIndexed { index, _ -> (index+1)%2==0 }
            .sum() * 3
        val oddValue = digitsList.filterIndexed{index, _ -> (index+1)%2!=0 }.sum()
        var reminder = (evenValue + oddValue) % 10
        if (reminder!=0){
            reminder = 10 - reminder
        }
        return reminder==lastDigit
    }catch (e: NumberFormatException){
        return false
    }
}