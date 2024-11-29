package dev.wary.data.treap

// TODO
class TreapMap<K: Comparable<K>, V>(
    val keyComparator: Comparator<K> = compareBy { it },
    val treap: Treap<TreapMap<K, V>.Entry> = TreapSet()
) {
    fun subMap(fromInclusive: K?, toExclusive: K?): TreapMap<K, V> {
        return TreapMap(
            keyComparator,
            treap.subTreap(
                if (fromInclusive == null) null else Entry(fromInclusive, null),
                if (toExclusive == null) null else Entry(toExclusive, null)
            )
        )
    }

    fun clear() {
        treap.clear()
    }

    fun containsKey(key: K): Boolean {
        return treap.find(Entry(key, null))
    }

    fun put(key: K, value: V?) {
        treap.insert(Entry(key, value))
    }

    fun remove(key: K) {
        treap.delete(Entry(key, null))
    }

    fun keys(): List<K> {
        val result = mutableListOf<K>()
        val iterator = treap.iterator()

        while (iterator.hasNext()) {
            result.add(iterator.next().key)
        }

        return result
    }

    fun values(): List<V?> {
        val result = mutableListOf<V?>()
        val iterator = treap.iterator()

        while (iterator.hasNext()) {
            result.add(iterator.next().value)
        }

        return result
    }

    val size
        get() = treap.size


    inner class Entry(val key: K, val value: V?): Comparable<Entry> {
        override fun compareTo(other: Entry) = keyComparator.compare(key, other.key)
    }
}