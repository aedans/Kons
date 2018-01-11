@file:Suppress("unused")

package io.github.aedans.kons

import java.util.*

fun <T> Cons<T>.asReversed() = toList().asReversed().toCons()

infix fun <T> Cons<T>.chunked(size: Int) = asSequence().chunked(size).map { it.toCons() }.toCons()
fun <T, R> Cons<T>.chunked(size: Int, transform: (List<T>) -> R) = asSequence().chunked(size, transform).toCons()

fun <T> Cons<T>.distinct() = asSequence().distinct().toCons()
infix fun <T, K> Cons<T>.distinctBy(selector: (T) -> K) = asSequence().distinctBy(selector).toCons()

infix fun <T> Cons<T>.drop(i: Int) = asSequence().drop(i).toCons()
infix fun <T> Cons<T>.dropWhile(predicate: (T) -> Boolean) = asSequence().dropWhile(predicate).toCons()
infix fun <T> Cons<T>.dropLast(i: Int) = toList().dropLast(i).toCons()
infix fun <T> Cons<T>.dropLastWhile(predicate: (T) -> Boolean) = toList().dropLastWhile(predicate).toCons()

infix fun <T> Cons<T>.filter(predicate: (T) -> Boolean) = asSequence().filter(predicate).toCons()
infix fun <T> Cons<T>.filterIndexed(predicate: (Int, T) -> Boolean) = asSequence().filterIndexed(predicate).toCons()
inline fun <T, reified R> Cons<T>.filterIsInstance(): Cons<R> = asSequence().filterIsInstance<R>().toCons()
infix fun <T, R : Any> Cons<T>.filterIsInstance(klazz: Class<R>) = asSequence().filterIsInstance(klazz).toCons()
infix fun <T> Cons<T>.filterNot(predicate: (T) -> Boolean) = asSequence().filterNot(predicate).toCons()
fun <T : Any> Cons<T?>.filterNotNull() = asSequence().filterNotNull().toCons()

infix fun <A, B> Cons<A>.flatMap(transform: (A) -> Iterable<B>) = asSequence().flatMap { transform(it).asSequence() }.toCons()
fun <T> Cons<Cons<T>>.flatten() = asSequence().flatten().toCons()

infix fun <A, B> Cons<A>.map(transform: (A) -> B) = asSequence().map(transform).toCons()
infix fun <A, B> Cons<A>.mapIndexed(transform: (Int, A) -> B) = asSequence().mapIndexed(transform).toCons()
infix fun <A, B : Any> Cons<A>.mapNotNull(transform: (A) -> B?) = asSequence().mapNotNull(transform).toCons()
infix fun <A, B : Any> Cons<A>.mapIndexedNotNull(transform: (Int, A) -> B) = asSequence().mapIndexedNotNull(transform).toCons()

infix operator fun <T> Cons<T>.minus(element: T) = asSequence().minus(element).toCons()
infix fun <T> Cons<T>.minusElement(element: T) = this - element

infix operator fun <T> Cons<T>.minus(elements: Iterable<T>) = asSequence().minus(elements).toCons()
infix operator fun <T> Cons<T>.minus(elements: Sequence<T>) = this - elements.toCons()
infix operator fun <T> Cons<T>.minus(elements: Array<out T>) = this - elements.toCons()

fun <T> Cons<T>?.orEmpty() = this ?: Nil

infix fun <T> Cons<T>.partition(predicate: (T) -> Boolean) = asSequence().partition(predicate).let { (a, b) -> a.toCons() to b.toCons() }

infix operator fun <T> Cons<T>.plus(element: T) = asSequence().plus(element).toCons()
infix fun <T> Cons<T>.plusElement(element: T) = this + element

infix operator fun <T> Cons<T>.plus(elements: Cons<T>) = asSequence().plus(elements).toCons()
infix operator fun <T> Cons<T>.plus(elements: Iterable<T>) = this + elements.toCons()
infix operator fun <T> Cons<T>.plus(elements: Sequence<T>) = this + elements.toCons()
infix operator fun <T> Cons<T>.plus(elements: Array<out T>) = this + elements.toCons()

fun <T : Any> Cons<T?>.requireNoNulls() = toList().requireNoNulls().toCons()

fun <T> Cons<T>.reversed() = asIterable().reversed().toCons()

fun <T : Comparable<T>> Cons<T>.sorted() = asSequence().sorted().toCons()
fun <T : Comparable<T>> Cons<T>.sortedDescending() = asSequence().sortedDescending().toCons()
inline infix fun <T, R : Comparable<R>> Cons<T>.sortedBy(crossinline selector: (T) -> R?) = asSequence().sortedBy(selector).toCons()
inline infix fun <T, R : Comparable<R>> Cons<T>.sortedByDescending(crossinline selector: (T) -> R?) = asSequence().sortedByDescending(selector).toCons()
infix fun <T> Cons<T>.sortedWith(comparator: Comparator<in T>) = asSequence().sortedWith(comparator).toCons()

fun <T> Cons<T>.shuffled() = asIterable().shuffled().toCons()
fun <T> Cons<T>.shuffled(random: Random) = asIterable().shuffled(random).toCons()

infix fun <T> Cons<T>.slice(indices: IntRange) = toList().slice(indices).toCons()
infix fun <T> Cons<T>.slice(indices: Iterable<Int>) = toList().slice(indices).toCons()

infix fun <T> Cons<T>.take(i: Int) = asSequence().take(i).toCons()
infix fun <T> Cons<T>.takeWhile(predicate: (T) -> Boolean) = asSequence().takeWhile(predicate).toCons()
infix fun <T> Cons<T>.takeLast(i: Int) = toList().takeLast(i).toCons()
infix fun <T> Cons<T>.takeLastWhile(predicate: (T) -> Boolean) = toList().takeLastWhile(predicate).toCons()

fun <A, B> Cons<Pair<A, B>>.unzip() = asSequence().unzip().let { (a, b) -> a.toCons() to b.toCons() }

fun <T, R> Cons<T>.windowed(size: Int, step: Int = 1, partialWindows: Boolean = false) = asSequence().windowed(size, step, partialWindows).map { it.toCons() }.toCons()
fun <T, R> Cons<T>.windowed(size: Int, step: Int = 1, partialWindows: Boolean = false, transform: (Cons<T>) -> R) = asSequence().windowed(size, step, partialWindows) { transform(it.toCons()) }.toCons()

infix fun <T, R> Cons<T>.zip(other: Sequence<R>) = asSequence().zip(other).toCons()
infix fun <T> Cons<T>.zip(other: Iterable<T>) = this zip other.asSequence()
infix fun <T> Cons<T>.zip(other: Array<out T>) = this zip other.asSequence()
fun <T, R, V> Cons<T>.zip(other: Sequence<R>, transform: (T, R) -> V) = asSequence().zip(other, transform).toCons()
fun <T, R, V> Cons<T>.zip(other: Iterable<R>, transform: (T, R) -> V) = zip(other.asSequence(), transform)
fun <T, R, V> Cons<T>.zip(other: Array<out R>, transform: (T, R) -> V) = zip(other.asSequence(), transform)
fun <T> Cons<T>.zipWithNext() = asSequence().zipWithNext().toCons()
infix fun <T, R> Cons<T>.zipWithNext(transform: (T, T) -> R) = asSequence().zipWithNext(transform).toCons()
