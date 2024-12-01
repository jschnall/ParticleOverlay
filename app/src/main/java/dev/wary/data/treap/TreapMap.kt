package dev.wary.data.treap

import java.util.SortedMap

// TODO
//class TreapMap<K, V>(
//    val keyComparator: Comparator<K> = compareBy { it.hashCode() },
//    val treap: Treap<TreapMap<K, V>.Entry> = TreapSet()
//): SortedMap<K, V> {
//    override fun subMap(fromInclusive: K?, toExclusive: K?): TreapMap<K, V> {
//        return TreapMap(
//            keyComparator,
//            treap.subSet(
//                if (fromInclusive == null) null else Entry(fromInclusive, null),
//                if (toExclusive == null) null else Entry(toExclusive, null)
//            )
//        )
//    }
//
//    override fun clear() {
//        treap.clear()
//    }
//
//    override fun containsKey(key: K): Boolean {
//        return treap.contains(Entry(key, null))
//    }
//
//    override fun put(key: K, value: V?): V? {
//        treap.add(Entry(key, value))
//    }
//
//    override fun remove(key: K): V? {
//        treap.remove(Entry(key, null))
//    }
//
//    fun keys(): List<K> {
//        val result = mutableListOf<K>()
//        val iterator = treap.iterator()
//
//        while (iterator.hasNext()) {
//            result.add(iterator.next().key)
//        }
//
//        return result
//    }
//
//    fun values(): List<V?> {
//        val result = mutableListOf<V?>()
//        val iterator = treap.iterator()
//
//        while (iterator.hasNext()) {
//            result.add(iterator.next().value)
//        }
//
//        return result
//    }
//
//    val size
//        get() = treap.size
//
//
//    inner class Entry(val key: K, val value: V?): Comparable<Entry> {
//        override fun compareTo(other: Entry) = keyComparator.compare(key, other.key)
//    }
//}