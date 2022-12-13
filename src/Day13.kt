private const val DAY = 13

class Day13 {

    sealed interface Node
    data class NodeValue(val value: Int) : Node
    data class ListValue(val items: MutableList<Node> = mutableListOf()) : Node {
        constructor(node: Node) : this(mutableListOf(node))
    }

    fun part1(input: List<String>): Int {
        return input.toPairValues().withIndex()
            .filter { true == isRightOrder(it.value.first, it.value.second) }
            .sumOf { it.index + 1 }
    }

    fun part2(input: List<String>): Int {
        val dividerPackets = listOf(
            ListValue(ListValue(NodeValue(2))),
            ListValue(ListValue(NodeValue(6)))
        )
        val inputList = input.toValues()
        val sorted = inputList.plus(dividerPackets)
            .sortedWith { left, right ->
                when (isRightOrder(left, right)) {
                    false -> 1
                    true -> -1
                    else -> 0
                }
            }
        return sorted.withIndex().filter { item ->
            item.value == dividerPackets[0] || item.value == dividerPackets[1]
        }.fold(1) { acc, item -> acc * (item.index + 1) }
    }

    fun parse(input: CharIterator): Node {
        val list = ListValue()
        var prevValue: Node? = null
        var numberText = ""
        while (input.hasNext()) {
            when (val ch = input.next()) {
                '[' -> prevValue = parse(input)
                ']', ',' -> {
                    when {
                        numberText.isNotBlank() -> NodeValue(numberText.toInt())
                        else -> prevValue
                    }?.let {
                        list.items.add(it)
                    }
                    numberText = ""
                    prevValue = null
                    if (ch == ']') {
                        return list
                    }
                }
                in '0'..'9' -> numberText += ch
                else -> Unit
            }
        }
        return prevValue ?: list
    }

    private fun List<String>.toPairValues(): List<Pair<ListValue, ListValue>> {
        return sequence {
            val iter = iterator()
            while (iter.hasNext()) {
                yield(parse(iter.next().iterator()) as ListValue to parse(iter.next().iterator()) as ListValue)
                if (iter.hasNext()) {
                    iter.next()
                }
            }
        }.toList()
    }

    private fun List<String>.toValues(): List<ListValue> {
        return sequence {
            val iter = iterator()
            while (iter.hasNext()) {
                val line = iter.next()
                if (line.isNotBlank()) {
                    yield(parse(line.iterator()) as ListValue)
                }
            }
        }.toList()
    }

    private fun isRightOrder(left: Node, right: Node): Boolean? {
        return when {
            left is NodeValue && right is NodeValue -> when {
                left.value < right.value -> true
                left.value > right.value -> false
                else -> null
            }

            left is ListValue && right is ListValue -> {
                val iterLeft = left.items.iterator()
                val iterRight = right.items.iterator()
                while (iterLeft.hasNext() && iterRight.hasNext()) {
                    val result = isRightOrder(iterLeft.next(), iterRight.next())
                    if (result != null) {
                        return result
                    }
                }
                when {
                    !iterLeft.hasNext() && iterRight.hasNext() -> return true
                    iterLeft.hasNext() && !iterRight.hasNext() -> return false
                    else -> null
                }
            }

            left is NodeValue -> isRightOrder(ListValue(mutableListOf(left)), right)

            right is NodeValue -> isRightOrder(left, ListValue(mutableListOf(right)))

            else -> null
        }
    }
}

fun main() {
    val testInput1 = readInput("Day${DAY}_test")
    val day = Day13()
    check(day.part1(testInput1).also { println(it) } == 13) { "Part1 Test - expected value doesn't match" }
    check(day.part2(testInput1).also { println(it) } == 140) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day${DAY}")
    println(day.part1(input))
    println(day.part2(input))
}
