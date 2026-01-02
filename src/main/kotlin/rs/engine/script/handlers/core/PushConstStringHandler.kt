package rs.engine.script.handlers.core

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.ScriptState
import rs.engine.script.RuneScriptOpcodeHandler

class PushConstStringHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.PUSH_CONSTANT_STRING
) {
    override fun handle(state: ScriptState) {
        state.pushString(state.stringOperand())
    }
}