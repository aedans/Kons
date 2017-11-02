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
interface Cons<out T> : List<T> {
    /**
     * The head of the cons list.
     */
    val car: T

    /**
     * The tail of the cons list.
     */
    val cdr: Cons<T>

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
 * Creates a cons cell from receiver and cdr. Both are evaluated lazily.
 */
infix fun <T> (() -> T).cons(cdr: () -> Cons<T>): Cons<T> = object : AbstractCons<T>() {
    override val car by lazy(this@cons)
    override val cdr by lazy(cdr)
}

operator fun <T> T.plus(cons: Cons<T>) = this cons cons
operator fun <T> T.plus(cons: () -> Cons<T>) = this cons cons

/**
 * Mirror of List(Int, (Int) -> T): List<T>.
 */
fun <T> cons(size: Int, init: (Int) -> T) = run {
    fun getCons(acc: Int): Cons<T> = when (acc) {
        size -> Nil
        else -> init(acc) cons { getCons(acc + 1) }
    }
    if (size < 0)
        throw IllegalArgumentException("size: $size")
    else
        getCons(0)
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
 * Mirror of emptyList(): List<T>.
 */
fun emptyCons() = Nil

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

/**
 * Appends a lazy element to a cons list.
 */
infix fun <T> Cons<T>.append(t: () -> T): Cons<T> = when (this) {
    Nil -> t() cons Nil
    else -> car cons { cdr.append(t) }
}

/**
 * Appends an element to a cons list.
 */
infix fun <T> Cons<T>.append(t: T) = append { t }

operator fun <T> Cons<T>.plus(t: () -> T) = append(t)
operator fun <T> Cons<T>.plus(t: T) = append(t)

/**
 * Prepends an element to a lazy cons list.
 */
infix fun <T> Cons<T>.prependTo(cons: () -> Cons<T>): Cons<T> = when (this) {
    Nil -> cons()
    else -> car cons { cdr.prependTo(cons) }
}

/**
 * Prepends an element to a cons list.
 */
infix fun <T> Cons<T>.prependTo(cons: Cons<T>) = prependTo { cons }

/**
 * Mirror of Iterable<T>.take(i: Int): List<T>.
 */
fun <T> Cons<T>.take(i: Int): Cons<T> = when (i) {
    0 -> Nil
    else -> car cons { cdr.take(i - 1) }
}
