package rs.engine.game

import io.luna.game.model.mob.Player
import rs.cache.config.InvType
import rs.engine.game.WorldExt.getInventory

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
}