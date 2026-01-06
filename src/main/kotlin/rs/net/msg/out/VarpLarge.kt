package rs.net.msg.out

import io.luna.game.model.mob.Player
import rs.io.Packet
import rs.net.msg.out.game.ServerGameMessage
import rs.net.msg.out.game.ServerGameProt.Companion.VARP_LARGE
import rs.net.msg.out.game.ServerGameProtPriority.IMMEDIATE

class VarpLarge(
    player: Player,
    private val varp: Int,
    private val value: Int,
) : ServerGameMessage(VARP_LARGE, IMMEDIATE, player) {
    override fun encode(buf: Packet) {
        buf.p4_alt3(value)
        buf.p2_alt1(varp)
    }
}