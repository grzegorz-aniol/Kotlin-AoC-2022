const val DAY = "10"
fun main() {
    fun List<String>.parseInput(): List<Int?> = map {
        if (it == "noop") {
            null
        } else {
            it.split(" ")[1].toInt()
        }
    }
    fun numOfCycles(value: Int?) = if (value != null) 2 else 1
    fun isSignalCycle(index: Int) = if (index <= 220) (index % 40 == 20) else false

    fun part1(input: List<String>): Int {
        var acc = 1
        var cycle = 0
        var sum = 0
        input.parseInput().forEach { value ->
            repeat (numOfCycles(value)) {
                cycle += 1
                if (isSignalCycle(cycle)) {
                    sum += cycle * acc
                }
            }
            value?.let { acc += it }
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        var acc = 1
        var cycle = 0
        var col = 0
        input.parseInput().forEach { value ->
            repeat(numOfCycles(value)) {
                cycle += 1
                val pixel = if (col in acc - 1..acc + 1) "█" else " "
                print(pixel)
                col = (col + 1) % 40
                if (cycle % 40 == 0) {
                    println()
                }
            }
            value?.let { acc += it }
        }
        return 0
    }

    val testInput1 = readInput("Day${DAY}_test")
    check(part1(testInput1).also { println(it) } == 13140) { "Part1 Test - expected value doesn't match" }
    check(part2(testInput1).also { println(it) } == 0) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day${DAY}")
    println(part1(input))
    println(part2(input))
}
