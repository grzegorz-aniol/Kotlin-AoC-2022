import java.rmi.UnexpectedException
import kotlin.math.max

private const val DAY = 16

private class Day16 {
    data class Edge(val fromNode: String, val toNode: String)
    data class Node(val valve: String, var rate: Int, val edges: MutableList<Edge> = mutableListOf())

    companion object {
        private fun getId(valve1: String, valve2: String) = "${valve1}_${valve2}"
    }

    class Graph(
        val nodes: MutableMap<String, Node> = mutableMapOf(),
        val startingValve: String,
    ) {
        val distanceMap: MutableMap<String, Int> = mutableMapOf()
        fun getNode(valve: String): Node = nodes[valve] ?: throw UnexpectedException("Missing node $valve")

        fun findShortestPaths(fromValve: String, toValve: String): Int? {
            val visited = mutableSetOf<String>()
            var shortestDistance: Int? = null

            fun deepFirstSearch(node: Node, distance: Int) {
                if (node.valve == toValve) {
                    shortestDistance = shortestDistance?.let { Math.min(it, distance) } ?: distance
                    return
                }
                visited.add(node.valve)
                node.edges
                    .filterNot { visited.contains(it.toNode) }
                    .forEach { deepFirstSearch(getNode(it.toNode), distance + 1) }
                visited.remove(node.valve)
            }

            deepFirstSearch(getNode(fromValve), distance = 0)
            return shortestDistance
        }

        fun generateDistancesMap() {
            val valvesList = nodes.values.map { it.valve }.toTypedArray()
            for (fromIdx in valvesList.indices) {
                for (toIdx in valvesList.indices) {
                    if (fromIdx < toIdx) {
                        val startValve = valvesList[fromIdx]
                        val endValve = valvesList[toIdx]
                        findShortestPaths(startValve, endValve)?.let { distance ->
                            distanceMap[getId(startValve, endValve)] = distance
                            distanceMap[getId(endValve, startValve)] = distance
                        }
                    }
                }
            }
        }

        fun getDistanceBetween(valve1: String, valve2: String): Int? = when (valve1) {
            valve2 -> 0
            else -> distanceMap[getId(valve1, valve2)]
        }

        fun showGraph() {
            nodes.values.withIndex().forEach { (index, node) ->
                val valve = node.valve
                println("MERGE ($valve:Valve {name: '$valve', rate: ${node.rate}})")
            }
            nodes.values.withIndex().forEach { (index, node) ->
                node.edges.forEach { rel ->
                    val nodeFrom = node.valve
                    val nodeTo = rel.toNode
                    println("MERGE ($nodeFrom)-[:CONNECTED_WITH]-($nodeTo)")
                }
            }
            nodes.values.forEach { nodeFrom ->
                val line = nodes.values.joinToString { nodeTo ->
                    if (nodeFrom.valve != nodeTo.valve) {
                        String.format("%3d", distanceMap[getId(nodeFrom.valve, nodeTo.valve)])
                    } else {
                        "   "
                    }
                }
                println("${nodeFrom.valve} $line")
            }
        }
    }

    data class Change(val from: String, val to: String, val distance: Int, val rate: Int, val openedAtTime: Int)

    data class Solution(
        val maxTime: Int = 30,
        val node: Node,
        val time: Int = 1,
        val pressure: Int = 0,
        val closedValves: Set<String> = setOf(),
        val workDone: List<Change> = listOf(),
    )

    fun part1(input: List<String>): Int {
        val graph = input.parse()
        val maxTime = 30
        graph.generateDistancesMap()

        val queue = ArrayDeque<Solution>()
        queue.add(
            Solution(
                time = 1,
                maxTime = maxTime,
                node = graph.getNode(graph.startingValve),
                closedValves = graph.nodes.filter { it.value.rate > 0 }.map { it.key }.toSet(),
            )
        )
        var theBestSolution = Int.MIN_VALUE

        while (queue.isNotEmpty()) {
            val solution = queue.removeLast()

            val newSolutions by lazy {
                solution.closedValves.mapNotNull { nextValve ->
                    val nextNode = graph.getNode(nextValve)
                    val distance = graph.getDistanceBetween(solution.node.valve, nextValve) ?: maxTime
                    val newTime = solution.time + distance + 1
                    if (nextNode.rate > 0 && newTime < maxTime) {
                        solution.copy(
                            node = nextNode,
                            time = newTime,
                            pressure = solution.pressure + nextNode.rate * Math.max(0, maxTime - newTime + 1),
                            closedValves = solution.closedValves - nextValve,
                            workDone = solution.workDone + Change(
                                from = solution.node.valve,
                                to = nextValve,
                                distance = distance,
                                rate = nextNode.rate,
                                openedAtTime = newTime,
                            )
                        )
                    } else {
                        null
                    }
                }.sortedBy { it.workDone.last().rate }
            }

            if (solution.time >= maxTime || solution.closedValves.isEmpty() || newSolutions.isEmpty()) {
                val result = solution.pressure
                if (result > theBestSolution) {
                    theBestSolution = result
                    var pressureCheck = 0
                    val description = solution.workDone.joinToString(separator = ", ") {
                        val node = graph.getNode(it.to)
                        val flow = node.rate * (maxTime - it.openedAtTime + 1)
                        pressureCheck += flow
                        "\n\tMoved ${it.distance}, opened ${it.to} at ${it.openedAtTime}, with rate: ${node.rate}, flow: $flow"
                    }
                    check (pressureCheck == result)
                    println("New solution: $theBestSolution => $description")
                }
            }

            queue.addAll(newSolutions)
        }

        return theBestSolution
    }

    fun part2(input: List<String>) = 0

    private fun List<String>.parse(): Graph {
        val nodes: MutableMap<String, Node> = mutableMapOf()
//        val edges: MutableMap<String, Edge> = mutableMapOf()

        fun getOrCreateNode(valve: String, rate: Int? = null): Node {
            val node = nodes.computeIfAbsent(valve) { Node(valve, rate ?: 0) }
            rate?.let { node.rate = it }
            return node
        }

        fun addEdge(fromNode: Node, toNode: Node) {
            val edge = Edge(fromNode.valve, toNode.valve)
//            edges.put(getId(fromNode.valve, toNode.valve), edge)
            fromNode.edges.add(edge)
        }

        var startingValve: String? = null
        forEach {
            val row = it.split(" ", ",", ";", "=").filterNot { it.isBlank() }
            val valve = row[1]
            val rate = row[5].toInt()
            if (startingValve == null) {
                startingValve = valve
            }
            val fromNode = getOrCreateNode(valve, rate)
            row.drop(10).forEach { nextValve ->
                val toNode = getOrCreateNode(nextValve, null)
                addEdge(fromNode, toNode)
            }
        }
        return Graph(nodes, startingValve!!)
    }
}

fun main() {
    val testInput1 = readInput("Day${DAY}_test")
    val day = Day16()
    check(day.part1(testInput1).also { println("Part 1 test result: $it") } == 1651) { "Part1 Test - expected value doesn't match" }
//    check(day.part2(testInput1).also { println("Part 2 test result: $it") } == -1) { "Part2 Test - expected value doesn't match" }

    val input = readInput("Day${DAY}")
    // 3709 wrong answer, 3629 wrong answer, 1804 wrong answer, 1795 wrong, 1804 wrong // 2909??
    println("Part 1 result: " + day.part1(input)) // good answer: 1792
//    println("Part 2 result: " + day.part2(input))
}
