package io.github.aedans.cons

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
 * Creates a cons cell from receiver and cdr. cdr is evaluated lazily.
 */
infix fun <T> T.cons(cdr: () -> Cons<T>) = object : Cell<T>() {
    override val car = this@cons
    override val cdr by lazy(cdr)
}

/**
 * Creates a cons cell from a receiver and cdr. receiver is evaluated lazily.
 */
infix fun <T> (() -> T).cons(cdr: Cons<T>) = object : Cell<T>() {
    override val car by lazy(this@cons)
    override val cdr = cdr
}

/**
 * Creates a cons cell from receiver and cdr. Both are evaluated lazily.
 */
infix fun <T> (() -> T).cons(cdr: () -> Cons<T>) = object : Cell<T>() {
    override val car by lazy(this@cons)
    override val cdr by lazy(cdr)
}

operator fun <T> T.plus(cons: Cons<T>) = this cons cons
operator fun <T> T.plus(cons: () -> Cons<T>) = this cons cons
operator fun <T> (() -> T).plus(cons: Cons<T>) = this cons cons
operator fun <T> (() -> T).plus(cons: () -> Cons<T>) = this cons cons

/**
 * Mirror of listOf(vararg T): List<T>.
 */
fun <T> consOf(t: T, vararg ts: T) = t cons ts.asIterable().toCons()
fun consOf() = Nil

/**
 * Lazily creates a cons list from an Iterable.
 */
fun <T> Iterable<T>.toCons() = iterator().collectToCons()
fun <T> Sequence<T>.toCons() = iterator().collectToCons()

/**
 * Lazily creates a cons list from an Iterator.
 */
fun <T> Iterator<T>.collectToCons(): Cons<T> = when {
    hasNext() -> {
        val it = lazy(this::next)
        it::value cons { it.value; collectToCons() }
    }
    else -> Nil
}

/**
 * Appends a lazy element to a cons list.
 */
infix fun <T> Cons<T>.append(t: () -> T): Cell<T> = when (this) {
    Nil -> t cons Nil
    is Cell<T> -> this::car cons { cdr append t }
}

/**
 * Appends an element to a cons list.
 */
infix fun <T> Cons<T>.append(t: T): Cell<T> = when (this) {
    Nil -> t cons Nil
    is Cell<T> -> this::car cons { cdr append t }
}

operator fun <T> Cons<T>.plus(t: () -> T) = this append t
operator fun <T> Cons<T>.plus(t: T) = this append t

/**
 * Prepends a cons list to another cons list.
 */
infix fun <T> Cons<T>.prependTo(cons: Cons<T>): Cons<T> = when (this) {
    Nil -> cons
    is Cell<T> -> this::car cons { cdr prependTo cons }
}

operator fun <T> Cons<T>.plus(cons: Cons<T>) = this prependTo cons
