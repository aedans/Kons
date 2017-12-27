package io.github.aedans.cons

/**
 * A linked list implementation using cons cells. While operations such as get and append are much slower than normal
 * lists, operations such as car and cdr (A.K.A. head and tail) are much faster, allowing for fast recursive traversal.
 * Cons lists are always terminated by the Nil (empty) list.
 *
 * More information can be found at https://en.wikipedia.org/wiki/Cons
 *
 * Unlike traditional Lisp cons lists, this implementation can be lazy, and by extension infinite.
 */
sealed class Cons<out T> : List<T> {
    /**
     * The head of the cons list.
     */
    abstract val car: T

    /**
     * The tail of the cons list.
     */
    abstract val cdr: Cons<T>

    /**
     * The head of the cons list, or null if the list is empty.
     */
    abstract val safeCar: T?

    /**
     * The tail of the cons list, or null if the list is empty.
     */
    abstract val safeCdr: Cons<T>?

    operator fun component1() = car
    operator fun component2() = cdr

    override val size get() = run {
        tailrec fun sizeImpl(acc: Int, cons: Cons<T>): Int = when (cons) {
            Nil -> acc
            is Cell<T> -> sizeImpl(acc + 1, cons.cdr)
        }
        sizeImpl(0, this)
    }

    override operator fun get(index: Int) = run {
        tailrec fun Cons<T>.getImpl(i: Int): T = when (i) {
            0 -> car
            else -> cdr.getImpl(i - 1)
        }
        if (index < 0) throw IndexOutOfBoundsException(index.toString()) else getImpl(index)
    }

    override fun contains(element: @UnsafeVariance T) = run {
        tailrec fun containsImpl(cons: Cons<T>): Boolean = when {
            cons == Nil -> false
            cons.car == element -> true
            else -> containsImpl(cons.cdr)
        }
        containsImpl(this)
    }

    override fun containsAll(elements: Collection<@UnsafeVariance T>) = elements.all(this::contains)

    override fun indexOf(element: @UnsafeVariance T) = run {
        tailrec fun indexOfImpl(acc: Int, cons: Cons<T>): Int = when {
            cons == Nil -> -1
            cons.car == element -> acc
            else -> indexOfImpl(acc + 1, cons.cdr)
        }
        indexOfImpl(0, this)
    }

    override fun isEmpty() = this == Nil

    override fun lastIndexOf(element: @UnsafeVariance T) = run {
        tailrec fun lastIndexOfImpl(last: Int, acc: Int, cons: Cons<T>): Int = when {
            cons == Nil -> last
            cons.car == element -> lastIndexOfImpl(acc, acc + 1, cons.cdr)
            else -> lastIndexOfImpl(last, acc + 1, cons.cdr)
        }
        lastIndexOfImpl(-1, 0, this)
    }

    override fun listIterator() = toList().listIterator()
    override fun listIterator(index: Int) = toList().listIterator(index)
    override fun subList(fromIndex: Int, toIndex: Int) = toList().subList(fromIndex, toIndex)

    override fun iterator() = object : Iterator<T> {
        private var current = this@Cons::car
        private var next = cdr
        private var hasNext = true
        override fun hasNext() = hasNext
        override fun next() = current().let {
            if (next == Nil) {
                hasNext = false
            } else {
                current = next::car
                next = next.cdr
            }
            it
        }
    }
}

/**
 * The empty cons list, used as the terminator for all cons lists.
 */
object Nil : Cons<Nothing>() {
    override val car get() = throw IndexOutOfBoundsException()
    override val cdr get() = throw IndexOutOfBoundsException()
    override val safeCar = null
    override val safeCdr = null
    override fun iterator() = emptyList<Nothing>().iterator()
    override fun toString() = "nil"
}

/**
 * Abstract implementation of a Cons cell implementing toString, equals, and hashCode.
 */
abstract class Cell<out T> : Cons<T>() {
    override val safeCar get() = car
    override val safeCdr get() = cdr
    override fun toString() = joinToString(prefix = "[", postfix = "]")
    override fun equals(other: Any?) = other is Cell<*> && other.car == car && other.cdr == cdr
    override fun hashCode() = (car?.hashCode() ?: 0).let { 31 * it + cdr.hashCode() }
}
