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
interface Cons<out T> : Iterable<T> {
    val car: T
    val cdr: Cons<T>

    /**
     * Mirror of List<T>.size: Int.
     */
    val size get() = run {
        tailrec fun getSize(acc: Int, cons: Cons<T>): Int = when (cons) {
            Nil -> acc
            else -> getSize(acc + 1, cons.cdr)
        }
        getSize(0, this)
    }

    /**
     * Mirror of List<T>.get(Int): T.
     */
    operator fun get(index: Int) = let {
        tailrec fun Cons<T>.getUnsafe(i: Int): T = when (i) {
            0 -> car
            else -> cdr.getUnsafe(i - 1)
        }
        if (index < 0) throw IndexOutOfBoundsException(index.toString()) else getUnsafe(index)
    }

    operator fun component1() = car
    operator fun component2() = cdr

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
 * Abstract implementation of Cons implementing toString, equals, and hashCode.
 */
abstract class AbstractCons<out T> : Cons<T> {
    override fun toString() = joinToString(prefix = "[", postfix = "]")
    override fun equals(other: Any?) = other !== Nil && other is Cons<*> && other.car == car && other.cdr == cdr
    override fun hashCode() = (car?.hashCode() ?: 0).let { 31 * it + cdr.hashCode() }
}

/**
 * Creates a cons cell from receiver and cdr.
 */
infix fun <T> T.cons(cdr: Cons<T>): Cons<T> = object : AbstractCons<T>() {
    override val car = this@cons
    override val cdr = cdr
}

/**
 * Creates a cons cell from receiver and cdr. cdr is evaluated lazily.
 */
infix fun <T> T.cons(cdr: () -> Cons<T>): Cons<T> = object : AbstractCons<T>() {
    override val car = this@cons
    override val cdr by lazy(cdr)
}

/**
 * Mirror of List(Int, (Int) -> T): List<T>.
 */
fun <T> cons(size: Int, init: (Int) -> T): Cons<T> = when (size) {
    0 -> Nil
    else -> init(size) cons { cons(size - 1, init) }
}

/**
 * Mirror of generateSequence(() -> T?): Sequence<T>.
 */
fun <T : Any> generateCons(nextFunction: () -> T?): Cons<T> = nextFunction().let {
    when (it) {
        null -> Nil
        else -> it cons { generateCons(nextFunction) }
    }
}

/**
 * Mirror of generateSequence(T?, (T) -> T?): Sequence<T>.
 */
fun <T : Any> generateCons(seed: T?, nextFunction: (T) -> T?): Cons<T> = when (seed) {
    null -> Nil
    else -> seed cons { generateCons(nextFunction(seed), nextFunction) }
}

/**
 * Mirror of listOf(vararg T): List<T>.
 */
fun <T> consOf(vararg t: T) = t.asIterable().toCons()
fun consOf() = Nil

/**
 * Lazily creates a cons list from an Iterable.
 */
fun <T> Iterable<T>.toCons() = iterator().collectToCons()

/**
 * Lazily creates a cons list from an Iterator.
 */
fun <T> Iterator<T>.collectToCons(): Cons<T> = when {
    hasNext() -> next() cons { collectToCons() }
    else -> Nil
}
