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

// Sequence aliases
fun <T : Any> generateCons(nextFunction: () -> T?) = generateSequence(nextFunction).toCons()
fun <T : Any> generateCons(seedFunction: () -> T?, nextFunction: (T) -> T?) = generateSequence(seedFunction, nextFunction).toCons()
fun <T : Any> generateCons(seed: T?, nextFunction: (T) -> T?) = generateSequence(seed, nextFunction).toCons()

fun <T> Sequence<T>.toCons() = iterator().collectToCons()

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
    hasNext() -> {
        val it = lazy { next() };
        { it.value } cons { it.value; collectToCons() }
    }
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
