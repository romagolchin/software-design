package ru.golchin

import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class Test {
    private fun <T> anyObject(): T {
        Mockito.anyObject<T>()
        return uninitialized()
    }

    private fun <T> uninitialized(): T = null as T

//    @Mock
    var mockedBackend = Mockito.mock(Backend::class.java)

    @Test
    fun myTest() {
        `when`(mockedBackend.login(anyObject())).thenAnswer { }
    }

    open class Backend {
        fun login(x: Any) {

        }
    }
}