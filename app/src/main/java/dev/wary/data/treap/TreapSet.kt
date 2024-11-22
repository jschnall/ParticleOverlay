package dev.wary.data.treap

import java.util.SortedSet


class TreapSet<V>(comparator: Comparator<V>? = null) : BaseTreap<V>(
    comparator ?: compareBy { it }), SortedSet<V> where V : Any, V : Comparable<V> {

    override fun add(element: V): Boolean {
        return insert(element)
    }

    override fun addAll(elements: Collection<V>): Boolean {
        return insertAll(elements)
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
        TODO("Not yet implemented")
        // return subTreap(fromInclusive = fromElement)
    }

    override fun headSet(toElement: V): SortedSet<V> {
        TODO("Not yet implemented")
    }

    override fun subSet(fromElement: V, toElement: V): SortedSet<V> {
        TODO("Not yet implemented")
    }

    override fun containsAll(elements: Collection<V>): Boolean {
        for (element in elements) {
            if (!contains(element)) return false
        }
        return true
    }

    override fun retainAll(elements: Collection<V>): Boolean {
        return intersect(elements)
    }

    override fun removeAll(elements: Collection<V>): Boolean {
        val oldSize = size

        for (element in elements) {
            remove(element)
        }

        return oldSize != size
    }

}