import kotlin.math.abs
import kotlin.math.sign

fun main() {
    fun part1(input: List<String>) = followTail(input.asMoves(), knotsCount = 2)
    fun part2(input: List<String>) = followTail(input.asMoves(), knotsCount = 10)

    val testInput1 = readInput("Day09_test1")
    check(part1(testInput1).also { println(it) } == 13) { "Part1 Test - expected value doesn't match" }

    val testInput2 = readInput("Day09_test2")
    check(part2(testInput2).also { println(it) } == 36) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}

fun followTail(moves: List<Move>, knotsCount: Int): Int {
    val knots = Array(knotsCount) { Point(0, 0) }
    val visited = mutableSetOf<Point>().also { it.add(knots.last()) }
    moves.forEach { move ->
        repeat (move.steps) {
            knots[0] += move.delta
            for (i in 1 until knotsCount) {
                val prevKnot = knots[i - 1]
                if (prevKnot.distance(knots[i]) > 1) {
                    knots[i] += knots[i].direction(prevKnot)
                }
            }
            visited.add(knots.last())
        }
    }
    return visited.count()
}

data class Point(val x: Int, val y: Int) {
    operator fun plus(d: Point) = Point(x + d.x, y + d.y)
    fun distance(other: Point) = when {
        abs(x - other.x) <= 1 && abs(y - other.y) <= 1 -> 1
        else -> 2
    }
    fun direction(other: Point) = Point((other.x - x).sign, (other.y - y).sign)
}

data class Move(val delta: Point, val steps: Int)

fun List<String>.asMoves(): List<Move> = map {
    val line = it.split(" ")
    val steps = line[1].toInt()
    when (line[0]) {
        "R" -> Move(Point(1, 0), steps)
        "L" -> Move(Point(-1, 0), steps)
        "U" -> Move(Point(0, 1), steps)
        else -> Move(Point(0, -1), steps)
    }
}
