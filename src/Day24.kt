class Day24 {

    private data class Point(val x: Int, val y: Int) {
        operator fun plus(d: Point) = Point(x + d.x, y + d.y)
    }

    private data class Blizzard(val direction: Point, val p0: Point)

    private companion object {
        val possibleMoves = listOf(Point(1, 0), Point(0, -1), Point(-1, 0), Point (0, 1), Point(0, 0))
    }

    private class Board(
        val startPoint: Point,
        val endPoint: Point,
        val width: Int,
        val height: Int,
        val hBlizzards: Map<Int, List<Blizzard>>,
        val vBlizzards: Map<Int, List<Blizzard>>
    ) {
        fun modulo(value: Int, max: Int): Int {
            return if (value > 0) {
                value % max
            } else {
                (value.rem(max) + max) % max
            }
        }

        fun isLocatedAt(p: Point, t: Int, blizzard: Blizzard): Boolean {
            val x = if (blizzard.direction.x != 0) {
                modulo(blizzard.p0.x + blizzard.direction.x * t, width)
            } else {
                blizzard.p0.x
            }
            val y = if (blizzard.direction.y != 0) {
                modulo(blizzard.p0.y + blizzard.direction.y * t, height)
            } else {
                blizzard.p0.y
            }
            return p.x == x && p.y == y
        }

        fun isFree(p: Point, t: Int): Boolean {
            return (hBlizzards[p.y]?.none { isLocatedAt(p, t, it) } ?: true) &&
                    (vBlizzards[p.x]?.none { isLocatedAt(p, t, it) } ?: true)
        }

        fun isValidPos(p: Point): Boolean {
            return ((p.x in 0 until width) && (p.y in 0 until height)) ||
                    p == startPoint ||
                    p == endPoint
        }

        fun possibleNextPos(p: Point, t: Int): List<Point> {
            return possibleMoves.mapNotNull { d ->
                    val nextPos = (p + d)
                    nextPos.takeIf { isValidPos(it) }
                }
                .filter { isFree(it, t + 1) }
        }
    }

    private data class Solution(val startPoint: Point, val endPoint: Point, val pos: Point, val t: Int, val allPos: List<Point>)

    private fun findRoute(board: Board, startTime: Int = 0,
                          startPoint: Point = board.startPoint, endPoint: Point = board.endPoint): Int {

        var solutions = mutableListOf<Solution>()
        solutions.add(
            Solution(
                startPoint = startPoint,
                endPoint = endPoint,
                pos = startPoint,
                t = startTime,
                allPos = listOf(startPoint)
            )
        )

        var theBestSolution: Solution? = null

        while (solutions.isNotEmpty()) {
            val newSolutions = mutableListOf<Solution>()
            for (solution in solutions) {
                if (solution.pos == endPoint) {
                    theBestSolution = solution
                    break
                } else {
                    board.possibleNextPos(solution.pos, solution.t)
                        .map { nextPos ->
                            solution.copy(
                                pos = nextPos,
                                t = solution.t + 1,
                                allPos = solution.allPos + nextPos,
                            )
                        }
                        .forEach { newSolutions.add(it) }
                }
            }
            if (theBestSolution != null) {
                break
            }
            solutions = newSolutions.groupBy { it.pos }.map { it.value.first() }.take(1_000).toCollection(ArrayDeque())
        }

        return theBestSolution?.t ?: -1
    }

    fun part1(input: List<String>): Int {
        val board = input.parse()
        return findRoute(board)
    }

    fun part2(input: List<String>): Int {
        val board = input.parse()
        val way1 = findRoute(board)
        val way2 = findRoute(board, startPoint = board.endPoint, endPoint = board.startPoint, startTime = way1)
        return findRoute(board, startTime = way2)
    }

    private fun List<String>.parse(): Board {
        val startPoint = Point(first().indexOf('.') - 1, -1)
        val endPoint = Point(last().indexOf('.') - 1, size - 2)
        val h = mutableMapOf<Int, MutableList<Blizzard>>()
        val v = mutableMapOf<Int, MutableList<Blizzard>>()
        drop(1).dropLast(1).withIndex().forEach { (y, line) ->
            line.drop(1).dropLast(1).withIndex().forEach { (x, ch) ->
                val dir = when (ch) {
                    '>' -> Point(1, 0)
                    '<' -> Point(-1, 0)
                    '^' -> Point(0, -1)
                    'v' -> Point(0, 1)
                    else -> null
                }
                if (dir != null) {
                    val blizzard = Blizzard(dir, Point(x, y))
                    if (dir.x != 0) {
                        h.computeIfAbsent(y) { mutableListOf() }.add(blizzard)
                    } else {
                        v.computeIfAbsent(x) { mutableListOf() }.add(blizzard)
                    }
                }
            }
        }
        return Board(
            startPoint = startPoint,
            endPoint = endPoint,
            width = first().length - 2,
            height = size - 2,
            hBlizzards = h,
            vBlizzards = v,
        )
    }

}

fun main() {
    val dayClass = Day24::class.java.simpleName
    val testInput1 = readInput("${dayClass}_test")
    val day = Day24()

    check(day.part1(testInput1).also { println("Part 1 test result: $it") } == 18) { "Part1 Test - expected value doesn't match" }
    check(day.part2(testInput1).also { println("Part 2 test result: $it") } == 54) { "Part2 Test - expected value doesn't match" }

    val input = readInput(dayClass)
    println("Part 1 result: " + day.part1(input))
    println("Part 2 result: " + day.part2(input))
}
