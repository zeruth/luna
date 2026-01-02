package rs.engine.script.handlers.player

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.RuneScriptOpcodeHandler
import rs.engine.script.ScriptPointer
import rs.engine.script.ScriptState

class BasWalkFHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.BAS_WALK_F,
    ScriptPointer.ActivePlayer
) {
    override fun handle(state: ScriptState) {
        val value = state.popInt()
        state.activePlayer().basWalkForward = value
    }
}