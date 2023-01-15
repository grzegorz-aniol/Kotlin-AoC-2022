import java.lang.StringBuilder
import java.math.BigInteger

class Day25 {

    private fun String.fromQuintet(): BigInteger {
        var base = BigInteger.ONE
        var sum = BigInteger.ZERO
        for (ch in reversed()) {
            val digit = when (ch) {
                '2' -> 2
                '1' -> 1
                '0' -> 0
                '-' -> -1
                else -> -2
            }
            sum += BigInteger.valueOf(digit.toLong()) * base
            base *= BigInteger.valueOf(5)
        }
        return sum
    }

    private fun BigInteger.toQuintet(): String {
        val val5 = BigInteger.valueOf(5)
        val val2 = BigInteger.valueOf(2)
        var value = this
        var overflow = BigInteger.ZERO
        val output = StringBuilder()
        while (value != BigInteger.ZERO) {
            val digit = value.mod(val5)
            val ch = when (digit.toInt()) {
                0 -> '0'
                1 -> '1'
                2 -> '2'
                3 -> '='
                else -> '-'
            }
            output.append(ch)

            overflow = if (digit > val2) {
                BigInteger.ONE
            } else {
                BigInteger.ZERO
            }
            value = (value / val5) + overflow
        }
        return output.reversed().toString()
    }

    fun part1(input: List<String>): String {
        val sumBase10 = input.sumOf { it.fromQuintet() }
        return sumBase10.toQuintet()
    }

    fun part2(input: List<String>): String {
        return ""
    }

}

fun main() {
    val dayClass = Day25::class.java.simpleName
    val testInput1 = readInput("${dayClass}_test")
    val day = Day25()
    check(day.part1(testInput1).also { println(it) } == "2=-1=0") { "Part1 Test - expected value doesn't match" }
//    check(day.part2(testInput1).also { println(it) } == "xx") { "Part2 Test - expected value doesn't match" }

    val input = readInput(dayClass)
    println(day.part1(input))
//    println(day.part2(input))
}
