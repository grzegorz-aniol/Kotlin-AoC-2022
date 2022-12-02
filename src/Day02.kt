fun main() {

    fun getMyScore(ch: Char): Int = ch - 'A' + 1

    fun getRoundResult(result: String): Int = when (result) {
        "A B", "B C", "C A" -> 6
        "A C", "B A", "C B" -> 0
        else -> 3
    }

    fun calculateScore(input: List<String>, codeToMoveConverter: (Char, Char) -> Char): Int {
        return input.sumOf {
            val opponentMove = it[0]
            val secretCode = it[2]
            val myMove = codeToMoveConverter(opponentMove, secretCode)
            getRoundResult("$opponentMove $myMove") + getMyScore(myMove)
        }
    }

    fun part1(input: List<String>): Int {
        return calculateScore(input) { _, secretCode ->
            when (secretCode) {
                'X' -> 'A'
                'Y' -> 'B'
                else -> 'C'
            }
        }
    }

    fun part2(input: List<String>): Int {
        return calculateScore(input) { opponentMove, secretCode ->
            val diff = secretCode - 'Y' // -1 lose, 0 draw, 1 win
            val opponentValue = opponentMove - 'A' // 0 rock, 1 paper, 3 scissors
            val expectedMoveValue = (opponentValue + diff) % 3
            'A' + expectedMoveValue;
        }
    }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 12)

    val input = readInput("Day02")
    println(part1(input)) // 9759
    println(part2(input)) // 12429
}
