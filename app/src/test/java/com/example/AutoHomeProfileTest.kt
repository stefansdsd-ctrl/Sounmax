package com.example

import com.example.data.AutoHomeProfile
import org.junit.Assert.assertEquals
import org.junit.Test

class AutoHomeProfileTest {
    @Test
    fun morningAndEveningAreSport() {
        assertEquals("sport", AutoHomeProfile.suggestedId(7))
        assertEquals("sport", AutoHomeProfile.suggestedId(18))
    }

    @Test
    fun workdayIsWerk() {
        assertEquals("werk", AutoHomeProfile.suggestedId(11))
        assertEquals("werk", AutoHomeProfile.suggestedId(15))
    }

    @Test
    fun nightIsSlaap() {
        assertEquals("slaap", AutoHomeProfile.suggestedId(23))
        assertEquals("slaap", AutoHomeProfile.suggestedId(2))
    }
}
