package ru.golchin

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.io.InputStream
import java.lang.IllegalArgumentException

internal class PostCounterTest {
    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }
    private fun <T> uninitialized(): T = null as T

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

    @Test
    internal fun mockClient() {
        val client = mock(PagingClient::class.java)
        val stream = mock(InputStream::class.java)
        `when`(client.doRequest(any(), any(), any(), any())).thenAnswer { invocation ->
            (invocation.getArgument(3) as (InputStream) -> Boolean)(stream)
        }
        val parser = mock(SearchResultParser::class.java)
        var curNumber = 0
        `when`(parser.parse(any())).thenReturn(SearchResult(++curNumber, "id"))
        val counterWithMock = PostCounter(client)
        val postCounts = counterWithMock.calculatePostCounts("abc", 10)
        assertEquals((1L..10).toList().toTypedArray(), postCounts)
        verify(client, times(10)).doRequest(any(), any())
    }
}