import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.*

internal class LRUCacheTest {
    private fun makeCache(capacity: Int) = LRUCacheImpl<String, Int>(capacity)

    @Test
    internal fun nonPositiveCapacity() {
        assertThrows(Exception::class.java) { LRUCacheImpl<Int, String>(0) }
    }

    @Test
    internal fun oldKeyDeletionTest() {
        val cache = makeCache(2)
        cache.put("ab", 1)
        cache.put("bc", 2)
        cache.get("ab")
        cache.put("ac", 3)
        assertEquals(1, cache.get("ab"))
        assertEquals(3, cache.get("ac"))
        assertEquals(null, cache.get("bc"))
    }

    @Test
    internal fun replacementTest() {
        val cache = makeCache(5)
        cache.put("ab", 1)
        cache.put("ab", 2)
        assertEquals(2, cache.get("ab"))
    }

    @Test
    internal fun random() {
        val cache = LRUCacheImpl<Int, Double>(16)
        val expectedCache = SimpleCache<Int, Double>(16)
        val random = Random()
        repeat(10_000) {
            if (random.nextBoolean()) {
                val value = random.nextGaussian()
                val key = random.nextInt()
                cache.put(key, value)
                expectedCache.put(key, value)
            } else {
                val key = random.nextInt()
                assertEquals(expectedCache.get(key), cache.get(key))
            }
        }
    }
}

internal class SimpleCache<K, V>(private val capacity: Int): LRUCache<K, V> {
    private val map = LinkedHashMap<K, V>(16, .75F, true)

    override fun get(key: K): V? = map[key]

    override fun put(key: K, value: V) {
        if (map.size == capacity) {
            val iterator = map.iterator()
            iterator.next()
            iterator.remove()
        }
        map[key] = value
    }
}