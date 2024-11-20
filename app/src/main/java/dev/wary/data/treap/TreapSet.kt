package dev.wary.data.treap

import java.util.SortedSet

class TreapSet<V>(comparator: Comparator<V>? = null) : Treap<V>(
    comparator ?: compareBy { it }), SortedSet<V> where V : Comparable<V> {

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

    override fun retainAll(elements: Collection<V>): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeAll(elements: Collection<V>): Boolean {
        TODO("Not yet implemented")
    }

}