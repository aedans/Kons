package io.github.aedans.cons

import org.testng.Assert
import org.testng.annotations.Test

@Test
class ListTest {
    fun toString1() = Assert.assertEquals((1 cons (2 cons (3 cons Nil))).toString(), "[1, 2, 3]")
    fun toString2() = Assert.assertEquals(1.cons { 2.cons { 3.cons { Nil } } }.toString(), "[1, 2, 3]")
    fun toString3() = Assert.assertEquals(consOf().toString(), "nil")
    fun toCons() = Assert.assertEquals(listOf(1, 2, 3).toCons(), 1 cons (2 cons (3 cons Nil)))
    fun consOf1() = Assert.assertEquals(consOf(1, 2, 3), 1 cons (2 cons (3 cons Nil)))
    fun consOf2() = Assert.assertEquals(consOf(), Nil)
    fun size() = Assert.assertEquals((0 until 10000).toCons().size, 10000)
    fun infiniteLazyCons() = object : Iterator<Int> {
        var i = 0
        override fun hasNext() = true
        override fun next() = i++
    }.collectToCons().let {  }

    fun stackSafe1() = (0..10000).toCons()[10000].let {  }
    fun stackSafe2() = cons(10001) { it }[10000].let {  }
    fun stackSafe3() = generateCons(10000) { it + 1 }[10000].let {  }
    fun stackSafe4() {
        var it = 0
        generateCons { it++ }[10000].let {  }
    }
}