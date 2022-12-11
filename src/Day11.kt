private const val DAY = "11"

data class Expression(val argument1: Int?, val argument2: Int?, val isAdd: Boolean) {
    fun calculateNextLevel(oldLevel: Int): Int {
        val v1 = argument1 ?: oldLevel
        val v2 = argument2 ?: oldLevel
        return if (isAdd) {
            v1 + v2
        } else {
            v1 * v2
        }
    }
}

data class DivisionTest(val divisor: Int, val nextOnTrue: Int, val nextOnFalse: Int) {
    fun calculateNextPass(v: Int) =
        if (v % divisor == 0) {
            nextOnTrue
        } else {
            nextOnFalse
        }
}

data class Monkey(val index: Int,  val items: ArrayDeque<Int>, val worryLevel: Expression, val divisionTest: DivisionTest) {
    var inspectionCounter = 0

    constructor(index: Int, startingItems: List<Int>, worryLevel: Expression, divisionTest: DivisionTest) :
        this(index, ArrayDeque<Int>().also { it.addAll(startingItems) }, worryLevel, divisionTest)

    fun inspects(): Int? {
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
            val arg1 = testCondition[0].toIntOrNull()
            val arg2 = testCondition[2].toIntOrNull()
            val isAdd = testCondition[1].contains("+")
            val divisor = iter.next().substringAfter("divisible by ").toInt()
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

    fun part1(input: List<String>): Int {
        val monkeys = input.toMonkeys()
        println(monkeys)
        for (round in 1..20) {
            println("Round $round")
            monkeys.forEach { monkey ->
                while (monkey.items.isNotEmpty()) {
                    monkey.inspects()?.let { value ->
                        val reliefValue = value / 3
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
        return monkeys.map { it.inspectionCounter }.sortedDescending().take(2).fold(1) { acc, v -> acc * v }
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    val testInput1 = readInput("Day${DAY}_test")
    check(part1(testInput1).also { println(it) } == 10605) { "Part1 Test - expected value doesn't match" }
//    check(part2(testInput1).also { println(it) } == 0) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day${DAY}")
    println(part1(input))
//    println(part2(input))
}
