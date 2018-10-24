import java.lang.IllegalArgumentException
import java.util.*
import kotlin.NoSuchElementException
import kotlin.test.assertEquals

class LRUCacheImpl<K, V>(private val capacity: Int): LRUCache<K, V> {
    init {
        if (capacity < 1) throw IllegalArgumentException(
                "expected non-negative capacity, got $capacity")
    }

    private val map = HashMap<K, Node<Pair<K, V>>>()

    private val valuesOrderedByAge = CustomLinkedList<Pair<K, V>>()

    private fun assertValidSizes() {
        assert(map.size <= capacity) {
            "size of map = ${map.size} exceeds capacity = $capacity"
        }
        assert(map.size == valuesOrderedByAge.size) {
            "map size = ${map.size} isn't equal to list size = ${valuesOrderedByAge.size}"
        }
    }

    override fun get(key: K): V? {
        return map[key]?.let { entry ->
            valuesOrderedByAge.remove(entry)
            valuesOrderedByAge.addFirst(entry)
            assertEquals(entry, valuesOrderedByAge.first())
            assertValidSizes()
            entry.value
        }?.second
    }

    override fun put(key: K, value: V) {
        map[key]?.let { node ->
            node.value = key to value
            valuesOrderedByAge.remove(node)
        } ?: run {
            val node = Node(key to value)
            if (map.size == capacity) {
                val last = valuesOrderedByAge.removeLast()
                map.remove(last?.value?.first)
            }
            map[key] = node
        }
        map[key]!!.let { valuesOrderedByAge.addFirst(it) }
        val mapping = map[key]?.value?.second
        assert(mapping == value) {
            "mapping for key $key is wrong: $mapping}"
        }
        assertValidSizes()
    }
}

internal class CustomLinkedList<E> {
    internal var size: Int = 0

    private var head: Node<E> = Node(null)

    private var tail: Node<E> = Node(null)

    init {
        head.next = tail
        tail.prev = head
    }

    fun remove(node: Node<E>) {
        node.prev?.next = node.next
        node.next?.prev = node.prev
        size--
    }

    fun addFirst(node: Node<E>) {
        val first = head.next
        node.next = first
        first?.prev = node
        head.next = node
        node.prev = head
        size++
    }

    fun removeLast(): Node<E>? {
        val last = tail.prev
        val lastButOne = last?.prev
        if (last != head) {
            lastButOne?.next = tail
            tail.prev = lastButOne
            size--
            return last
        } else throw NoSuchElementException()
    }

    fun first(): Node<E>? = head.next

}

internal class Node<E>(var value: E?,
                       var next: Node<E>? = null,
                       var prev: Node<E>? = null)
