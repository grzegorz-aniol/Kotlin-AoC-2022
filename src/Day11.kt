private const val DAY = 11

class Day11(private val debug: Boolean = false) {

    fun part1(input: List<String>): Long {
        val monkeys = input.toMonkeys()
        return runMonkeyBusiness(numOfRounds = 20, monkeys, reliefFun = { it / 3 })
    }

    fun part2(input: List<String>): Long {
        val monkeys = input.toMonkeys()
        val product = monkeys.map { it.divisionRule.divisor }.fold(1L) { acc, v -> acc * v }
        return runMonkeyBusiness(numOfRounds = 10_000, monkeys, reliefFun = { it % product })
    }

    private fun runMonkeyBusiness(numOfRounds: Int, monkeys: List<Monkey>, reliefFun: (Long) -> Long): Long {
        if (debug) println(monkeys)
        for (round in 1..numOfRounds) {
            if (debug) println("Round $round")
            monkeys.forEach { monkey ->
                while (monkey.items.isNotEmpty()) {
                    monkey.inspect()?.let { value ->
                        val reliefValue = reliefFun(value)
                        val nextMonkey = monkey.divisionRule.calculateNextPass(reliefValue)
                        monkeys[nextMonkey].items.addLast(reliefValue)
                    }
                }
            }
            if (debug) {
                monkeys.forEach(Monkey::printStatus)
                println("---------------")
            }
        }
        return monkeys.map { it.inspectionCounter }.sortedDescending().take(2).fold(1L) { acc, v -> acc * v }
    }

    private data class Expression(val argument1: Long?, val argument2: Long?, val isSum: Boolean) {
        fun calculateNextLevel(oldLevel: Long): Long {
            val v1 = argument1 ?: oldLevel
            val v2 = argument2 ?: oldLevel
            return if (isSum) {
                v1 + v2
            } else {
                v1 * v2
            }
        }
    }

    private data class DivisionRule(val divisor: Long, val nextMonkeyOnTrue: Int, val nextMonkeyOnFalse: Int) {
        fun calculateNextPass(v: Long) =
            if (v % divisor == 0L) {
                nextMonkeyOnTrue
            } else {
                nextMonkeyOnFalse
            }
    }

    private data class Monkey(val index: Int, val items: ArrayDeque<Long>, val levelChangeExpression: Expression, val divisionRule: DivisionRule) {
        var inspectionCounter = 0

        constructor(index: Int, startingItems: List<Int>, levelChangeExpression: Expression, divisionRule: DivisionRule) :
                this(index, ArrayDeque<Long>().also { it.addAll(startingItems.map(Int::toLong)) }, levelChangeExpression, divisionRule)

        fun inspect(): Long? {
            val level = items.removeFirstOrNull() ?: return null
            ++inspectionCounter
            return levelChangeExpression.calculateNextLevel(level)
        }

        fun printStatus() {
            println("Monkey $index -> " + items.joinToString(","))
        }
    }

    private fun List<String>.toMonkeys(): List<Monkey>  {
        val numberRegexp = Regex("\\d+")
        return sequence {
            val iter = iterator()
            var index = 0
            while (iter.hasNext()) {
                if (iter.next().startsWith("Monkey")) {
                    val items = numberRegexp.findAll(iter.next()).map { it.value.toInt() }.toList()
                    val expr = Regex("= (\\w+) ([*+]) (\\w+)").find(iter.next())?.groupValues?.let {
                        Expression(it[1].toLongOrNull(), it[3].toLongOrNull(), it[2].contains("+"))
                    }!!
                    val divisor = numberRegexp.find(iter.next())?.value?.toLong()!!
                    val onTrue = numberRegexp.find(iter.next())?.value?.toInt()!!
                    val onFalse = numberRegexp.find(iter.next())?.value?.toInt()!!
                    yield(Monkey(
                        index = index++,
                        startingItems = items,
                        levelChangeExpression = expr,
                        divisionRule = DivisionRule(divisor, onTrue, onFalse)
                    ))
                }
            }
        }.toList()
    }

}

fun main() {
    val testInput1 = readInput("Day${DAY}_test")
    val day = Day11()
    check(day.part1(testInput1).also { println(it) } == 10605L) { "Part1 Test - expected value doesn't match" }
    check(day.part2(testInput1).also { println(it) } == 2713310158L) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day${DAY}")
    println(day.part1(input))
    println(day.part2(input))
}
