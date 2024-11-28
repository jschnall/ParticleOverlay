package dev.wary.data.treap

// TODO move tests to separate module, and make them proper JUnit tests
fun main() {
//    testFind()
//    testDelete()
//    testIterator()
//    testIterator(isDescending)
//    testUnion()
//    testIntersection()
//    testMinMax()
    testSubTreap()
}

fun testFind() {
    val treap = TreapSet<Int>()

    println(treap.size)
    println (treap.find(40_000))
    for (i in 1 .. 100_000) {
        treap.insert(i)
    }
    println(treap.size)
    println (treap.find(40_000))
    for (i in 1 .. 50_000) {
        treap.delete(i)
    }
    println(treap.size)
    println (treap.find(40_000))
    for (i in 50_000 downTo 1) {
        treap.insert(i)
    }
    println(treap.size)
    println (treap.find(40_000))

    treap.clear()
    println(treap.size)
    println (treap.find(40_000))
}

fun testDelete() {
    val treap = TreapSet<Int>()

    for (i in 1 .. 10) {
        treap.insert(i)
    }

    treap.delete(5)
    treap.delete(5)
    treap.delete(1)
    treap.delete(0)
    treap.delete(5)
    println(treap.size)

    treap.print()
}

fun testUnion() {
    val treap = TreapSet<Int>()

    treap.insert(-5)
    treap.insert(0)
    treap.insert(7)

    treap.union(setOf(-7, 0, 5)).print()
}

fun testIntersection() {
    val treap = TreapSet<Int>()

    treap.insert(-5)
    treap.insert(0)
    treap.insert(7)

    treap.intersect(setOf(-7, 0, 5)).print()
}

fun testMinMax() {
    val treap = TreapSet<Int>()

    println ("${treap.minOrNull()}, ${treap.maxOrNull()}")
    treap.insert(0)
    println ("${treap.minOrNull()}, ${treap.maxOrNull()}")
    treap.insert(-10_000)
    treap.insert(5000)
    println ("${treap.minOrNull()}, ${treap.maxOrNull()}")
}

fun testSubTreap() {
    val treap = TreapSet<Int>()

    for (i in -100 .. 100) {
        treap.insert(i)
    }
    treap.print()
    println(treap.size)

    val subTreap = treap.subTreap(-10, 10)
    subTreap.print()
    println(subTreap.size)

    try {
        subTreap.delete(-100)
    } catch(e: Exception) {
        println(e.message)
    }
    for (i in -10 .. 0) {
        subTreap.delete(i)
    }
    println(treap.size)
    println(subTreap.size)

    for (i in 5 .. 50) {
        treap.delete(i)
    }
    treap.print()
    println(treap.size)
    subTreap.print()
    println(subTreap.size)
}

fun testIterator(isDescending: Boolean = false, fromInclusive: Int? = null, toExclusive: Int? = null) {
    val treap = TreapSet<Int>()

    for (i in 1 .. 10) {
        treap.insert(i)
    }
    treap.print()

    val iter = treap.Iterator(isDescending, fromInclusive, toExclusive)
    iter.remove()
    println(treap.size)
    while (iter.hasNext()) {
        iter.next()
        iter.remove()
    }
    println(treap.size)
}

fun <T: Comparable<T>> Treap<T>.print() {
    val iter = iterator()
    print("[")
    if (iter.hasNext()) print(iter.next())
    while (iter.hasNext()) {
        print(", ${iter.next()}")
    }
    println("]")
}