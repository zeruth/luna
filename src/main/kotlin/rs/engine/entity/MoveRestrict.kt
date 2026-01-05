package rs.engine.entity

enum class MoveRestrict {
    NORMAL,
    BLOCKED,
    BLOCKED_NORMAL,
    INDOORS,
    OUTDOORS,
    NOMOVE,
    PASSTHRU;

    companion object {
        fun of(id: Int) = values()
            .getOrNull(id) ?: throw IndexOutOfBoundsException("Invalid HuntVis id: $id")
    }
}