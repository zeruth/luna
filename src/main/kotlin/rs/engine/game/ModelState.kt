package rs.engine.game

enum class ModalState(val mask: Int) {
    NONE(0x0),
    MAIN(0x1),
    CHAT(0x2),
    SIDE(0x4),
    TUT(0x8);
}
