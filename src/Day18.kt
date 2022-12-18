class Day18 {

    private data class Point3D(val x: Int, val y: Int, val z:Int)

    private fun List<String>.toPoints(): List<Point3D> = map {
            val (x, y, z) = it.split(",").map { it.toInt() }
            Point3D(x, y, z)
        }

    private fun <T> Array<MutableList<T>>.withNextNotEmpty() = sequence {
            for (index in 0 until size - 1) {
                val item1 = get(index)
                val item2 = get(index + 1)
                if (item1.isNotEmpty() && item2.isNotEmpty()) {
                    yield(item1 to item2)
                }
            }
        }

    private fun detectAdj(list1: List<Point3D>, list2: List<Point3D>): Int {
        return list1.sumOf { p1 ->
            list2.count { p2 ->
                (p1.x == p2.x && p1.y == p2.y) ||
                (p1.y == p2.y && p1.z == p2.z) ||
                (p1.z == p2.z && p1.x == p2.x)
            }
        }
    }

    private fun calculateSurface(points: List<Point3D>): Int {
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

    fun part1(input: List<String>): Int {
        return calculateSurface(input.toPoints())
    }

    fun part2(input: List<String>): Int {
        return 0
    }

}

fun main() {
    val dayClass = Day18::class.java.simpleName
    val testInput1 = readInput("${dayClass}_test")
    val day = Day18()
    check(day.part1(testInput1).also { println(it) } == 64) { "Part1 Test - expected value doesn't match" }
//    check(day.part2(testInput1).also { println(it) } == -1) { "Part2 Test - expected value doesn't match" }

    val input = readInput(dayClass)
    println(day.part1(input))
//    println(day.part2(input))
}
