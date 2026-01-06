package rs.net.msg.out.game

enum class ServerGameProtPriority {
    // counted as part of the buffer_full command
    // alternate names: LOW, CONTENT
    BUFFERED,

    // not counted as part of the buffer_full command
    // alternate names: HIGH, ESSENTIAL, ENGINE
    IMMEDIATE
}