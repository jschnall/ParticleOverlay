package dev.wary.data.treap

import java.util.SortedMap


class TreapMap<K, V>(comparator: Comparator<K>? = null) : Treap<TreapMap.Entry<K, V>>(
    if (comparator == null) compareBy { it } else compareBy(comparator) { entry -> entry.key }
), SortedMap<K, V> where K : Comparable<K> {

    data class Entry<K : Comparable<K>, V>(val key: K, val value: V) : Comparable<Entry<K, V>> {
        override fun compareTo(other: Entry<K, V>): Int {
            return key.compareTo(other.key)
        }
    }

    override fun containsKey(key: K): Boolean {
        TODO("Not yet implemented")
    }

    override fun remove(key: K): V? {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun comparator(): java.util.Comparator<in K>? {
        TODO("Not yet implemented")
    }

    override fun firstKey(): K {
        TODO("Not yet implemented")
    }

    override fun lastKey(): K {
        TODO("Not yet implemented")
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = TODO("Not yet implemented")
    override val keys: MutableSet<K>
        get() = TODO("Not yet implemented")
    override val values: MutableCollection<V>
        get() = TODO("Not yet implemented")

    override fun tailMap(fromKey: K): SortedMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun headMap(toKey: K): SortedMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun subMap(fromKey: K, toKey: K): SortedMap<K, V> {
        TODO("Not yet implemented")
    }

    override fun get(key: K): V? {
        TODO("Not yet implemented")
    }

    override fun containsValue(value: V): Boolean {
        TODO("Not yet implemented")
    }

    override fun putAll(from: Map<out K, V>) {
        TODO("Not yet implemented")
    }

    override fun put(key: K, value: V): V? {
        TODO("Not yet implemented")
    }
}