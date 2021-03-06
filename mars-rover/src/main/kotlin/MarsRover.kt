import java.lang.Exception

data class Rover(
    var position: Pair<Int, Int> = Pair(0, 0),
    var direction: Direction,
    var planet: Planet
) {
    companion object {
        const val ABORT_WHEN_OBSTACLE_FOUND = "obstacle found"
    }

    fun move(commands: List<Command>) {
        commands.forEach { command ->
            when (command) {
                Command.L -> {
                    direction = Direction.values()[(direction.ordinal - 1).myMod(Direction.values().size)]
                }
                Command.R -> {
                    direction = Direction.values()[(direction.ordinal + 1).myMod(Direction.values().size)]
                }
                Command.F -> {
                    position = if (direction == Direction.N || direction == Direction.S) {
                        Pair(position.first + direction.factor, position.second)
                    } else {
                        Pair(position.first, position.second + direction.factor)
                    }
                }
                Command.B -> {
                    position = if (direction == Direction.N || direction == Direction.S) {
                        Pair(position.first - direction.factor, position.second)
                    } else {
                        Pair(position.first, position.second - direction.factor)
                    }
                }
            }

            position = Pair(position.first.myMod(planet.height), position.second.myMod(planet.width))

            if (planet.grid[position.first][position.second]) {
                position = planet.lastSafePoint ?: position
                throw Exception(ABORT_WHEN_OBSTACLE_FOUND)
            }
        }
    }

    fun isInPosition(aPosition: Pair<Int, Int>): Boolean {
        return this.position.first == aPosition.first && this.position.second == aPosition.second
    }

    fun isPointingTo(aDirection: Direction): Boolean {
        return this.direction == aDirection
    }
}

data class Planet(val grid: List<List<Boolean>>) {
    val height get() = grid.size

    val width get() = if (grid.isNotEmpty()) grid.first().size else 0

    val lastSafePoint: Pair<Int, Int>? by lazy {
        for (i in 0 until height) {
            for (j in 0 until width) {
                if (!grid[height - 1 - i][width - 1 - j]) return@lazy Pair(height - 1 - i, width - 1 - j)
            }
        }
        return@lazy null
    }
}

enum class Direction(val factor: Int) {
    N(-1), E(1), S(1), W(-1)
}

enum class Command {
    F, B, L, R
}

fun Int.myMod(m: Int): Int {
    return (this % m + m) % m
}

