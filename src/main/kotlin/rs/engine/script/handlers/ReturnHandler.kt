package rs.engine.script.handlers

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.ScriptState

class ReturnHandler : CommandHandler(RuneScriptOpcode.RETURN) {
    override fun handle(state: ScriptState) {
        if (state.fp == 0) {
            state.execution = ScriptState.FINISHED
            return
        }

        state.popFrame()
    }
}