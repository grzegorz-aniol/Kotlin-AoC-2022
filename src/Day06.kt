fun main() {

    // first solution: the simple one
    fun findUniquePatternSimple(buffer: String, len: Int) = buffer.withIndex()
        .windowed(size = len)
        .first { it.map { v -> v.value }.toSet().size == len }
        .last().index + 1

    // second solution, more time efficient
    fun findUniquePatternAdvanced(buffer: String, len: Int): Int {
        // use Map to count elements in range
        fun MutableMap<Char, Int>.include(key: Char)  = compute(key) { k, v -> v?.let { it + 1 } ?: 1 }
        fun MutableMap<Char, Int>.exclude(key: Char)  = compute(key) { k, v -> if (v == 1 || v == null) null else v - 1 }
        val m = mutableMapOf<Char, Int>()
        for ((index, value) in buffer.withIndex()) {
            m.include(value)
            if (m.size == len && m.all { it.value == 1 }) {
                return index + 1
            }
            if (index + 1 >= len) {
                m.exclude(buffer[index + 1 -len])
            }
        }
        return -1
    }

    fun part1(input: List<String>) = findUniquePatternAdvanced(input[0], len = 4)

    fun part2(input: List<String>) = findUniquePatternAdvanced(input[0], len = 14)

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 7) { "Part1 Test - expected value doesn't match" }
    check(part2(testInput) == 19) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day06")
    println(part1(input)) // 1531
    println(part2(input)) // 2518
}
