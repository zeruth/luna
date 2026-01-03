package rs.engine.entity.hunt

enum class HuntCheckNotTooStrong {
    OFF,
    OUTSIDE_WILDERNESS;

    companion object {
        fun of(id: Int) = HuntCheckNotTooStrong.values()
            .getOrNull(id) ?: throw IndexOutOfBoundsException("Invalid HuntCheckNotTooStrong id: $id")
    }
}