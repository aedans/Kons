package io.github.aedans.kons

import arrow.core.Eval
import arrow.core.Eval.Companion.later

/**
 * A linked list implementation using cons cells. While operations such as get and append are much slower than normal
 * lists, operations such as car and cdr (A.K.A. head and tail) are much faster, allowing for fast recursive traversal.
 * Cons lists are always terminated by the Nil (empty) list.
 *
 * More information can be found at https://en.wikipedia.org/wiki/Cons
 *
 * Unlike traditional Lisp cons lists, this implementation can be lazy, and by extension infinite.
 *
 * Extending classes must implement (car | lazyCar) (cdr | lazyCdr)
 */
sealed class Cons<out T> : List<T> {
    /**
     * The head of the cons list.
     */
    open val car: T? get() = lazyCar.value()

    /**
     * The tail of the cons list.
     */
    open val cdr: Cons<T>? get() = lazyCdr.value()

    open val lazyCar: Eval<T?> get() = later { car }
    open val lazyCdr: Eval<Cons<T>?> get() = later { cdr }

    open operator fun component1() = car
    open operator fun component2() = cdr

    val unsafeCar get() = car ?: throw IndexOutOfBoundsException()
    val unsafeCdr get() = cdr ?: throw IndexOutOfBoundsException()

    override val size
        get() = run {
            tailrec fun sizeImpl(acc: Int, cons: Cons<T>): Int = when (cons) {
                Nil -> acc
                is Cell -> sizeImpl(acc + 1, cons.cdr)
            }
            sizeImpl(0, this)
        }

    override operator fun get(index: Int) = run {
        tailrec fun Cons<T>.getImpl(i: Int): T = when (i) {
            0 -> unsafeCar
            else -> unsafeCdr.getImpl(i - 1)
        }
        if (index < 0) throw IndexOutOfBoundsException(index.toString()) else getImpl(index)
    }

    override fun contains(element: @UnsafeVariance T) = run {
        tailrec fun containsImpl(cons: Cons<T>): Boolean = when (cons) {
            Nil -> false
            is Cell -> if (cons.car == element) true else containsImpl(cons.cdr)
        }
        containsImpl(this)
    }

    override fun containsAll(elements: Collection<@UnsafeVariance T>) = elements.all(this::contains)

    override fun indexOf(element: @UnsafeVariance T) = run {
        tailrec fun indexOfImpl(acc: Int, cons: Cons<T>): Int = when (cons) {
            Nil -> -1
            is Cell -> if (cons.car == element) acc else indexOfImpl(acc + 1, cons.cdr)
        }
        indexOfImpl(0, this)
    }

    override fun isEmpty() = this == Nil

    override fun lastIndexOf(element: @UnsafeVariance T) = run {
        tailrec fun lastIndexOfImpl(last: Int, acc: Int, cons: Cons<T>): Int = when (cons) {
            Nil -> last
            is Cell -> if (cons.car == element)
                lastIndexOfImpl(acc, acc + 1, cons.cdr)
            else
                lastIndexOfImpl(last, acc + 1, cons.cdr)
        }
        lastIndexOfImpl(-1, 0, this)
    }

    override fun listIterator() = toList().listIterator()
    override fun listIterator(index: Int) = toList().listIterator(index)
    override fun subList(fromIndex: Int, toIndex: Int) = toList().subList(fromIndex, toIndex).toCons()

    override fun iterator() = run {
        var next = this@Cons
        object : Iterator<T> {
            override fun hasNext() = next != Nil
            override fun next() = next.unsafeCar.also { next = next.unsafeCdr }
        }
    }
}

/**
 * The empty cons list, used as the terminator for all cons lists.
 */
object Nil : Cons<Nothing>() {
    override val car = null
    override val cdr = null
    override fun iterator() = emptyList<Nothing>().iterator()
    override fun toString() = "nil"
}

/**
 * Abstract implementation of a Cons cell implementing toString, equals, and hashCode.
 *
 * Extending classes must implement (car | lazyCar) (cdr | lazyCdr)
 */
abstract class Cell<out T> : Cons<T>() {
    override val car: T get() = lazyCar.value()
    override val cdr: Cons<T> get() = lazyCdr.value()
    override val lazyCar: Eval<T> get() = later { car }
    override val lazyCdr: Eval<Cons<T>> get() = later { cdr }
    override operator fun component1() = car
    override operator fun component2() = cdr
    override fun toString() = joinToString(prefix = "[", postfix = "]")
    override fun equals(other: Any?) = other is Cell<*> && other.car == car && other.cdr == cdr
    override fun hashCode() = (car?.hashCode() ?: 0).let { 31 * it + cdr.hashCode() }
}
