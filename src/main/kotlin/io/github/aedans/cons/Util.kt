package io.github.aedans.cons

typealias NonEmptyList<T> = Nel<T>
typealias Nel<T> = Cell<T>

/**
 * Creates a cons cell from receiver and cdr.
 */
infix fun <T> T.cons(cdr: Cons<T>) = Cell({ this }, { cdr })

/**
 * Creates a cons cell from receiver and cdr. cdr is evaluated lazily.
 */
infix fun <T> T.cons(cdr: () -> Cons<T>) = Cell({ this }, cdr)

/**
 * Creates a cons cell from receiver and cdr. Both are evaluated lazily.
 */
infix fun <T> (() -> T).cons(cdr: () -> Cons<T>) = Cell(this, cdr)

operator fun <T> T.plus(cons: Cons<T>) = this cons cons
operator fun <T> T.plus(cons: () -> Cons<T>) = this cons cons
operator fun <T> (() -> T).plus(cons: () -> Cons<T>) = this cons cons

/**
 * Mirror of List(Int, (Int) -> T): List<T>.
 */
fun <T> cons(size: Int, init: (Int) -> T) = run {
    fun getCons(acc: Int): Cons<T> = when (acc) {
        size -> Nil
        else -> ({ init(acc) } cons { getCons(acc + 1) })
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
fun <T> consOf(t: T, vararg ts: T) = t cons ts.asIterable().toCons()
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
infix fun <T> Cons<T>.append(t: () -> T): Cell<T> = when (this) {
    Nil -> t() cons Nil
    else -> ({ car } cons { cdr.append(t) })
}

/**
 * Appends an element to a cons list.
 */
infix fun <T> Cons<T>.append(t: T) = append { t }

operator fun <T> Cons<T>.plus(t: () -> T) = append(t)
operator fun <T> Cons<T>.plus(t: T) = append(t)

/**
 * Prepends a cons list to another cons list.
 */
infix fun <T> Cons<T>.prependTo(cons: Cons<T>): Cons<T> = when (this) {
    Nil -> cons
    else -> ({ car } cons { cdr.prependTo(cons) })
}

/**
 * Mirror of Iterable<T>.take(i: Int): List<T>.
 */
fun <T> Cons<T>.take(i: Int): Cons<T> = when (i) {
    0 -> Nil
    else -> ({ car } cons { cdr.take(i - 1) })
}

tailrec fun <A, R> Cons<A>.fold(r: R, fn: (R, A) -> R): R = when (this) {
    Nil -> r
    else -> cdr.fold(fn(r, car), fn)
}

fun <A, B> Cons<A>.map(fn: (A) -> B): Cons<B> = when (this) {
    Nil -> Nil
    else -> let { (x, xs) -> { fn(x) } cons { xs.map(fn) } }
}
