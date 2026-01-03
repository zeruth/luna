package rs.engine.entity.hunt

enum class HuntNobodyNear {
    KEEPHUNTING,
    PAUSEHUNT;

    companion object {
        fun of(id: Int) = HuntNobodyNear.values()
            .getOrNull(id) ?: throw IndexOutOfBoundsException("Invalid HuntNobodyNear id: $id")
    }
}