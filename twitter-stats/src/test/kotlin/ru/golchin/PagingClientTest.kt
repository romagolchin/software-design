package ru.golchin

import com.xebialabs.restito.builder.stub.StubHttp.whenHttp
import com.xebialabs.restito.semantics.Action.stringContent
import com.xebialabs.restito.semantics.ActionSequence.sequence
import com.xebialabs.restito.semantics.Condition.startsWithUri
import com.xebialabs.restito.server.StubServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.IllegalArgumentException
import java.util.stream.Collectors.joining



class PagingClientTest {
    private val PORT = 8080
    private val client = PagingClient("localhost", PORT, "http")
    private var stubServer: StubServer? = null

    @BeforeEach
    internal fun setUp() {
        stubServer = StubServer(PORT).run()
    }

    @AfterEach
    internal fun tearDown() {
        stubServer?.stop()
    }

    @Test
    internal fun success() {
        whenHttp(stubServer).match(startsWithUri("/method/search"))
                .then(sequence(
                        stringContent("a"),
                        stringContent("")))
        var count = 0L
        client.doRequest("search",
                mutableMapOf(),
                100,
                fun(inputStream: InputStream): Boolean {
                    inputStream.use { stream ->
                        BufferedReader(InputStreamReader(stream)).use {
                            val str = it.lines().collect(joining())
                            count += str.length
                            return str.isNotEmpty()
                        }
                    }
                }
        )
        assertEquals(1, count)
    }

    @Test
    internal fun failNotFound() {
        val exception = assertThrows<ServiceInvocationException> {
            client.doRequest("search", mutableMapOf())
        }
        assertTrue { exception.message?.contains("404") ?: false }
    }

    @Test
    internal fun failNegativeMaxCalls() {
        assertThrows<IllegalArgumentException> {
            client.doRequest("", mutableMapOf(), -1)
        }
    }
}