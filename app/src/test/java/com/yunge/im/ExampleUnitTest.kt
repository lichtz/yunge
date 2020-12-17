package com.yunge.im

import android.util.Log
import com.yunge.im.util.PhoneUtil
import org.junit.Test

import org.junit.Assert.*
import java.security.SecureRandom

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun cc() {
        val secureRandom = SecureRandom()
        var i = 0;
        while (i < 100) {
            i++;
            val nextInt = secureRandom.nextInt(100000000)
            System.out.println("getRandom: " + nextInt)
        }

    }
}