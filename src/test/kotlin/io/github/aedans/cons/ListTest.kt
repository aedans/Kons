package io.github.aedans.cons

import org.testng.Assert
import org.testng.annotations.Test

@Test
class ListTest {
    fun toString1() = Assert.assertEquals((1 cons (2 cons (3 cons Nil))).toString(), "[1, 2, 3]")
    fun toString2() = Assert.assertEquals(1.cons { 2.cons { 3.cons { Nil } } }.toString(), "[1, 2, 3]")
    fun toString3() = Assert.assertEquals(({ 1 } cons { { 2 } cons { { 3 } cons { Nil } } }).toString(), "[1, 2, 3]")
    fun toString4() = Assert.assertEquals(consOf().toString(), "nil")
    fun toCons() = Assert.assertEquals(listOf(1, 2, 3).toCons(), 1 cons (2 cons (3 cons Nil)))
    fun consOf1() = Assert.assertEquals(consOf(1, 2, 3), 1 cons (2 cons (3 cons Nil)))
    fun consOf2() = Assert.assertEquals(consOf(), Nil)
    fun size() = Assert.assertEquals((0 until 100).toCons().size, 100)
    fun take() = Assert.assertEquals((0 until 105).take(100).size, 100)
    fun cons() = Assert.assertEquals((0 until 100).toCons(), cons(100) { it })
    fun contains1() = Assert.assertEquals((0..100).toCons().contains(100), true)
    fun contains2() = Assert.assertEquals((0 until 100).toCons().contains(100), false)
    fun indexOf1() = Assert.assertEquals((0..100).toCons().indexOf(100), 100)
    fun indexOf2() = Assert.assertEquals((0 until 100).toCons().indexOf(100), -1)
    fun append() = Assert.assertEquals((0..100).toCons(), (0 until 100).toCons() append 100)
    fun prepend() = Assert.assertEquals((0..100).toCons(), (0..50).toCons() prependTo (51..100).toCons())

    fun infiniteLazyCons() = object : Iterator<Int> {
        var i = 0
        override fun hasNext() = true
        override fun next() = i++
    }.collectToCons().let {  }

    fun stackSafe1() = (0..10000).toCons()[10000].let {  }
    fun stackSafe2() = cons(10001) { it }[10000].let {  }
    fun stackSafe3() = ((0 until 10000).toCons() + 10000)[10000].let {  }
    fun stackSafe4() = (0..10000).toCons().contains(10000).let {  }
    fun stackSafe5() = (0..10000).toCons().indexOf(10000).let {  }
    fun stackSafe6() = (0..10000).toCons().lastIndexOf(10000).let {  }
    fun stackSafe7() = ((0 until 10000).toCons() append 1)[10000].let {  }
    fun stackSafe8() = ((0..10000).toCons() prependTo (0..1).toCons())[10000].let {  }
}