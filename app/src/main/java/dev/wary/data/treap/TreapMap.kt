package dev.wary.data.treap

class TreapMap<K: Comparable<K>, V>(
    val keyComparator: Comparator<K> = compareBy { it },
    val treap: Treap<TreapMap<K, V>.Entry> = TreapSet()
): TreapSet<TreapMap<K, V>.Entry>(comparator = compareBy { it }) {
    fun subMap(): Treap<TreapMap<K, V>.Entry> {
        return TreapMap(keyComparator, treap.subTreap())
    }

    fun insert(key: K, value: V?) {
        treap.insert(Entry(key, value))
    }

    fun delete(key: K) {
        treap.delete(Entry(key, null))
    }

    inner class Entry(val key: K, val value: V?): Comparable<Entry> {
        override fun compareTo(other: Entry) = keyComparator.compare(key, other.key)
    }
}