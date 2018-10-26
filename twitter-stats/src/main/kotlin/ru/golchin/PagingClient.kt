package ru.golchin

import org.apache.http.HttpStatus.SC_OK
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.HttpClients
import java.io.IOException
import java.io.InputStream
import java.lang.Math.max
import java.lang.Thread.sleep
import java.net.URI
import kotlin.system.measureTimeMillis

open class PagingClient(private val host: String,
                   private val port: Int? = null,
                   private val scheme: String = "https") {
    private fun buildUri(endpoint: String, requestParams: Map<String, String>): URI {
        val builder = URIBuilder()
                .setScheme(scheme)
                .setHost(host)
                .setPath("method/$endpoint")
        if (port != null)
            builder.port = port
        requestParams.forEach {
            builder.setParameter(it.key, it.value)
        }
        return builder.build()
    }

    /*
    pageHandler: specifies action that is done with each page,
    returns if we need next page
     */
    fun doRequest(endpoint: String,
                  requestParams: MutableMap<String, String>,
                  maxCallsPerSecond: Int = 1,
                  pageHandler: (InputStream) -> Boolean = { _ -> false }) {
        if (maxCallsPerSecond <= 0)
            throw IllegalArgumentException("expected positive maxCallsPerSecond, got $maxCallsPerSecond")
        val waitIntervalMillis = 1000 / maxCallsPerSecond + 1
        var keepGoing = true
        while (keepGoing) {
            val elapsedMillis = measureTimeMillis {
                val httpGet = HttpGet(buildUri(endpoint, requestParams))
                val httpResponse: CloseableHttpResponse?
                try {
                    httpResponse = HttpClients.createDefault().execute(httpGet)
                    val statusCode = httpResponse.statusLine.statusCode
                    if (statusCode != SC_OK)
                        throw ServiceInvocationException("unexpected error: $statusCode " +
                                "while handling request: ${httpGet.uri}")
                    keepGoing = pageHandler(httpResponse.entity.content)
                } catch (e: IOException) {
                    throw ServiceInvocationException("i/o error while connecting to ${httpGet.uri}. " +
                            "make sure you are connected to the internet")
                }
            }
            sleep(max(0, waitIntervalMillis - elapsedMillis))
        }
    }
}

class ServiceInvocationException(uri: String) : Exception(uri)