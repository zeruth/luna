package rs.engine.entity.hunt

enum class HuntVis {
    OFF,
    LINEOFSIGHT,
    LINEOFWALK;

    companion object {
        fun of(id: Int) = HuntVis.values()
            .getOrNull(id) ?: throw IndexOutOfBoundsException("Invalid HuntVis id: $id")
    }
}