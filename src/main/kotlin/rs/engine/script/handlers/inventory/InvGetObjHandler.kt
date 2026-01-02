package rs.engine.script.handlers.inventory

import rs.engine.game.PlayerExt.invGetSlot
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

        state.pushInt(state.activePlayer().invGetSlot(inv, slot))
    }
}