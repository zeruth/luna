package rs.engine.entity

enum class NpcStat {
    ATTACK,
    DEFENCE,
    STRENGTH,
    HITPOINTS,
    RANGED,
    MAGIC;

    companion object {
        fun of(id: Int) = values()
            .getOrNull(id) ?: throw IndexOutOfBoundsException("Invalid HuntVis id: $id")
    }
}