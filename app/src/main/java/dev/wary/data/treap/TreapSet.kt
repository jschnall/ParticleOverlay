package dev.wary.data.treap

import java.util.SortedSet


class TreapSet<V>(comparator: Comparator<V>? = null) : Treap<V>(
    comparator ?: compareBy { it }), SortedSet<V> where V : Any, V : Comparable<V> {

    override fun add(element: V): Boolean {
        return upsert(element)
    }

    override fun addAll(elements: Collection<V>): Boolean {
        val oldSize = size

        for (element in elements) {
            upsert(element)
        }

        return oldSize != size
    }

    override fun iterator(): MutableIterator<V> {
        return this.Iterator()
    }

    override fun contains(element: V): Boolean {
        return search(element)
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
        TODO("Not yet implemented")
    }

    override fun removeAll(elements: Collection<V>): Boolean {
        val oldSize = size

        for (element in elements) {
            remove(element)
        }

        return oldSize != size
    }

}