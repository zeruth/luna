package rs.engine.script.handlers.player

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.RuneScriptOpcodeHandler
import rs.engine.script.ScriptPointer
import rs.engine.script.ScriptState

class PAnimProtectHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.P_ANIMPROTECT,
    ScriptPointer.ActivePlayer
) {
    override fun handle(state: ScriptState) {
        val value = state.popInt()
        check(value > -1)
        state.activePlayer().animProtect = value
    }
}