package dev.wary.data.treap

interface SortedSet<T>: Collection<T> {
    fun add(element: T): Boolean
    fun addAll(elements: Collection<T>): Boolean
    fun clear()
    fun first(): T
    fun last(): T
    fun tailSet(fromInclusive: T): SortedSet<T>
    fun headSet(toExclusive: T): SortedSet<T>
    fun subSet(fromInclusive: T? = null, toExclusive: T? = null): SortedSet<T>
    fun retainAll(elements: Collection<T>): Boolean
    fun removeAll(elements: Collection<T>): Boolean
    fun remove(element: T): Boolean
    val comparator: Comparator<in T>
}

fun <T> sortedSetOf(comparator: Comparator<in T> = compareBy { it.hashCode() }): SortedSet<T> {
    return TreapSet(comparator)
}