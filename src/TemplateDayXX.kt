private const val DAY = 13

class TemplateDayXX {

    fun part1(input: List<String>): Int {
        return 0
    }

    fun part2(input: List<String>): Int {
        return 0
    }

}

fun main() {
    val testInput1 = readInput("Day${DAY}_test")
    val day = Day14()
    check(day.part1(testInput1).also { println(it) } == -1) { "Part1 Test - expected value doesn't match" }
    check(day.part2(testInput1).also { println(it) } == -1) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day${DAY}")
    println(day.part1(input))
    println(day.part2(input))
}
