fun main() {

    fun List<String>.splitToRanges(): List<Pair<IntRange, IntRange>> =
        map { text ->
            val numbers = text.split(",").flatMap { it.split("-") }.map { it.toInt() }
            check (numbers.size == 4)
            Pair(numbers[0]..numbers[1], numbers[2]..numbers[3])
        }

    fun IntRange.contains(other: IntRange) =
        other.first in this && other.last in this

    fun fullyContains(ranges: Pair<IntRange, IntRange>) =
        ranges.first.contains(ranges.second) || ranges.second.contains(ranges.first)

    fun overlaps(pairOfRanges: Pair<IntRange, IntRange>) =
        pairOfRanges.first.intersect(pairOfRanges.second).isNotEmpty()

    fun part1(input: List<String>): Int =
        input.splitToRanges().count(::fullyContains)

    fun part2(input: List<String>): Int =
        input.splitToRanges().count(::overlaps)

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2) { "Part1 Test - expected value doesn't match" }
    check(part2(testInput) == 4) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
