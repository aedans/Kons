Kons
====

[![Download](https://api.bintray.com/packages/aedans/maven/kons/images/download.svg)](https://bintray.com/aedans/maven/kons/_latestVersion) 

[Kotlin](http://kotlinlang.org) linked list implementation using lazily evaluated cons cells.

Gradle
------

```gradle
repositories {
    jcenter()
}

dependencies {
    compile 'io.github.aedans:kons:$kons_version'
}
```

Code Samples
------------

```kotlin
// Recursively folds a list
tailrec fun <A, R> Cons<A>.fold(r: R, fn: (R, A) -> R): R = when (this) {
    Nil -> r
    is Cell -> cdr.fold(fn(r, car), fn) // car and cdr are smart cast from nullable to non-nullable
}

// Lazily filters a list
tailrec fun <T> Cons<T>.filter(fn: (T) -> Boolean): Cons<T> = when (this) {
    Nil -> Nil
    is Cell -> if (fn(car)) lazyCar cons later { cdr.filter(fn) } else cdr.filter(fn)
}

// Stack safe due to Eval
fun <A, B> Cons<A>.map(fn: (A) -> B): Eval<Cons<B>> = when (this) {
    Nil -> Eval.Nil
    is Cell -> now(later { fn(car) } cons defer { cdr.map(fn) })
}

// List of all natural numbers
val nat = generateSequence(0) { it + 1 }.toCons()

// List of all positive even numbers
val evens = nat.map { it * 2 }

// List of all primes
val primes = nat.filter { i -> (2 until i).none { i % it == 0 } }
```
