package ru.golchin

import java.nio.file.Files
import java.nio.file.Paths

class PostCounter(private val client: PagingClient = PagingClient("api.vk.com")) {
    private val parser: SearchResultParser = SearchResultParser()

    private var accessToken: String = ""

    init {
        Files.newBufferedReader(Paths.get("access_token")).use {
            accessToken = it.readLine()
        }
    }

    fun calculatePostCounts(hashtag: String, hours: Int): Array<Long> {
        if (hours < 0 || hours > 24)
            throw IllegalArgumentException("specify between 1 and 24 hours")
        val result = Array(hours) { 0L }
        var now = System.currentTimeMillis() / 1000
        val requestParams = mutableMapOf("q" to "#$hashtag",
                "v" to "5.85",
                "access_token" to accessToken)
        repeat(hours) {
            requestParams["end_time"] = now.toString()
            now -= 3600
            requestParams["start_time"] = now.toString()
            var count = 0L
            client.doRequest("newsfeed.search", requestParams, 3) { stream ->
                val postPage = parser.parse(stream)
                count += postPage.postCount
                requestParams["start_from"] = postPage.nextId ?: return@doRequest false
                true
            }
            result[result.size - it - 1] = count
        }
        return result
    }
}