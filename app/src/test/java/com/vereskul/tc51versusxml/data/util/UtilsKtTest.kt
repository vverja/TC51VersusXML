package com.vereskul.tc51versusxml.data.util

import org.junit.Test

import org.junit.Assert.*

class UtilsKtTest {

    @Test
    fun checkBarcode() {
        assertFalse(checkBarcode("123"))
        assertFalse(checkBarcode("аываыва"))

        assert(checkBarcode("2005207500009"))
        assert(checkBarcode("2005444300004"))
        assertFalse(checkBarcode("2005207500008"))
    }
}