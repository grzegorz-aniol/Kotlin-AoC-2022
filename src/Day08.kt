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

    fun part2(input: List<String>): Long {
        val map = parseMap(input)
        val rows = input.size
        val cols = input.first().length

        fun scoreView(sx: Int, sy: Int, dx: Int, dy: Int): Long {
            val pointHeight = map[sy][sx]
            var x = sx
            var y = sy
            var score = 0L
            while (x in 0 until cols && y in 0 until rows) {
                if (x != sx || y != sy) {
                    score += 1
                    val height = map[y][x]
                    if (height >= pointHeight) {
                        break
                    }
                }
                x += dx
                y += dy
            }
            return score
        }

        var maxScore = 0L
        for (y in 0 until rows - 1) {
            for (x in 0 until cols - 1) {
                val s1 = scoreView(x, y, 1, 0)
                val s2 = scoreView(x, y, -1, 0)
                val s3 = scoreView(x, y, 0, 1)
                val s4 = scoreView(x, y, 0, -1)
                val totalScore = s1 * s2 * s3 * s4
                maxScore = Math.max(maxScore, totalScore)
            }
        }
        return maxScore
    }

    val testInput = readInput("Day08_test")
    check(part1(testInput).also { println(it) } == 21) { "Part1 Test - expected value doesn't match" }
    check(part2(testInput).also { println(it) } == 8L) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day08")
    println(part1(input)) // 1684
    println(part2(input)) // 486540
}
