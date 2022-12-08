fun main() {

    fun parseMap(input: List<String>): Array<IntArray> {
        val array = Array(input.size) { IntArray(input.first().length) }
        for ((row, line) in input.withIndex()) {
            for ((col, ch) in line.withIndex()) {
                array[row][col] = Integer.parseInt(ch.toString())
            }
        }
        return array
    }

    fun part1(input: List<String>): Int {
        val map = parseMap(input)
        val rows = input.size
        val cols = input.first().length
        val visibilityMap = Array(rows) { IntArray(cols) { 0 } }

        fun analyzeLine(sx: Int, sy: Int, dx: Int, dy: Int) {
            var height = -1
            var x = sx
            var y = sy
            while (x in 0 until cols && y in 0 until rows) {
                if (map[y][x] > height) {
                    visibilityMap[y][x] = 1
                }
                height = Math.max(height, map[y][x])
                x += dx
                y += dy
            }
        }

        for (inrows in 0 until rows) {
            analyzeLine(0, inrows, 1, 0)
            analyzeLine(cols - 1, inrows, -1, 0)
        }
        for (incols in 0 until cols) {
            analyzeLine(incols, 0, 0, 1)
            analyzeLine(incols, rows-1, 0, -1)
        }
        return visibilityMap.sumOf { it.sum() }
    }

    fun part2(input: List<String>) = 0

    val testInput = readInput("Day08_test")
    check(part1(testInput).also { println(it) } == 21) { "Part1 Test - expected value doesn't match" }
//    check(part2(testInput) == -1) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day08")
    println(part1(input))
//    println(part2(input))
}
