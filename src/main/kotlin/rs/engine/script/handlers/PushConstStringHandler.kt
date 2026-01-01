package rs.engine.script.handlers

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.ScriptState

class PushConstStringHandler : CommandHandler(RuneScriptOpcode.PUSH_CONSTANT_STRING) {
    override fun handle(state: ScriptState) {
        state.pushString(state.stringOperand())
    }
}