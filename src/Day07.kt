fun main() {

    data class Context(
        var totalSize: Long = 0L,
        var totalFilteredSize: Long = 0L,
        val maxSizeThreshold: Long = -1L,
        val dirSizes: MutableList<Long> = mutableListOf()
    ) {
        operator fun plusAssign(other: Context) {
            this.totalSize += other.totalSize
            this.totalFilteredSize = other.totalFilteredSize
        }

        fun summarize(): Context {
            dirSizes += totalSize
            return this.copy(totalFilteredSize = totalFilteredSize + (totalSize.takeIf { it <= maxSizeThreshold } ?: 0L))
        }

        fun closestMatch(spaceRequired: Long) =
            dirSizes.filter { it >= spaceRequired }.minBy { it - spaceRequired }
    }

    fun analyzeLog(input: Iterator<String>, parentCtx: Context): Context {
        val ctx = parentCtx.copy(totalSize = 0L)
        while (input.hasNext()) {
            val line = input.next()
            when {
                line == "$ cd /" -> Unit
                line == "$ ls" -> Unit
                line == "$ cd .." -> break
                line.startsWith("$ cd ") -> ctx += analyzeLog(input, ctx)
                line.startsWith("dir ") -> Unit
                else -> ctx.totalSize += line.split(" ")[0].toInt()
            }
        }
        return ctx.summarize()
    }

    fun part1(input: List<String>): Long {
        return analyzeLog(input.iterator(), Context(maxSizeThreshold = 100000)).totalFilteredSize
    }

    fun part2(input: List<String>): Long {
        val totalDiskSize = 70000000L
        val requiredSpace = 30000000L
        val ctx = analyzeLog(input.iterator(), Context())
        return ctx.closestMatch(spaceRequired = (requiredSpace - (totalDiskSize - ctx.totalSize)).coerceAtLeast(0L))
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437L) { "Part1 Test - expected value doesn't match" }
    check(part2(testInput) == 24933642L) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
