package rs.engine.script.handlers.player

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.RuneScriptOpcodeHandler
import rs.engine.script.ScriptPointer
import rs.engine.script.ScriptState

class StaffModLevelHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.STAFFMODLEVEL,
    ScriptPointer.ActivePlayer
) {
    override fun handle(state: ScriptState) {
        state.pushInt(state.activePlayer().rights.clientValue)
    }
}