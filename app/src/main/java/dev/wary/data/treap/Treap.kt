package dev.wary.data.treap

import kotlin.random.Random

interface Treap<T : Comparable<T>> {
    fun subTreap(fromInclusive: T? = null, toExclusive: T? = null): Treap<T>
    fun minOrNull(floor: T? = null): T?
    fun maxOrNull(ceil: T? = null): T?
    fun clear()
    fun iterator(): MutableIterator<T>
    fun iteratorDescending(): MutableIterator<T>
    fun intersect(elements : Collection<T>): Treap<T>
    fun union(elements: Collection<T>): Treap<T>
    fun delete(value: T): Boolean
    fun insert(value: T): Boolean
    fun find(value: T): Boolean

    val comparator: Comparator<T>
    val size: Int
}

