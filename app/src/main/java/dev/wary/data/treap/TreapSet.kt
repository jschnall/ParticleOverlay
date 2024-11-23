package dev.wary.data.treap

import java.util.SortedSet

class DelegateTreapSet<V>(
    private val treapSet: TreapSet<V>,
    private val fromInclusive: V? = null,
    private val toExclusive: V? = null
): SortedSet<V> where V: Comparable<V> {
    override fun add(element: V): Boolean {
        TODO("Not yet implemented")
    }

    override fun addAll(elements: Collection<V>): Boolean {
        TODO("Not yet implemented")
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

    override fun iterator(): MutableIterator<V> {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun comparator(): java.util.Comparator<in V> {
        TODO("Not yet implemented")
    }

    override fun first(): V {
        TODO("Not yet implemented")
    }

    override fun last(): V {
        TODO("Not yet implemented")
    }

    override val size: Int
        get() = TODO("Not yet implemented")

    override fun tailSet(fromElement: V): SortedSet<V> {
        TODO("Not yet implemented")
    }

    override fun headSet(toElement: V): SortedSet<V> {
        TODO("Not yet implemented")
    }

    override fun subSet(fromElement: V, toElement: V): SortedSet<V> {
        TODO("Not yet implemented")
    }

    override fun containsAll(elements: Collection<V>): Boolean {
        TODO("Not yet implemented")
    }

    override fun contains(element: V): Boolean {
        TODO("Not yet implemented")
    }

    override fun retainAll(elements: Collection<V>): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeAll(elements: Collection<V>): Boolean {
        TODO("Not yet implemented")
    }

    override fun remove(element: V): Boolean {
        TODO("Not yet implemented")
    }

}

class TreapSet<V>(comparator: Comparator<V>? = null): BaseTreap<V>(
    comparator ?: compareBy { it }), SortedSet<V> where V: Comparable<V> {

    override fun add(element: V): Boolean {
        return insert(element)
    }

    override fun addAll(elements: Collection<V>): Boolean {
        val oldSize = size

        for (element in elements) {
            insert(element)
        }

        return oldSize != size
    }

    override fun iterator(): MutableIterator<V> {
        return this.Iterator(false)
    }

    override fun contains(element: V): Boolean {
        return find(element)
    }

    override fun remove(element: V): Boolean {
        return delete(element)
    }

    override fun isEmpty(): Boolean {
        return size == 0
    }

    override fun comparator(): java.util.Comparator<in V> {
        return comparator
    }

    override fun first(): V {
        minOrNull()?.let {
            return it
        }
        throw NoSuchElementException("Set is empty")
    }

    override fun last(): V {
        maxOrNull()?.let {
            return it
        }
        throw NoSuchElementException("Set is empty")
    }

    override fun tailSet(fromElement: V): SortedSet<V> {
        return DelegateTreapSet(this, fromInclusive = fromElement)
    }

    override fun headSet(toElement: V): SortedSet<V> {
        return DelegateTreapSet(this, toExclusive = toElement)
    }

    override fun subSet(fromElement: V, toElement: V): SortedSet<V> {
        return DelegateTreapSet(this, fromElement, toElement)
    }

    override fun containsAll(elements: Collection<V>): Boolean {
        for (element in elements) {
            if (!contains(element)) return false
        }
        return true
    }

    override fun retainAll(elements: Collection<V>): Boolean {
        return retainAll(elements)
    }

    override fun removeAll(elements: Collection<V>): Boolean {
        val oldSize = size

        for (element in elements) {
            remove(element)
        }

        return oldSize != size
    }

}