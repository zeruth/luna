package rs.engine.script.handlers.core

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.ScriptState
import rs.engine.script.RuneScriptOpcodeHandler

class PushConstIntHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.PUSH_CONSTANT_INT
) {
    override fun handle(state: ScriptState) {
        state.pushInt(state.intOperand())
    }
}