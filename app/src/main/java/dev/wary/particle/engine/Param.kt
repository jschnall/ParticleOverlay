package dev.wary.particle.engine

import kotlin.random.Random

interface Param<T> {
    val value: T
}

class ExactParam<T>(val v: T): Param<T> {
    override val value: T
        get() = v
}

class ListParam<T>(val list: List<T>): Param<T> {
    override val value: T
        get() = list[Random.nextInt(0, list.size)]
}

fun <T> listParamOf(vararg items: T) = ListParam(items.toList())

abstract class RangeParam<T>(val from: T, val toExclusive: T): Param<T>

class IntRangeParam(from: Int, toExclusive: Int): RangeParam<Int>(from, toExclusive) {
    override val value
        get() = Random.nextInt(from, toExclusive)
}

class LongRangeParam(from: Long, toExclusive: Long): RangeParam<Long>(from, toExclusive) {
    override val value
        get() = Random.nextLong(from, toExclusive)
}

class DoubleRangeParam(from: Double, toExclusive: Double): RangeParam<Double>(from, toExclusive) {
    override val value
        get() = Random.nextDouble(from, toExclusive)
}

class ColorRangeParam(
    fromDoubleColor: DoubleColor,
    toDoubleColorExclusive: DoubleColor
) {
    constructor(from: Int, to: Int): this(
        fromDoubleColor = DoubleColor.fromInt(from),
        toDoubleColorExclusive = DoubleColor.fromInt(to)
    )
}