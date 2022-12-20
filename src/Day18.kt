class Day18 {

    private data class Point3D(val x: Int, val y: Int, val z:Int) {
        operator fun plus(other: Point3D) = Point3D(x + other.x, y + other.y, z + other.z)
    }

    private val directions = setOf(Point3D(-1, 0, 0), Point3D(1, 0, 0),
        Point3D(0, -1, 0), Point3D(0, 1, 0), Point3D(0, 0, -1), Point3D(0, 0, 1)
    )

    private fun List<String>.toPoints(): Set<Point3D> = asSequence().map {
            val (x, y, z) = it.split(",").map { it.toInt() }
            Point3D(x, y, z)
        }.toSet()

    private fun <T> Array<MutableList<T>>.withNextNotEmpty() = sequence {
            for (index in 0 until size - 1) {
                val item1 = get(index)
                val item2 = get(index + 1)
                if (item1.isNotEmpty() && item2.isNotEmpty()) {
                    yield(item1 to item2)
                }
            }
        }

    private fun detectAdj(points1: List<Point3D>, points2: List<Point3D>): Int {
        return points1.sumOf { p1 ->
            points2.count { p2 ->
                (p1.x == p2.x && p1.y == p2.y) ||
                (p1.y == p2.y && p1.z == p2.z) ||
                (p1.z == p2.z && p1.x == p2.x)
            }
        }
    }

    private fun calculateSurface(points: Set<Point3D>): Int {
        val maxX = points.maxOf { it.x }
        val maxY = points.maxOf { it.y }
        val maxZ = points.maxOf { it.z }
        val projectionX = Array(maxX + 1) { mutableListOf<Point3D>() }
        val projectionY = Array(maxY + 1) { mutableListOf<Point3D>() }
        val projectionZ = Array(maxZ + 1) { mutableListOf<Point3D>() }
        points.forEach { p ->
            projectionX[p.x].add(p)
            projectionY[p.y].add(p)
            projectionZ[p.z].add(p)
        }
        val adjX = projectionX.withNextNotEmpty().sumOf { (list1, list2) -> detectAdj(list1, list2) }
        val adjY = projectionY.withNextNotEmpty().sumOf { (list1, list2) -> detectAdj(list1, list2) }
        val adjZ = projectionZ.withNextNotEmpty().sumOf { (list1, list2) -> detectAdj(list1, list2) }
        return 6 * points.size - 2 * (adjX + adjY + adjZ)
    }

    private fun calculateExternalSurface(points: Set<Point3D>): Int {
        val visited = mutableSetOf<Point3D>()
        val queue = ArrayDeque<Point3D>()
        val rangeX = -1 .. points.maxOf { it.x } + 1
        val rangeY = -1 .. points.maxOf { it.y } + 1
        val rangeZ = -1 .. points.maxOf { it.z } + 1
        var count = 0
        queue.addLast(Point3D(-1, -1, -1))
        while (queue.isNotEmpty()) {
            val p = queue.removeLast()
            if (p in points) {
                ++count
                continue
            }
            if (p in visited) {
                continue
            }
            visited.add(p)
            for (d in directions) {
                val np = p + d
                if (np.x in rangeX &&
                    np.y in rangeY &&
                    np.z in rangeZ) {
                    queue.addLast(np)
                }
            }
        }
        return count
    }

    fun part1(input: List<String>): Int {
        return calculateSurface(input.toPoints())
    }

    fun part2(input: List<String>): Int {
        return calculateExternalSurface(input.toPoints())
    }

}

fun main() {
    val dayClass = Day18::class.java.simpleName
    val testInput1 = readInput("${dayClass}_test")
    val day = Day18()
    check(day.part1(testInput1).also { println(it) } == 64) { "Part1 Test - expected value doesn't match" }
    check(day.part2(testInput1).also { println(it) } == 58) { "Part2 Test - expected value doesn't match" }

    val input = readInput(dayClass)
    println(day.part1(input))
    println(day.part2(input))
}
