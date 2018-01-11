package io.github.aedans.kons

import arrow.core.Eval
import arrow.core.Eval.Companion.later
import arrow.core.Eval.Companion.now

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
 * Lazily creates a cons list from a String.
 */
fun String.toCons(): Cons<Char> = iterator().collectToCons()

/**
 * Lazily creates a cons list from an Array.
 */
fun <T> Array<out T>.toCons() = iterator().collectToCons()

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

@Suppress("PrivatePropertyName")
private val EvalNil = now(Nil)
@Suppress("unused", "PropertyName")
val Eval.Companion.Nil get() = EvalNil
