fun main() {

    fun fullyContains(r1: IntRange, r2: IntRange) =
        r1.intersect(r2).let { it ->
            it.size == r1.count() || it.size == r2.count()
        }

    fun List<String>.splitToRanges(): List<Pair<IntRange, IntRange>> =
        map { text ->
            val numbers = text.split(",").flatMap { it.split("-") }.map { it.toInt() }
            check (numbers.size == 4)
            Pair(numbers[0]..numbers[1], numbers[2]..numbers[3])
        }

    fun part1(input: List<String>): Int =
        input.splitToRanges().count {fullyContains(it.first, it.second) }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2) { "Part1 Test - expected value doesn't match" }
//    check(part2(testInput) == 70) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day04")
    println(part1(input)) //
//    println(part2(input)) //
}
