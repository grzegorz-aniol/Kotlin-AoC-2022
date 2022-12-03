fun main() {

    fun typePriority(ch: Char): Int = when (ch) {
        in 'a'..'z' -> ch - 'a' + 1
        else -> ch - 'A' + 27
    }

    fun part1(input: List<String>): Int {
        return input.sumOf { text ->
            val compartment1 = text.slice(0 until text.length / 2)
            val compartment2 = text.slice(text.length / 2 until text.length)
            check(compartment1.length == compartment2.length)
            val commonType = compartment1.toSet().intersect(compartment2.toSet())
            check(commonType.size == 1)
            typePriority(commonType.first())
        }
    }

    fun part2(input: List<String>): Int {
        return input.windowed(size = 3, step = 3).sumOf { group ->
            val badgeType = group.drop(1).fold(group.first().toSet()) { acc, backpack ->
                acc.intersect(backpack.toSet())
            }
            check(badgeType.size == 1)
            typePriority(badgeType.first())
        }
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157) { "Part1 Test - expected value doesn't match" }
    check(part2(testInput) == 70) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day03")
    println(part1(input)) // 8153
    println(part2(input)) // 2342
}
