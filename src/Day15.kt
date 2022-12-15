import java.lang.Math.abs
import java.math.BigInteger

private const val DAY = 15

class Day15 {

    private data class Point(val x: Int, val y: Int) {
        fun distance(other: Point) = abs(other.x - x) + abs(other.y - y)
        fun min(other: Point) = Point(Math.min(x, other.x), Math.min(y, other.y))
        fun max(other: Point) = Point(Math.max(x, other.x), Math.max(y, other.y))
    }

    private data class Zone(val center: Point, val distance: Int) {
        constructor(sensor: Point, beacon: Point) : this(sensor, sensor.distance(beacon))
    }

    private data class Area(val zones: List<Zone>, val beacons: Set<Point>, val minRange: Point, val maxRange: Point) {
        fun getAllXRangesForY(y: Int): List<IntRange> {
            return zones.mapNotNull { z ->
                val distanceY = abs(z.center.y - y)
                (z.distance - distanceY).takeIf { it >= 0 }
                    ?.let { IntRange(z.center.x - it, z.center.x + it) }
            }
        }
    }

    private fun List<String>.toArea(): Area {
        val numberRegexp = Regex("-?\\d+")
        var minRange = Point(Int.MAX_VALUE, Int.MAX_VALUE)
        var maxRange = Point(Int.MIN_VALUE, Int.MIN_VALUE)
        val beacons = mutableSetOf<Point>()
        val zones = map { row ->
            val (sensor, beacon) = numberRegexp.findAll(row)
                .map { it.value.toInt() }.windowed(2, 2).map { Point(it[0], it[1]) }.toList()
            beacons.add(beacon)
            val zone = Zone(sensor = sensor, beacon = beacon)
            minRange = minRange.min(sensor).min(beacon).min(Point(sensor.x - zone.distance, sensor.y))
            maxRange = maxRange.max(sensor).max(beacon).max(Point(sensor.x + zone.distance, sensor.y))
            zone
        }
        return Area(zones, beacons, minRange, maxRange)
    }

    private fun detectNoBeaconPoints(area: Area, lineY: Int): Int {
        val beaconsXInLineY = area.beacons.filter { it.y == lineY }.map { it.x }.toSet()
        val ranges = area.getAllXRangesForY(lineY)
        var count = 0
        for (x in area.minRange.x..area.maxRange.x) {
            if (ranges.any { x in it } && !beaconsXInLineY.contains(x)) {
                count++
            }
        }
        return count
    }

    private fun distressBeaconFreq(area: Area, maxRange: Int): BigInteger {
        for (y in 0..maxRange) {
            val orderedRanges = area.getAllXRangesForY(y).sortedBy { it.first }.toMutableList()
            var posX = orderedRanges.first().first
            while (orderedRanges.isNotEmpty()) {
                val r = orderedRanges.removeFirst()
                if (r.first > posX + 1) {
                    val x = posX + 1
                    if (!area.beacons.contains(Point(x,y))) {
                        var v1 = BigInteger.valueOf(x.toLong())
                        v1 = v1.multiply(BigInteger.valueOf(4_000_000L))
                        v1 = v1.add(BigInteger.valueOf(y.toLong()))
                        return v1
                    }
                }
                if (r.last < posX) {
                    continue
                }
                posX = r.last
            }
        }
        return BigInteger.ZERO
    }

    fun part1(input: List<String>, lineY: Int) =
        detectNoBeaconPoints(input.toArea(), lineY)

    fun part2(input: List<String>, maxRange: Int) =
        distressBeaconFreq(input.toArea(), maxRange)
}

fun main() {
    val testInput1 = readInput("Day${DAY}_test")
    val day = Day15()
    check(day.part1(testInput1, lineY = 10).also { println(it) } == 26) { "Part1 Test - expected value doesn't match" }
    check(day.part2(testInput1, maxRange = 20).also { println(it) } == BigInteger.valueOf(56000011)) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day${DAY}")
    println(day.part1(input, lineY = 2_000_000))
    println(day.part2(input, maxRange = 4_000_000))
}
