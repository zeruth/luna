package rs.engine.game

import io.luna.game.model.World
import io.luna.game.model.mob.Player

object WorldExt {
    fun World.getInventory(inv: Int) : Inventory? {
        if (inv == -1) return null;

        for (inventory in invs.values) {
            if (inventory.type == inv)
                return inventory;
        }

        val invetory = Inventory.fromType(inv)
        invs[inv] = invetory

        return invetory
    }

    fun World.getPlayerByUid(uid: Int) : Player? {
        for (player in players) {
            if (player.index == uid)
                return player
        }
        return null
    }
}