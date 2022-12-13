private const val DAY = 12

class Day12 {
    data class Point(val x: Int, val y: Int) {
        operator fun plus(d: Point) = Point(x + d.x, y + d.y)
    }

    class Area(input: List<String>) {
        val map: Array<IntArray>
        var start: Point
        var end: Point
        val dimension: Point

        init {
            var s = Point(0, 0)
            var e = Point(0, 0)
            map = input.mapIndexed { y, row ->
                row.mapIndexed { x, ch ->
                    when (ch) {
                        'S' -> {
                            s = Point(x, y)
                            0
                        }
                        'E' -> {
                            e = Point(x, y)
                            'z' - 'a'
                        }
                        else -> ch - 'a'
                    }
                }.toIntArray()
            }.toTypedArray()
            start = s
            end = e
            dimension = Point(map.first().count(), map.count())
        }

        fun reverse() {
            val x = start
            start = end
            end = x
            map.forEach { for (i in it.indices) it[i] = -it[i] }
        }

        fun getNeighbour(p: Point, d: Point): Point? =
            (p + d).takeIf { it.x in 0 until dimension.x && it.y in 0 until dimension.y }

        fun height(p: Point) = map[p.y][p.x]
    }

    class Path(private val points: MutableList<Point> = mutableListOf()) {
        fun last() = points.last()
        fun count() = points.count()
        operator fun plus(p: Point): Path {
            return Path(points.plus(p).toMutableList())
        }
    }

    private val directions = listOf(Point(1, 0), Point(-1, 0), Point(0, 1), Point(0, -1))

    fun part1(input: List<String>): Int {
        val area = Area(input)
        val all = ArrayDeque<Path>()
        var record = Int.MAX_VALUE
        val visited = Array(area.dimension.y) { BooleanArray(area.dimension.x) { false } }

        all.add(Path(mutableListOf(area.start)))
        visited[area.start.y][area.start.x] = true

        while (all.isNotEmpty()) {
            val path = all.removeLast()
            val point = path.last()
            if (point == area.end) {
                record = Math.min(record, path.count())
                break
            }
            if (path.count() > record) {
                break
            }

            val height = area.height(point)
            directions.mapNotNull { p -> area.getNeighbour(point, p) }
                .filter { p -> !visited[p.y][p.x] }
                .filter { p ->
                    val ith = area.height(p)
                    (height + 1 == ith) || (height >= ith)
                }.sortedWith(compareByDescending { area.height(it) })
                .forEach { p->
                    visited[p.y][p.x] = true
                    all.addFirst(path + p)
                }
        }

        return record - 1
    }

    fun part2(input: List<String>): Int {
        val area = Area(input)
        area.reverse() // to solve part2 it's easier to reverse START & END and heights
        val all = ArrayDeque<Path>()
        var record = Int.MAX_VALUE
        val visited = Array(area.dimension.y) { BooleanArray(area.dimension.x) { false } }

        all.add(Path(mutableListOf(area.start)))
        visited[area.start.y][area.start.x] = true

        while (all.isNotEmpty()) {
            val path = all.removeLast()
            val point = path.last()
            if (area.height(point) == 0) {
                record = Math.min(record, path.count())
                break
            }
            if (path.count() > record) {
                break
            }

            val height = area.height(point)
            directions.mapNotNull { p -> area.getNeighbour(point, p) }
                .filter { p -> !visited[p.y][p.x] }
                .filter { p ->
                    val ith = area.height(p)
                    (height + 1 == ith) || (height >= ith)
                }.sortedWith(compareByDescending { area.height(it) })
                .forEach { p->
                    visited[p.y][p.x] = true
                    all.addFirst(path + p)
                }
        }

        return record - 1
    }
}

fun main() {
    val testInput1 = readInput("Day${DAY}_test")
    val day = Day12()
    check(day.part1(testInput1).also { println(it) } == 31) { "Part1 Test - expected value doesn't match" }
    check(day.part2(testInput1).also { println(it) } == 29) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day${DAY}")
    println(day.part1(input))
    println(day.part2(input))
}
