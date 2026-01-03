package rs.engine.entity.hunt

enum class HuntModeType {
    OFF,
    PLAYER,
    NPC,
    OBJ,
    SCENERY;

    companion object {
        fun of(id: Int) = values()
            .getOrNull(id) ?: throw IndexOutOfBoundsException("Invalid HuntModeType id: $id")
    }
}