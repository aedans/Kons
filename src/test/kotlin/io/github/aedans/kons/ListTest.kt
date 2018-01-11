package io.github.aedans.kons

import org.testng.Assert.assertEquals
import org.testng.annotations.Test

@Test
class ListTest {
    fun toCons1() = assertEquals((0..10000).toCons().eval().toList(), (0..10000).toList())
    fun toStringT() = assertEquals((0..10000).toCons().toString(), (0..10000).toList().toString())
    fun consOf1() = assertEquals(consOf(1, 2, 3), 1 cons (2 cons (3 cons Nil)))
    fun consOf2() = assertEquals(consOf(), Nil)
    fun size() = assertEquals((0 until 10000).toCons().size, 10000)
    fun contains1() = assertEquals((0..10000).toCons().contains(10000), true)
    fun contains2() = assertEquals((0 until 10000).toCons().contains(10000), false)
    fun indexOf1() = assertEquals((0..10000).toCons().indexOf(10000), 10000)
    fun indexOf2() = assertEquals((0 until 10000).toCons().indexOf(10000), -1)
    fun plus1() = assertEquals((0..10000).toCons().eval(), ((0 until 10000).toCons() + 10000).eval())
    fun plus2() = assertEquals((0..10000).toCons().eval(), ((0..5000).toCons() + (5001..10000).toCons()).eval())

    private fun <T> Cons<T>.eval() = this[10000].let { this }

    fun infiniteLazyCons() = object : Iterator<Int> {
        var i = 0
        override fun hasNext() = true
        override fun next() = i++
    }.collectToCons().let {  }
}
