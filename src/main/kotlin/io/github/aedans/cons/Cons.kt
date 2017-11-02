package io.github.aedans.cons

/**
 * A linked list implementation using cons cells. While operations such as get and append are much slower than normal
 * lists, operations such as car and cdr (A.K.A. head and tail) are much faster, allowing for fast recursive traversal.
 * Cons lists are always terminated by the Nil (empty) list.
 *
 * More information can be found at https://en.wikipedia.org/wiki/Cons
 *
 * Unlike traditional lisp cons lists, this implementation can be lazy, and by extension infinite.
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

    operator fun component1() = car
    operator fun component2() = cdr

    override val size get() = run {
        tailrec fun getSize(acc: Int, cons: Cons<T>): Int = when (cons) {
            Nil -> acc
            else -> getSize(acc + 1, cons.cdr)
        }
        getSize(0, this)
    }

    override operator fun get(index: Int) = run {
        tailrec fun Cons<T>.getGet(i: Int): T = when (i) {
            0 -> car
            else -> cdr.getGet(i - 1)
        }
        if (index < 0) throw IndexOutOfBoundsException(index.toString()) else getGet(index)
    }

    override fun contains(element: @UnsafeVariance T) = run {
        tailrec fun getContains(cons: Cons<T>): Boolean = when {
            cons == Nil -> false
            cons.car == element -> true
            else -> getContains(cons.cdr)
        }
        getContains(this)
    }

    override fun containsAll(elements: Collection<@UnsafeVariance T>) = elements.all { contains(it) }

    override fun indexOf(element: @UnsafeVariance T) = run {
        tailrec fun getIndexOf(acc: Int, cons: Cons<T>): Int = when {
            cons == Nil -> -1
            cons.car == element -> acc
            else -> getIndexOf(acc + 1, cons.cdr)
        }
        getIndexOf(0, this)
    }

    override fun isEmpty() = this == Nil

    override fun lastIndexOf(element: @UnsafeVariance T) = run {
        tailrec fun getLastIndexOf(last: Int, acc: Int, cons: Cons<T>): Int = when {
            cons == Nil -> last
            cons.car == element -> getLastIndexOf(acc, acc + 1, cons.cdr)
            else -> getLastIndexOf(last, acc + 1, cons.cdr)
        }
        getLastIndexOf(-1, 0, this)
    }

    override fun listIterator() = toList().listIterator()
    override fun listIterator(index: Int) = toList().listIterator(index)
    override fun subList(fromIndex: Int, toIndex: Int) = toList().subList(fromIndex, toIndex)

    override fun iterator() = object : Iterator<T> {
        private var current = car
        private var next = cdr
        private var hasNext = true
        override fun hasNext() = hasNext
        override fun next() = current.let {
            if (next == Nil) {
                hasNext = false
            } else {
                current = next.car
                next = next.cdr
            }
            it
        }
    }
}

/**
 * Abstract implementation of a Cons cell implementing toString, equals, and hashCode.
 */
class Cell<out T>(
        private val carF: () -> T,
        private val cdrF: () -> Cons<T>
) : Cons<T>() {
    override val car get() = carF()
    override val cdr get() = cdrF()
    override fun toString() = joinToString(prefix = "[", postfix = "]")
    override fun equals(other: Any?) = other is Cell<*> && other.car == car && other.cdr == cdr
    override fun hashCode() = (car?.hashCode() ?: 0).let { 31 * it + cdr.hashCode() }
}

/**
 * The empty cons list, used as the terminator for all cons lists.
 */
object Nil : Cons<Nothing>() {
    override val car get() = throw IndexOutOfBoundsException()
    override val cdr get() = throw IndexOutOfBoundsException()
    override fun iterator() = emptyList<Nothing>().iterator()
    override fun toString() = "nil"
}
