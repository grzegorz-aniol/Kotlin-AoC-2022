import kotlin.math.sign

private const val DAY = 14

private const val SPACE = 0
private const val ROCK = 1
private const val SAND = 2

class Day14 {
    private data class Point(val x: Int, val y: Int) {
        operator fun plus(d: Point) = Point(x + d.x, y + d.y)
        fun direction(other: Point) = Point((other.x - x).sign, (other.y - y).sign)
    }

    private data class Area(val area: Array<IntArray>, val dimension: Point, val startPoint: Point) {
        val directions = listOf(Point(0, 1), Point(-1, 1), Point(1, 1))
        fun includes(p: Point) = (p.x in 0 until dimension.x && p.y in 0 until dimension.y)
        fun get(p: Point) = area[p.y][p.x]
        fun put(p: Point, what: Int) {
            area[p.y][p.x] = what
        }

        fun nextMove(p: Point): Point? {
            for (d in directions) {
                val nextPoint = p + d
                if (!includes(nextPoint)) {
                    return nextPoint
                }
                if (get(nextPoint) == SPACE) {
                    return nextPoint
                }
            }
            return null
        }
    }

    private fun List<String>.toPoints(): List<List<Point>> = map {
        it.split(" -> ")
            .map { point ->
                val (x, y) = point.split(",").map { it.toInt() }
                Point(x, y)
            }
    }

    private fun forEachPointInLine(s: Point, e: Point, run: (p: Point) -> Unit) {
        var p = s
        val direction = p.direction(e)
        run(p)
        while (p != e) {
            p += direction
            run(p)
        }
    }

    private fun List<List<Point>>.toArea(withFloor: Boolean = false): Area {
        val minY = 0
        var maxY = this.maxOf { it.maxOf { it.y } }
        val data = if (withFloor) {
            maxY += 2
            toMutableList().apply {
                add(listOf(Point(-1000, maxY), Point(1000, maxY)))
            }
        } else {
            this
        }
        val minX = data.minOf { it.minOf { it.x } }
        val maxX = data.maxOf { it.maxOf { it.x } }
        val dx = maxX - minX + 1
        val dy = maxY - minY + 1
        val area = Array(dy) { IntArray(dx) { SPACE } }
        data.forEach { multiline ->
            multiline.windowed(size = 2).forEach { (s, e) ->
                forEachPointInLine(s, e) { p -> area[p.y - minY][p.x - minX] = ROCK }
            }
        }
        return Area(area, dimension = Point(dx, dy), startPoint = Point(500 - minX, 0 - minY))
    }

    fun part1(input: List<String>) = solve(input)

    fun part2(input: List<String>) = solve(input, withFloor = true)

    fun solve(input: List<String>, withFloor: Boolean = false): Int {
        val area = input.toPoints().toArea(withFloor)
        var found = true
        var total = 0
        while (found) {
            found = dropSandUnit(area)
            if (found) {
                ++total
            }
        }
        return total
    }

    private fun dropSandUnit(area: Area): Boolean {
        var p = area.startPoint
        while (area.includes(p)) {
            val nextPoint = area.nextMove(p)
            if (nextPoint == null) {
                if (area.get(p) == SPACE) {
                    area.put(p, SAND)
                    return true
                } else {
                    return false
                }
            } else {
                p = nextPoint
            }
        }
        return false
    }

}

fun main() {
    val testInput1 = readInput("Day${DAY}_test")
    val day = Day14()
    check(day.part1(testInput1).also { println(it) } == 24) { "Part1 Test - expected value doesn't match" }
    check(day.part2(testInput1).also { println(it) } == 93) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day${DAY}")
    println(day.part1(input))
    println(day.part2(input))
}
