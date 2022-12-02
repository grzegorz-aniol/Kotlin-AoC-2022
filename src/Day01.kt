fun main() {
    fun part1(input: List<String>): Int {
        val result = input.toNumbers()
            .fold(Int.MIN_VALUE to 0) { (globalMax, partialSum), value ->
                if (value == null) {
                    globalMax.coerceAtLeast(partialSum) to 0
                } else {
                    globalMax to partialSum + value
                }
            }
        return result.first.coerceAtLeast(result.second)
    }

    class Accumulator(val calories: MutableList<Int> = mutableListOf(), val partialSum: Int = 0)

    fun part2(input: List<String>): Int {
        val result = input.toNumbers()
            .fold(Accumulator()) { acc, value ->
                Accumulator(
                    acc.calories, if (value == null) {
                        acc.calories.add(acc.partialSum)
                        0
                    } else {
                        acc.partialSum + value
                    }
                )
            }

        if (result.partialSum > 0) {
            result.calories.add(result.partialSum)
        }

        return result.calories.sortedDescending().toList().take(3).sum()
    }

    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)
    check(part2(testInput) == 45000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
