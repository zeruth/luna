package rs.engine.script.handlers

import rs.cache.config.InvType
import rs.engine.game.PlayerExt.invGetSlot
import rs.engine.game.PlayerExt.invTotal
import rs.engine.script.RuneScriptOpcode
import rs.engine.script.RuneScriptOpcodeHandler
import rs.engine.script.ScriptPointer
import rs.engine.script.ScriptState

class InvGetObjHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.INV_GETOBJ,
    ScriptPointer.ActivePlayer
) {
    override fun handle(state: ScriptState) {
        val ints = state.popInts(2)
        val inv = ints[0]
        val slot = ints[1]

        state.pushInt(state.activePlayer.invGetSlot(inv, slot))
    }
}

class InvTotalHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.INV_TOTAL,
    ScriptPointer.ActivePlayer
) {
    override fun handle(state: ScriptState) {
        val ints = state.popInts(2)
        val inv = ints[0]
        val obj = ints[1]

        InvType.get(inv) ?: throw RuntimeException("Unknown invType $inv")

        state.pushInt(state.activePlayer.invTotal(inv, obj))
    }
}