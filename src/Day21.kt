import java.math.BigDecimal

class Day21 {

    enum class Operator { PLUS, MINUS, MULTIPLE, DIVIDE }
    sealed interface Entry {
        val name: String
    }

    data class Operation(override val name: String, val operator: Operator, val left: String, val right: String) : Entry
    data class Value(override val name: String, val value: BigDecimal) : Entry

    private fun Operator.reverse() = when (this) {
        Operator.PLUS -> Operator.MINUS
        Operator.MINUS -> Operator.PLUS
        Operator.MULTIPLE -> Operator.DIVIDE
        else -> Operator.MULTIPLE
    }

    private fun parse(input: List<String>): Map<String, Entry> {
        val numRegexp = Regex("\\d+")
        return input.associate { line ->
            val items = line.split(": ")
            val name = items[0]
            name to if (items[1].contains(numRegexp)) {
                Value(name, items[1].toBigDecimal())
            } else {
                val args = items[1].split(" ").map { it.trim() }
                val op = when (args[1]) {
                    "+" -> Operator.PLUS
                    "-" -> Operator.MINUS
                    "*" -> Operator.MULTIPLE
                    else -> Operator.DIVIDE
                }
                Operation(name, op, args[0], args[2])
            }
        }
    }

    private fun evaluate(expressions: Map<String, Entry>, keyValue: String): BigDecimal? {
        val exprMap = expressions.toMutableMap()
        while (exprMap[keyValue] !is Value) {
            var foundNewValues = 0
            exprMap.values.filterIsInstance<Operation>()
                .forEach { operation ->
                    val leftValue = (exprMap[operation.left] as? Value)?.value
                    val rightValue = (exprMap[operation.right] as? Value)?.value
                    if (leftValue != null && rightValue != null) {
                        val value = when (operation.operator) {
                            Operator.PLUS -> leftValue + rightValue
                            Operator.MINUS -> leftValue - rightValue
                            Operator.MULTIPLE -> leftValue * rightValue
                            else -> leftValue / rightValue
                        }
                        exprMap[operation.name] = Value(operation.name, value)
                        ++foundNewValues
                    }
                }
            if (foundNewValues == 0) {
                return null
            }
        }
        return (exprMap[keyValue] as Value).value
    }

    fun part1(input: List<String>): BigDecimal? {
        val expressions = parse(input).toMutableMap()
        return evaluate(expressions, keyValue = "root")
    }

    fun part2(input: List<String>): BigDecimal? {
        val expressions = parse(input)
        val newExpressions = expressions.toMutableMap()

        fun reverseExpression(forKey: String, newRootKey: String) {
            if (forKey != newRootKey && newExpressions[forKey] is Value) {
                return
            }
            expressions.values.filterIsInstance<Operation>()
                .forEach { operation ->
                    if (operation.left == forKey) {
                        newExpressions[forKey] =
                            Operation(forKey, operation.operator.reverse(), operation.name, operation.right)
                        reverseExpression(operation.name, newRootKey)
                    } else if (operation.right == forKey) {
                        val newOperation = if (operation.operator == Operator.PLUS || operation.operator == Operator.MULTIPLE) {
                            Operation(forKey, operation.operator.reverse(), operation.name, operation.left)
                        } else {
                            Operation(forKey, operation.operator, operation.left, operation.name)
                        }
                        newExpressions[forKey] = newOperation
                        reverseExpression(operation.name, newRootKey)
                    }
                }
        }

        val keyToFind = "humn"
        val knownKey = "root"
        check(expressions.values.filterIsInstance<Value>().count { it.name == keyToFind } == 1)
        check(
            expressions.values.filterIsInstance<Operation>()
                .count { it.left == keyToFind || it.right == keyToFind } == 1)

        val root = expressions[knownKey] as Operation

        val expressionsWithoutKeyToFind = expressions.toMutableMap().also { it.remove(keyToFind) }
        val leftValue = evaluate(expressionsWithoutKeyToFind, root.left)
        val rightValue = evaluate(expressionsWithoutKeyToFind, root.right)
        check (leftValue == null || rightValue == null)
        val equalValue = listOfNotNull(leftValue, rightValue).first()

        newExpressions.remove(knownKey)
        newExpressions.remove(keyToFind)
        newExpressions[root.left] = Value(root.left, equalValue)
        newExpressions[root.right] = Value(root.right, equalValue)
        reverseExpression(forKey = keyToFind, newRootKey = keyToFind)

        return evaluate(newExpressions, keyToFind)
    }

}

fun main() {
    val dayClass = Day21::class.java.simpleName
    val testInput1 = readInput("${dayClass}_test")
    val day = Day21()
    check(day.part1(testInput1).also { println(it) } == BigDecimal(152)) { "Part1 Test - expected value doesn't match" }
    check(day.part2(testInput1).also { println(it) } == BigDecimal(301)) { "Part2 Test - expected value doesn't match" }

    val input = readInput(dayClass)
    println(day.part1(input))
    println(day.part2(input))
}
