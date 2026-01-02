package rs.engine.script.handlers.player

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.RuneScriptOpcodeHandler
import rs.engine.script.ScriptPointer
import rs.engine.script.ScriptState

class UidHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.UID,
    ScriptPointer.ActivePlayer
) {
    override fun handle(state: ScriptState) {
        //TODO: Fix this, luna doesn't track uid like lost-city
        state.pushInt(state.activePlayer().index)
    }
}