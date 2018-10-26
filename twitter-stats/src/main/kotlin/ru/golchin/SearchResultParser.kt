package ru.golchin

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.InputStream

class SearchResult(val postCount: Int, val nextId: String? = null, val errorMessage: String? = null)

open class SearchResultParser {
    private val mapper = ObjectMapper()

    internal fun parse(inputStream: InputStream): SearchResult {
        inputStream.use { stream ->
            val jsonNode: JsonNode = mapper.readTree(stream)
            val response = jsonNode.get("response")
            var next: JsonNode? = null
            var size = 0
            var message: String? = null
            if (response != null) {
                next = jsonNode.get("next_from")
                size = response["items"].size()
            } else {
                message = jsonNode.get("error")?.get("error_msg")?.asText()
            }
            return SearchResult(size, next?.asText(), message)
        }
    }
}