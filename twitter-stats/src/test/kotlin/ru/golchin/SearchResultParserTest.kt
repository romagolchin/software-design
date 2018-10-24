package ru.golchin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class SearchResultParserTest {
    private val parser = SearchResultParser()
    @Test
    internal fun success() {
        val searchResult = parser.parse(javaClass.getResourceAsStream("/parse_normal.json"))
        assertEquals(1, searchResult.postCount)
        assertEquals("1", searchResult.nextId)
    }

    @Test
    internal fun error() {
        val errorJson = javaClass.getResourceAsStream("/error.json")
        val exception = assertThrows<ServiceInvocationException> {
            parser.parse(errorJson)
        }
        assertEquals("User authorization failed: no access_token passed.", exception.message)
    }
}