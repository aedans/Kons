package io.github.aedans.cons

import arrow.core.Eval
import arrow.core.Eval.Companion.defer
import arrow.core.Eval.Companion.later
import arrow.core.Eval.Companion.now
import kotlin.coroutines.experimental.*

typealias NonEmptyList<T> = Nel<T>
typealias Nel<T> = Cell<T>

/**
 * Creates a cons cell from receiver and cdr.
 */
infix fun <T> T.cons(cdr: Cons<T>) = object : Cell<T>() {
    override val car = this@cons
    override val cdr = cdr
}

/**
 * Creates a cons cell from receiver and cdr. Cdr is evaluated lazily.
 */
infix fun <T> T.cons(cdr: Eval<Cons<T>>) = object : Cell<T>() {
    override val car = this@cons
    override val lazyCdr = cdr
}

/**
 * Creates a cons cell from a receiver and cdr. Receiver is evaluated lazily.
 */
infix fun <T> Eval<T>.cons(cdr: Cons<T>): Cell<T> = object : Cell<T>() {
    override val lazyCar = this@cons
    override val cdr = cdr
}

/**
 * Creates a cons cell from receiver and cdr. Both are evaluated lazily.
 */
infix fun <T> Eval<T>.cons(cdr: Eval<Cons<T>>): Cell<T> = object : Cell<T>() {
    override val lazyCar = this@cons
    override val lazyCdr = cdr
}

operator fun <T> T.plus(cons: Cons<T>): Cell<T> = this cons cons
operator fun <T> T.plus(cons: Eval<Cons<T>>): Cell<T> = this cons cons
operator fun <T> Eval<T>.plus(cons: Cons<T>): Cell<T> = this cons cons
operator fun <T> Eval<T>.plus(cons: Eval<Cons<T>>): Cell<T> = this cons cons

/**
 * Mirror of listOf(vararg T): List<T>.
 */
fun <T> consOf(t: T, vararg ts: T): Cell<T> = t cons ts.asIterable().toCons()
fun <T> consOf(t: T) = t cons Nil
fun consOf(): Nil = Nil
fun emptyCons(): Nil = Nil

/**
 * Lazily creates a cons list from an Iterable.
 */
fun <T> Iterable<T>.toCons(): Cons<T> = iterator().collectToCons()

/**
 * Lazily creates a cons list from a Sequence.
 */
fun <T> Sequence<T>.toCons(): Cons<T> = iterator().collectToCons()

/**
 * Lazily creates a cons list from an Iterator.
 */
fun <T> Iterator<T>.collectToCons(): Cons<T> = when {
    hasNext() -> {
        val car = later(this::next)
        car cons later { car.value; collectToCons() }
    }
    else -> Nil
}

/**
 * Appends a lazy element to a cons list.
 */
infix fun <T> Cons<T>.append(t: Eval<T>): Cell<T> = run {
    fun Cons<T>.appendImpl(): Eval<Cell<T>> = run {
        when (this) {
            Nil -> now(t cons Nil)
            is Cell -> defer { cdr.appendImpl() }.map { car cons it }
        }
    }

    appendImpl().value()
}

/**
 * Appends an element to a cons list.
 */
infix fun <T> Cons<T>.append(t: T): Cell<T> = this append later { t }

operator fun <T> Cons<T>.plus(t: Eval<T>): Cell<T> = this append t
operator fun <T> Cons<T>.plus(t: T): Cell<T> = this append t

/**
 * Prepends a cons list to another cons list.
 */
infix fun <T> Cons<T>.prependTo(cons: Cons<T>): Cons<T> = buildSequence {
    forEach { yield(it) }
    yieldAll(cons)
}.toCons() as Cell<T>

operator fun <T> Cons<T>.plus(cons: Cons<T>) = this prependTo cons

@Suppress("PrivatePropertyName")
private val EvalNil = now(Nil)
@Suppress("unused", "PropertyName")
val Eval.Companion.Nil get() = EvalNil

fun main(args: Array<String>) {
    fun test(i: Int): Cons<Int> = i cons later { test(i + 1) }
    val iterator = test(0).iterator()
    iterator.forEach { if (it % 10000 == 0) println(it) }
}
