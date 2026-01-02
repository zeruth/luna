package rs.engine.script.handlers.player

import rs.engine.game.PlayerExt.canAccess
import rs.engine.game.WorldExt.getPlayerByUid
import rs.engine.script.RuneScriptOpcode
import rs.engine.script.RuneScriptOpcodeHandler
import rs.engine.script.ScriptPointer
import rs.engine.script.ScriptPointer.Companion.ActivePlayers
import rs.engine.script.ScriptPointer.Companion.ProtectedActivePlayers
import rs.engine.script.ScriptState

class PFindUidHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.P_FINDUID,
    ScriptPointer.ActivePlayer
) {
    override fun handle(state: ScriptState) {
        //TODO: Fix this, luna doesn't track uid like lost-city
        val uid = state.popInt() // shr 0
        val player = state.activePlayer().world.getPlayerByUid(uid)

        if (state.pointerGet(ScriptPointer.ProtectedActivePlayers[state.intOperand()].ordinal) && state.activePlayer().index == uid) {
            // script is already running on this player with protected access, no-op
            state.pushInt(1);
            return;
        }

        if (player == null || !player.canAccess()) {
            state.pushInt(0);
            return;
        }

        state.activePlayer(player)
        state.pointerAdd(ActivePlayers[state.intOperand()]);
        state.pointerAdd(ProtectedActivePlayers[state.intOperand()]);
        state.pushInt(1);
    }
}