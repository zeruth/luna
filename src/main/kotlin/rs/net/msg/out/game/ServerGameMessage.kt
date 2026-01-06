package rs.net.msg.out.game

import io.luna.game.model.mob.Player
import io.netty.buffer.ByteBuf
import rs.io.Packet

abstract class ServerGameMessage(val prot: ServerGameProt, val priority: ServerGameProtPriority, val player: Player) {
    abstract fun encode(buf: Packet)

    companion object {
        fun ServerGameMessage.write(
            out: ByteBuf?
        ) {
            val client = player.client ?: return
            val buf = client.out

            buf.position(0)

            if (client.encryptor != null) {
                buf.p1(prot.id + client.encryptor.nextInt())
            } else {
                buf.p1(prot.id)
            }

            if (prot.length == -1) {
                buf.p1(0)
            } else if (prot.length == -2) {
                buf.p2(0)
            }

            val start = buf.position()
            encode(buf)

            if (prot.length == -1) {
                buf.psize1(buf.position() - start)
            } else if (prot.length == -2) {
                buf.psize2(buf.position() - start)
            }

            out?.writeBytes(buf.data.sliceArray(0 until buf.position()))
        }
    }
}