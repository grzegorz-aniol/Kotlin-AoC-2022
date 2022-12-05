fun main() {

    class Movement(val count: Int, val from: Int, val to: Int)

    class Rearrangement(
        val stacks: Array<ArrayDeque<Char>>,
        val procedure: List<Movement>
    )

    fun parseInput(input: List<String>): Rearrangement {
        val stacks = Array<ArrayDeque<Char>>(10) { ArrayDeque() }
        input.takeWhile { it.isNotBlank() }.reversed().drop(1)
            .forEach { line ->
                (0..9).forEach { index -> line.getOrNull(4 * index + 1)?.takeIf { it.isLetter() }?.let { stacks[index].add(it) } }
            }
        val moves = input.takeLastWhile { it.isNotBlank() }
            .map { line ->
                val (count, from, to) = line.split(" ").withIndex()
                    .filter { it.index % 2 != 0 }
                    .map { Integer.parseInt(it.value) }
                Movement(count, from - 1, to - 1)
            }
        return Rearrangement(stacks, moves)
    }

    fun rearrange(r: Rearrangement): String {
        r.procedure.forEach { movement ->
            val stackFrom = r.stacks[movement.from]
            val stackTo = r.stacks[movement.to]
            repeat(movement.count) { _ -> stackTo.add(stackFrom.removeLast()) }
        }
        return String(r.stacks.mapNotNull { it.lastOrNull() }.toCharArray())
    }

    fun part1(input: List<String>): String {
        return rearrange(parseInput(input))
    }

    fun part2(input: List<String>): String = ""

    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ") { "Part1 Test - expected value doesn't match" }
    check(part2(testInput) == "MCD") { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day05")
    println(part1(input)) // JCMHLVGMG
//    println(part2(input)) // 792
}
