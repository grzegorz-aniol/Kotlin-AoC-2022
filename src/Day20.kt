import java.lang.Math.abs
import kotlin.math.absoluteValue
import kotlin.math.sign

class Day20 {

    private fun updatePosition(position: Int, change: Long, length: Int): Int {
        val result = when {
            change > 0 -> {
                if (position + change >= length) {
                    (position + change + 1) % length
                } else {
                    (position + change) % length
                }
            }
            change < 0 -> {
                val c = change.rem(length)
                if (position + c < 0) {
                    (position + (length - 2 + c)) % length
                } else {
                    (position + (length -1 + c)) % length
                }
            }
            else -> position
        }
        return result.toInt()
    }

    private fun updatePosition2(position: Int, change: Long, length: Int): Int {
        val result = when {
            change > 0 -> {
                (position + change) % length
            }
            change < 0 -> {
                val np = position + change
                if (np < 0) {
                    length + np
                } else {
                    np
                }
            }
            else -> position
        }
        return result.toInt()
    }


    private fun modulo(position: Int, change: Long, length: Int) : Int {
        val result = when {
            change > 0 -> (position + change) % length
            change < 0 -> (position.rem(length) + (length - 1 + change)) % length
            else -> position
        }
        return result.toInt()
    }

    private fun modulo2(position: Int, change: Long, length: Int) : Int {
        val result = when {
            change > 0 -> (position + change) % length
            change < 0 -> (position + length + change) % length
            else -> position
        }.toInt()
        check (result in 0 until length)
        return result
    }

    private fun reduceValue(value: Long, length: Int): Long =
        when {
            value > 0 -> value % (length - 1)
            value < 0 -> value % length
            else -> 0L
        }


    private data class Element(val originalIndex: Int, val value: Long, val reducedValue: Long)

    private fun mix(input: List<String>, multiplier: Long = 1, times: Int = 1): Long {
        val length = input.size
        val valuesInOriginalOrder = input.map(String::toLong).map { it * multiplier }.toLongArray()
        val sequence = valuesInOriginalOrder.withIndex().map { (index, value) -> Element(index, value, reduceValue(value, length)) }.toTypedArray()

        fun showSequence() {
            println(sequence.joinToString(separator = " ") { it.value.toString() })
        }

        check (sequence.count { it.value == 0L } == 1)

        repeat(times) {
            println("Mix #$it")
            for (index in valuesInOriginalOrder.indices) {
                val currentPosition = sequence.indexOfFirst { it.originalIndex == index }
                val currentElement = sequence[currentPosition]
                val value = currentElement.reducedValue
                if (value == 0L || value == length.toLong()) {
                    continue
                }

//                val direction = value.sign
//                var pos = currentPosition
//                repeat (value.absoluteValue.toInt()) {
//                    val nextPos = modulo2(pos, direction.toLong(), length)
//                    val element = sequence[pos]
//                    sequence[pos] = sequence[nextPos]
//                    sequence[nextPos] = element
//                    pos = nextPos
//                }

                val newPosition = updatePosition(currentPosition, value, length)
                check(newPosition in 0 until length)
                if (newPosition == currentPosition) {
                    continue
                }

                val delta = (newPosition - currentPosition).sign
                if (delta > 0) {
                    for (pos in currentPosition + 1 .. newPosition) {
                        val elementToMove = sequence[pos]
                        val nextPosition = pos - delta
                        check (nextPosition in 0 until length)
                        sequence[nextPosition] = elementToMove
                    }
                } else {
                    for (pos in currentPosition - 1 downTo newPosition) {
                        val elementToMove = sequence[pos]
                        val nextPosition = pos - delta
                        check (nextPosition in 0 until length)
                        sequence[nextPosition] = elementToMove
                    }
                }
                sequence[newPosition] = currentElement
            }
        }

        showSequence()

        val zeroIndex = sequence.indexOfFirst { it.value == 0L }
        return sequence[(zeroIndex + 1000).mod(length)].value +
                sequence[(zeroIndex + 2000).mod(length)].value +
                sequence[(zeroIndex + 3000).mod(length)].value

    }

    fun part1(input: List<String>): Long {
        return mix(input)
    }

    fun part2(input: List<String>): Long {
        return mix(input, multiplier = 811589153, times = 10)
    }

}

fun main() {
    val dayClass = Day20::class.java.simpleName
    val testInput1 = readInput("${dayClass}_test")
    val day = Day20()

    check(day.part1(testInput1).also { println("Part 1 test result: $it") } == 3L) { "Part1 Test - expected value doesn't match" }
    check(day.part2(testInput1).also { println("Part 2 test result: $it") } == 1623178306L) { "Part2 Test - expected value doesn't match" }

    val input = readInput(dayClass)
    println("Part 1 result: " + day.part1(input)) // 19559
//    println(day.part2(input))
}
