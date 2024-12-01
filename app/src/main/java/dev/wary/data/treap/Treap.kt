package dev.wary.data.treap

interface Treap<T>: SortedSet<T> {
    override fun subSet(fromInclusive: T?, toExclusive: T?): Treap<T>
    fun minOrNull(floor: T? = null): T?
    fun maxOrNull(ceil: T? = null): T?
    fun iteratorDescending(): MutableIterator<T>
    infix fun intersect(elements : Collection<T>): Treap<T>
    fun union(elements: Collection<T>): Treap<T>
}

