package ru.golchin

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.InputStream

class PostCounterMockTest {
    val client = mockk<PagingClient>()
    val stream = mockk<InputStream>()
    val parser = mockk<SearchResultParser>()
    val counter = PostCounter(client, parser)

    @BeforeEach
    internal fun setUp() {
        every { client.doRequest(any(), any(), any(), any()) } answers
                { (lastArg() as (InputStream) -> Boolean)(stream) }
    }

    @Test
    internal fun clientSuccess() {
        every { parser.parse(any()) } returns SearchResult(0, "id")
        val postCounts = counter.calculatePostCounts("abc", 10)
        assertArrayEquals(Array(10) { 0L }, postCounts)
        verify(exactly = 10) { client.doRequest(any(), any(), any(), any()) }
        verify(exactly = 10) { parser.parse(any()) }
    }

    @Test
    internal fun parserFailure() {
        every { parser.parse(any()) } returns SearchResult(0, errorMessage = "xxx")
        val exception = assertThrows<ServiceInvocationException> { counter.calculatePostCounts("", 10) }
        assertEquals("xxx", exception.message)
    }

    @Test
    internal fun clientFailure() {
        every { parser.parse(any()) } returns SearchResult(-1, "id")
        assertThrows<AssertionError> { counter.calculatePostCounts("abc", 9) }
    }
}