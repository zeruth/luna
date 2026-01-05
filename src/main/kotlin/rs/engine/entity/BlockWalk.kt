package rs.engine.entity

enum class BlockWalk {
    NONE,
    NPC,
    ALL;

    companion object {
        fun of(id: Int) = values()
            .getOrNull(id) ?: throw IndexOutOfBoundsException("Invalid HuntVis id: $id")
    }
}