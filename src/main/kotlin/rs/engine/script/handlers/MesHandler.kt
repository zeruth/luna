package rs.engine.script.handlers

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.ScriptPointer
import rs.engine.script.ScriptPointer.Companion.checkedHandler
import rs.engine.script.ScriptState

class MesHandler : CommandHandler(RuneScriptOpcode.MES) {
    override fun handle(state: ScriptState) {
        state.checkedHandler(ScriptPointer.ActivePlayer)
        val message = state.popString()

        state.activePlayer().sendMessage(message)
    }
}