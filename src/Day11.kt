private const val DAY = "11"

data class Expression(val argument1: Long?, val argument2: Long?, val isAdd: Boolean) {
    fun calculateNextLevel(oldLevel: Long): Long {
        val v1 = argument1 ?: oldLevel
        val v2 = argument2 ?: oldLevel
        return if (isAdd) {
            v1 + v2
        } else {
            v1 * v2
        }
    }
}

data class DivisionTest(val divisor: Long, val nextOnTrue: Int, val nextOnFalse: Int) {
    fun calculateNextPass(v: Long) =
        if (v % divisor == 0L) {
            nextOnTrue
        } else {
            nextOnFalse
        }
}

data class Monkey(val index: Int,  val items: ArrayDeque<Long>, val worryLevel: Expression, val divisionTest: DivisionTest) {
    var inspectionCounter = 0

    constructor(index: Int, startingItems: List<Int>, worryLevel: Expression, divisionTest: DivisionTest) :
        this(index, ArrayDeque<Long>().also { it.addAll(startingItems.map(Int::toLong)) }, worryLevel, divisionTest)

    fun inspects(): Long? {
        val value = items.removeFirstOrNull() ?: return null
        ++inspectionCounter
        return worryLevel.calculateNextLevel(value)
    }

    fun printStatus() {
        println("Monkey $index -> " + items.joinToString(","))
    }
}

fun List<String>.toMonkeys(): List<Monkey>  {
    val iter = iterator()
    val result = mutableListOf<Monkey>()
    while (iter.hasNext()) {
        var line = iter.next()
        if (line.startsWith("Monkey")) {
            val items = iter.next().substringAfter(": ").split(",").map(String::trim).map(String::toInt).toList()
            val testCondition = iter.next().substringAfter(" = ").trim().split(" ")
            val arg1 = testCondition[0].toLongOrNull()
            val arg2 = testCondition[2].toLongOrNull()
            val isAdd = testCondition[1].contains("+")
            val divisor = iter.next().substringAfter("divisible by ").toLong()
            var nextForTrue = -1
            var nextForFalse = -1
            repeat(2) {
                line = iter.next()
                line.substringAfter(" to monkey ").toInt().also {
                    if (line.contains("If true:")) {
                        nextForTrue = it
                    } else {
                        nextForFalse = it
                    }
                }
            }
            result.add(Monkey(
                index = result.size,
                startingItems = items,
                worryLevel = Expression(arg1, arg2, isAdd),
                divisionTest = DivisionTest(divisor, nextForTrue, nextForFalse)
            ))
        }
    }
    return result
}

fun main() {

    fun runMonkeyBusiness(numOfRounds: Int, monkeys: List<Monkey>, reliefFun: (Long) -> Long): Long {
        println(monkeys)
        for (round in 1..numOfRounds) {
            println("Round $round")
            monkeys.forEach { monkey ->
                while (monkey.items.isNotEmpty()) {
                    monkey.inspects()?.let { value ->
                        val reliefValue = reliefFun(value)
                        val nextMonkey = monkey.divisionTest.calculateNextPass(reliefValue)
                        monkeys[nextMonkey].items.addLast(reliefValue)
                    }
                }
                println("Turn ${monkey.index}")
                monkeys.forEach(Monkey::printStatus)
            }
            monkeys.forEach(Monkey::printStatus)
            println("---------------")
        }
        return monkeys.map { it.inspectionCounter }.sortedDescending().take(2).fold(1L) { acc, v -> acc * v }
    }

    fun part1(input: List<String>): Long {
        val monkeys = input.toMonkeys()
        return runMonkeyBusiness(numOfRounds = 20, monkeys, reliefFun = { it / 3 })
    }

    fun part2(input: List<String>): Long {
        val monkeys = input.toMonkeys()
        val coeff = monkeys.map { it.divisionTest.divisor }.fold(1L) { acc, v -> acc * v }
        return runMonkeyBusiness(numOfRounds = 10_000, monkeys, reliefFun = { it % coeff })
    }

    val testInput1 = readInput("Day${DAY}_test")
    check(part1(testInput1).also { println(it) } == 10605L) { "Part1 Test - expected value doesn't match" }
    check(part2(testInput1).also { println(it) } == 2713310158L) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day${DAY}")
    println(part1(input))
    println(part2(input))
}
