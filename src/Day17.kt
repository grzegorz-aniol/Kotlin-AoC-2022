import java.math.BigInteger

private const val DAY = 17

private const val LEFT = -1
private const val RIGHT = 1

private const val INITIAL_HEIGHT = 4
private const val WIDTH = 7

private const val EMPTY = 0
private const val ROCK = 1

class Day17 {

    private data class Point(val x: Int, val y: Int) {
        operator fun plus(d: Point) = Point(x + d.x, y + d.y)
    }

    private data class Block(val rocks: List<Point>) {
        constructor(vararg points: Point) : this(points.toList())

        operator fun plus(d: Point) = Block(rocks.map { it + d })
    }

    private val blockTemplates = listOf(
        Block(Point(0, 0), Point(1, 0), Point(2, 0), Point(3, 0)),
        Block(Point(0, 1), Point(1, 1), Point(2, 1), Point(1, 0), Point(1, 2)),
        Block(Point(0, 0), Point(1, 0), Point(2, 0), Point(2, 1), Point(2, 2)),
        Block(Point(0, 0), Point(0, 1), Point(0, 2), Point(0, 3)),
        Block(Point(0, 0), Point(0, 1), Point(1, 0), Point(1, 1)),
    )

    private class Chamber {
        val levels = MutableList(INITIAL_HEIGHT) { IntArray(WIDTH) { EMPTY } }
        fun addLevel() {
            levels.add(IntArray(WIDTH) { EMPTY })
        }

        fun getTopLevel(): Int =
            levels.asIterable().withIndex().reversed().firstOrNull { it.value.any { it == ROCK } }?.index ?: -1

        fun getStartingPosition(block: Block): Block {
            val startPoint = Point(2, getTopLevel() + 4)
            val nextBlock = block + startPoint
            nextBlock.rocks.forEach {
                while (levels.size - 1 < it.y) {
                    addLevel()
                }
            }
            return nextBlock
        }

        fun getNextPosition(block: Block, direction: Point): Block? {
            val candidate = block + direction
            if (candidate.rocks.any { it.x < 0 || it.x >= WIDTH || it.y < 0 }) {
                return null
            }
            if (candidate.rocks.any { levels[it.y][it.x] != EMPTY }) {
                return null
            }
            return candidate
        }

        fun lockBlock(block: Block) {
            block.rocks.forEach { levels[it.y][it.x] = ROCK }
        }

        fun print(block: Block, limit: Int? = 30) {
            levels.withIndex().reversed()
                .filter { (y, _) -> limit?.let { it > levels.size - y } ?: true }
                .forEach { (y, row) ->
                    println(row.withIndex().map { (x, value) ->
                        if (block.rocks.contains(Point(x, y))) {
                            return@map '@'
                        }
                        if (value == ROCK) '#' else '.'
                    }.joinToString(separator = ""))
                }
            println()
        }
    }

    private infix fun Int.nextOf(size: Int) = (this + 1) % size

    private fun List<String>.parseJets(): List<Int> {
        return get(0).map {
            when (it) {
                '<' -> LEFT
                else -> RIGHT
            }
        }
    }

    private fun simulate(chamber: Chamber, jets: List<Int>, rounds: BigInteger): BigInteger {
        var jetIndex = 0
        var blockIndex = 0
        var reduceIndex = 0
        var round = BigInteger.ZERO

        while (round < rounds) {
            val blockTemplate = blockTemplates[blockIndex]
            blockIndex = blockIndex nextOf blockTemplates.size
            var block = chamber.getStartingPosition(blockTemplate)
            var canMoveDown = true
            while (canMoveDown) {
                val jetDirection = jets[jetIndex]
                jetIndex = jetIndex nextOf jets.size

                val jetMoveDirection = Point(jetDirection, 0)
                chamber.getNextPosition(block, jetMoveDirection)?.also { block = it }

                val downMoveDirection = Point(0, -1)
                if (chamber.getNextPosition(block, downMoveDirection)?.also { block = it } == null) {
                    canMoveDown = false
                }
            }
            chamber.lockBlock(block)
            reduceIndex = reduceIndex nextOf 1_000
            round = round.plus(BigInteger.ONE)
        }

        return BigInteger.valueOf(chamber.getTopLevel().toLong()) + BigInteger.ONE
    }

    data class ChangePerBlock(val jetIndex: Int, val grow: Int)
    data class Pattern(val sequence: List<ChangePerBlock>, val grow: Int)

    data class Cycle(
        val indexRound: Int,
        val indexGrow: Int,
        val cycleRound: Int,
        val cycleGrow: Int,
        val actualSequence: List<Pattern>
    )

    private fun simulateAndDetectCycle(chamber: Chamber, jets: List<Int>, rounds: BigInteger): Cycle {
        var jetIndex = 0
        var blockIndex = 0
        var round = BigInteger.ZERO
        var prevJetIndex = 0
        var prevTop = 0

        val currentSequence = mutableListOf<ChangePerBlock>()
        val patterns = mutableListOf<Pattern>()

        while (round < rounds) {
            val topBeforeRound = chamber.getTopLevel()
            val jetIndexBeforeRound = jetIndex

            val blockTemplate = blockTemplates[blockIndex]
            var block = chamber.getStartingPosition(blockTemplate)
            var canMoveDown = true
            while (canMoveDown) {
                val jetDirection = jets[jetIndex]
                jetIndex = jetIndex nextOf jets.size

                val jetMoveDirection = Point(jetDirection, 0)
                chamber.getNextPosition(block, jetMoveDirection)?.also { block = it }

                val downMoveDirection = Point(0, -1)
                if (chamber.getNextPosition(block, downMoveDirection)?.also { block = it } == null) {
                    canMoveDown = false
                }
            }

            chamber.lockBlock(block)

            if (jetIndexBeforeRound < prevJetIndex) {
                val grow = topBeforeRound - prevTop
                prevTop = topBeforeRound

                val pattern = Pattern(currentSequence.toList(), grow)
                val patternIndex = patterns.indexOf(pattern)
                if (patternIndex != -1) {
                    // pattern detected
                    val initialPatterns = patterns.slice(0 until patternIndex)
                    val (indexRound, indexGrow) = initialPatterns.sumOf { it.sequence.size } to initialPatterns.sumOf { it.grow }
                    val cyclePatterns = patterns.slice(patternIndex until patterns.size)
                    val (cycleRound, cycleGrow) = cyclePatterns.sumOf { it.sequence.size } to cyclePatterns.sumOf { it.grow }
                    return Cycle(indexRound, indexGrow, cycleRound, cycleGrow, cyclePatterns)
                }
                patterns.add(pattern)
                currentSequence.clear()
            }
            currentSequence.add(ChangePerBlock(jetIndexBeforeRound, chamber.getTopLevel() - topBeforeRound))

            round = round.plus(BigInteger.ONE)
            prevJetIndex = jetIndexBeforeRound
            blockIndex = blockIndex nextOf blockTemplates.size

        }
        throw RuntimeException("Cannot detect cycle")
    }

    private fun detectTopUsingCycles(
        chamber: Chamber,
        jets: List<Int>,
        totalRounds: BigInteger,
        cycleDetectionMaxRound: Int
    ): BigInteger {
        val cycle = simulateAndDetectCycle(chamber, jets, rounds = cycleDetectionMaxRound.toBigInteger())

        check(cycle.cycleRound != 0)

        var totalGrow = BigInteger.ZERO
        var rounds = totalRounds

        if (cycle.indexRound > 0) {
            totalGrow += cycle.indexGrow.toBigInteger()
            rounds -= cycle.indexRound.toBigInteger()
        }
        val cycles = rounds.div(cycle.cycleRound.toBigInteger())
        val cyclesReminder = rounds.remainder(cycle.cycleRound.toBigInteger()).toInt()
        if (cycles > BigInteger.ONE) {
            rounds -= cycles * cycle.cycleRound.toBigInteger()
            totalGrow += cycles * cycle.cycleGrow.toBigInteger()
        }
        if (cyclesReminder > 0) {
            for (index in 0..cyclesReminder) {
                totalGrow += cycle.actualSequence.flatMap { it.sequence }.take(rounds.toInt()).sumOf { it.grow }
                    .toBigInteger()
                rounds = BigInteger.ZERO
            }
        } else {
            check(rounds == BigInteger.ZERO)
        }
        return totalGrow.add(BigInteger.ONE)
    }

    fun part1(input: List<String>): BigInteger {
        val jets = input.parseJets()
        return simulate(Chamber(), jets, rounds = "2022".toBigInteger())
    }

    fun part2(input: List<String>): BigInteger {
        val jets = input.parseJets()
        return detectTopUsingCycles(Chamber(), jets, totalRounds = "1000000000000".toBigInteger(), cycleDetectionMaxRound = 50_000)
    }

}

fun main() {
    val testInput1 = readInput("Day${DAY}_test")
    val day = Day17()
    check(day.part1(testInput1).also { println(it) } == "3068".toBigInteger()) { "Part1 Test - expected value doesn't match" }
    check(day.part2(testInput1).also { println(it) } == "1514285714288".toBigInteger()) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day${DAY}")
    println(day.part1(input))
    println(day.part2(input))
}
