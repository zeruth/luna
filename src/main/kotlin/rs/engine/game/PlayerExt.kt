package rs.engine.game

import io.luna.game.model.mob.Player
import rs.cache.config.InvType
import rs.cache.config.ScriptVarType
import rs.cache.config.VarPlayerType
import rs.engine.game.WorldExt.getInventory
import rs.net.msg.out.VarpLarge
import rs.net.msg.out.VarpSmall
import rs.net.msg.out.game.ServerGameMessage
import rs.net.msg.out.game.ServerGameProtPriority

object PlayerExt {

    fun Player.invTotal(inv: Int, obj: Int) : Int {
        val container = getInventory(inv) ?: throw RuntimeException("invGetSlot: Invalid inventory type: $inv")
        return container.getItemCount(obj)
    }

    fun Player.invGetSlot(inv: Int, slot: Int) : Int {
        val container = getInventory(inv) ?: throw RuntimeException("invGetSlot: Invalid inventory type: $inv")

        if (!container.validSlot(slot)) {
            throw RuntimeException("invGetSlot: Invalid slot: $slot of max ${container.capacity}");
        }

        return container.get(slot)?.id ?: -1
    }

    fun Player.getInventory(inv: Int) : Inventory? {
        if (inv == -1) return null;

        val invType = InvType.get(inv)
        var container: Inventory?

        if (invType == null) return null;

        if (invType.scope == InvType.SCOPE_SHARED) {
            container = world.getInventory(inv)
        } else {
            container = invs[inv]

            if (container == null) {
                container = Inventory.fromType(inv)
            }
        }

        return container
    }

    fun Player.canAccess(): Boolean {
        // once the world has gone past shutting down, no protection rules apply
        if (world.shutdown)
            return true

        return !protect && !busy();
    }

    fun Player.busy(): Boolean {
        return delayed || containsModalInterface()
    }

    fun Player.containsModalInterface(): Boolean {
        // main or chat is open
        return (this.modalState.mask and (ModalState.MAIN.mask or ModalState.CHAT.mask)) != ModalState.NONE.mask
    }

    fun Player.setVar(id: Int, value: Any) {
        val varp = VarPlayerType.get(id) ?: throw RuntimeException("VarPlayerType not found: $id")

        if (varp.type == ScriptVarType.STRING && value is String) {
            varsString[varp.id] = value
        } else if (value is Int) {
            vars[varp.id] = value;

            if (varp.transmit) {
                this.writeVarp(id, value);
            }
        } else {
            throw RuntimeException("VarPlayerType set value not valid: $value")
        }
    }

    fun Player.isConnected() : Boolean {
        return client.channel.isActive
    }

    fun Player.writeInner(mesage: ServerGameMessage) {
        client.channel.pipeline().write(mesage)
        //TODO: Track metrics
    }

    fun Player.write(mesage: ServerGameMessage) {
        if (!isConnected()) {
            return
        }

        if (mesage.priority == ServerGameProtPriority.IMMEDIATE) {
            writeInner(mesage)
        } else {
            buffer.add(mesage)
        }
    }

    fun Player.writeVarp(id: Int, value: Int) {
        if (value in -128..127) {
            write(VarpSmall(id, value, player = this))
        } else {
            write(VarpLarge(id, value, player = this))
        }
    }
}