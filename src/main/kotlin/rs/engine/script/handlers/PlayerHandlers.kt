package rs.engine.script.handlers

import rs.engine.game.PlayerExt.canAccess
import rs.engine.game.WorldExt.getPlayerByUid
import rs.engine.script.RuneScriptOpcode
import rs.engine.script.RuneScriptOpcodeHandler
import rs.engine.script.ScriptPointer
import rs.engine.script.ScriptPointer.Companion.ActivePlayers
import rs.engine.script.ScriptPointer.Companion.ProtectedActivePlayers
import rs.engine.script.ScriptState

object BasReadyAnimHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.BAS_READYANIM,
    ScriptPointer.ActivePlayer
) {
    override fun handle(state: ScriptState) {
        val value = state.popInt()
        state.activePlayer.basReadyAnim = value
    }
}

object BasTurnOnSpotHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.BAS_TURNONSPOT,
    ScriptPointer.ActivePlayer
) {
    override fun handle(state: ScriptState) {
        val value = state.popInt()
        state.activePlayer.basTurnOnSpot = value
    }
}

object BasWalkBHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.BAS_WALK_B,
    ScriptPointer.ActivePlayer
) {
    override fun handle(state: ScriptState) {
        val value = state.popInt()
        state.activePlayer.basWalkBackward = value
    }
}

object BasWalkFHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.BAS_WALK_F,
    ScriptPointer.ActivePlayer
) {
    override fun handle(state: ScriptState) {
        val value = state.popInt()
        state.activePlayer.basWalkForward = value
    }
}

object MesHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.MES,
    ScriptPointer.ActivePlayer
) {
    override fun handle(state: ScriptState) {
        val message = state.popString()

        state.activePlayer.sendMessage(message)
    }
}

object PAnimProtectHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.P_ANIMPROTECT,
    ScriptPointer.ActivePlayer
) {
    override fun handle(state: ScriptState) {
        val value = state.popInt()
        check(value > -1)
        state.activePlayer.animProtect = value
    }
}

object PFindUidHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.P_FINDUID,
    ScriptPointer.ActivePlayer
) {
    override fun handle(state: ScriptState) {
        //TODO: Fix this, luna doesn't track uid like lost-city
        val uid = state.popInt() // shr 0
        val player = state.activePlayer.world.getPlayerByUid(uid)

        if (state.pointerGet(ScriptPointer.ProtectedActivePlayers[state.intOperand()].ordinal) && state.activePlayer.index == uid) {
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

object StaffModLevelHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.STAFFMODLEVEL,
    ScriptPointer.ActivePlayer
) {
    override fun handle(state: ScriptState) {
        state.pushInt(state.activePlayer.rights.clientValue)
    }
}

object UidHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.UID,
    ScriptPointer.ActivePlayer
) {
    override fun handle(state: ScriptState) {
        //TODO: Fix this, luna doesn't track uid like lost-city
        state.pushInt(state.activePlayer.index)
    }
}