class Day22 {

    companion object {
        private val EMPTY = 0
        private val FREE = 1
        private val BLOCK = 2
        private val MAX_ROW_SIZE = 152

        private fun rollover(value: Int, change: Int, width: Int) = when {
            change > 0 -> (value + 1) % width
            change < 0 -> (value + (width - 1)) % width
            else -> value
        }
    }

    private data class Order(val rotate: Int?, val forward: Int?)

    private data class Board(val map: Array<IntArray>, val orders: List<Order>) {
        val visited = mutableSetOf<Point>()

        fun movePoint(p: Point, d: Point): Point {
            var np = p
            do {
                np = Point(rollover(np.x, d.x, MAX_ROW_SIZE), rollover(np.y, d.y, map.size))
            } while (map[np.y][np.x] == EMPTY)
            return np
        }

        fun showBoard() {
            for (y in map.indices) {
                println(map[y].withIndex().map { (x, value) ->
                    if (Point(x, y) in visited) {
                        "*"
                    } else {
                        when (value) {
                            0 -> " "
                            1 -> "."
                            else -> "#"
                        }
                    }
                }.joinToString(separator = ""))
            }
            println()
        }
    }

    private val allDirections = listOf(Point(1, 0), Point(0, 1), Point(-1, 0), Point(0, -1))

    private fun List<String>.parse(): Board {
        val iter = iterator()
        val rows = MutableList(0) { IntArray(0) }
        while (iter.hasNext()) {
            val line = iter.next()
            if (line.isBlank()) {
                break
            }
            val row = line.indices.map { x ->
                when (line[x]) {
                    '.' -> FREE
                    '#' -> BLOCK
                    else -> EMPTY
                }
            }.toMutableList()
            while (row.size < MAX_ROW_SIZE) {
                row.add(EMPTY)
            }
            rows += row.toIntArray()
        }
        val reg = Regex("(\\d+)|([RL])")
        val orders = reg.findAll(iter.next()).map {
            val move = it.groups[1]?.value?.toInt()
            val rotate = when (it.groups[2]?.value) {
                "R" -> 1
                "L" -> -1
                else -> null
            }
            Order(rotate, move)
        }.toList()
        return Board(map = rows.toTypedArray(), orders = orders)
    }


    fun part1(input: List<String>): Int {
        val board = input.parse()

        var heading = 0
        var point = Point(board.map[0].indexOfFirst { it != EMPTY }, 0)

        board.showBoard()

        for (order in board.orders) {
            board.visited += point
            order.rotate?.let { rotate ->
                heading = rollover(heading, rotate, allDirections.size)
            }
            order.forward?.let { move ->
                for (step in 1..move) {
                    val nextPoint = board.movePoint(point, allDirections[heading])
                    val nextPlace = board.map[nextPoint.y][nextPoint.x]
                    if (nextPlace == BLOCK) {
                        break
                    }
                    point = nextPoint
                    board.visited += point
                }
            }
        }
        board.showBoard()

        return 1000 * (point.y + 1) + 4 * (point.x + 1) + heading
    }

    fun part2(input: List<String>): Int {
        return 0
    }

}

fun main() {
    val dayClass = Day22::class.java.simpleName
    val testInput1 = readInput("${dayClass}_test")
    val day = Day22()
    check(day.part1(testInput1).also { println(it) } == 6032) { "Part1 Test - expected value doesn't match" }
//    check(day.part2(testInput1).also { println(it) } == -1) { "Part2 Test - expected value doesn't match" }

    val input = readInput(dayClass)
    println(day.part1(input)) // 49284 is too low
//    println(day.part2(input))
}
