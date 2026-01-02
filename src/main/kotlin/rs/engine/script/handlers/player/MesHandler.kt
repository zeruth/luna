package rs.engine.script.handlers.player

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.RuneScriptOpcodeHandler
import rs.engine.script.ScriptPointer
import rs.engine.script.ScriptState

class MesHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.MES,
    ScriptPointer.ActivePlayer
) {
    override fun handle(state: ScriptState) {
        val message = state.popString()

        state.activePlayer().sendMessage(message)
    }
}