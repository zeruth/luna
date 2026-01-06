package rs.net.msg.out

import io.luna.game.model.mob.Player
import rs.io.Packet
import rs.net.msg.out.game.ServerGameMessage
import rs.net.msg.out.game.ServerGameProt.Companion.VARP_SMALL
import rs.net.msg.out.game.ServerGameProtPriority.IMMEDIATE

class VarpSmall(
    private val varp: Int,
    private val value: Int,
    player: Player
) : ServerGameMessage(VARP_SMALL, IMMEDIATE, player) {
    override fun encode(buf: Packet) {
        buf.p2_alt2(varp)
        buf.p1_alt3(value)
    }
}