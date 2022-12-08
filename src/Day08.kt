typealias Matrix = Array<IntArray>
typealias BreakFun = (score: Long, startHeight: Int, height: Int) -> Pair<Boolean, Long>
typealias TermFun = (score: Long) -> Long
typealias AccFun = (Long, Long, Long, Long, Long) -> Long

fun main() {

    fun List<String>.toMatrix() = map { it.map { it - '0' }.toIntArray() }.toTypedArray()

    fun Matrix.walk(sx: Int, sy: Int, dx: Int, dy: Int, breakFun: BreakFun, termFun: TermFun): Long {
        var (x, y) = sx to sy
        val (rows, cols) = this.size to this.first().size
        var points = 0L
        while (x in 0 until cols && y in 0 until rows) {
            if (x != sx || y != sy) {
                points += 1
                val breakStatus = breakFun(points, this[sy][sx], this[y][x])
                if (breakStatus.first) {
                    return breakStatus.second
                }
            }
            x += dx
            y += dy
        }
        return termFun(points)
    }

    fun Matrix.calculateScore(breakFun: BreakFun, termFun: TermFun, accFun: AccFun): Long {
        val (rows, cols) = this.size to this.first().size
        var score = 0L
        for (y in 0 until rows) {
            for (x in 0 until cols) {
                val v1 = walk(x, y, 1, 0, breakFun, termFun)
                val v2 = walk(x, y, -1, 0, breakFun, termFun)
                val v3 = walk(x, y, 0, 1, breakFun, termFun)
                val v4 = walk(x, y, 0, -1, breakFun, termFun)
                score = accFun(score, v1, v2, v3, v4)
            }
        }
        return score
    }

    fun part1(input: List<String>): Long {
        val matrix = input.toMatrix()

        val breakFun: BreakFun = { _, startHeight: Int, height: Int -> (height >= startHeight) to 0 }
        val termFun: TermFun = { _ -> 1 }
        val accFun: AccFun = { prev, v1, v2, v3, v4 -> prev + Math.min(1, v1 + v2 + v3 + v4) }

        return matrix.calculateScore(breakFun, termFun, accFun)
    }

    fun part2(input: List<String>): Long {
        val matrix = input.toMatrix()

        val breakFun: BreakFun = { score, startHeight: Int, height: Int -> (height >= startHeight) to score }
        val termFun: TermFun = { score -> score }
        val accFun: AccFun = { prev, v1, v2, v3, v4 -> Math.max(prev, v1 * v2 * v3 * v4) }

        return matrix.calculateScore(breakFun, termFun, accFun)
    }

    val testInput = readInput("Day08_test")
    check(part1(testInput).also { println(it) } == 21L) { "Part1 Test - expected value doesn't match" }
    check(part2(testInput).also { println(it) } == 8L) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
