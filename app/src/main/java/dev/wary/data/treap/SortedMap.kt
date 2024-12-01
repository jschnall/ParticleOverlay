package dev.wary.data.treap

interface SortedMap<K, V> {
    fun containsKey(key: K): Boolean
    fun clear()
    fun isEmpty(): Boolean
    fun comparator(): Comparator<in K>?
    fun firstKey(): K
    fun lastKey(): K
    val entries: MutableSet<MutableMap.MutableEntry<K, V>>
    val keys: MutableSet<K>
    val values: MutableCollection<V>
    val size: Int
    fun tailMap(fromKey: K): SortedMap<K, V>
    fun headMap(toKey: K): SortedMap<K, V>
    fun subMap(fromKey: K, toKey: K): SortedMap<K, V>
    fun get(key: K): V?
    fun containsValue(value: V): Boolean
    fun remove(key: K): V?
    fun putAll(from: Map<out K, V>)
    fun put(key: K, value: V?): V?
}