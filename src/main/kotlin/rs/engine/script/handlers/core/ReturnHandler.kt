package rs.engine.script.handlers.core

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.ScriptState
import rs.engine.script.RuneScriptOpcodeHandler

class ReturnHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.RETURN
) {
    override fun handle(state: ScriptState) {
        if (state.fp == 0) {
            state.execution = ScriptState.FINISHED
            return
        }

        state.popFrame()
    }
}