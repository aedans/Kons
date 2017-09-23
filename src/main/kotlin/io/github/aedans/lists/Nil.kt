package io.github.aedans.lists

/**
 * The empty cons list, used as the terminator for all cons lists.
 */
object Nil : Cons<Nothing> {
    override val car get() = throw IndexOutOfBoundsException()
    override val cdr get() = throw IndexOutOfBoundsException()
    override fun iterator() = emptyList<Nothing>().iterator()
}
