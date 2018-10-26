package ru.golchin

import io.mockk.Runs
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.just
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import java.io.InputStream
import java.lang.IllegalArgumentException

internal class PostCounterTest {
    private val counter = PostCounter()

    @Test
    fun calculatePostCounts() {
        assertTrue {
            counter.calculatePostCounts("деньучителя", 10)
                    .asSequence().all { x -> x >= 0 }
        }
    }

    @Test
    internal fun failBadArgument() {
        assertThrows<IllegalArgumentException> {
            counter.calculatePostCounts("", -1)
        }
    }
}