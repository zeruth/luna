package rs.engine.script.handlers.core

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.ScriptState
import rs.engine.script.RuneScriptOpcodeHandler
import rs.engine.script.RuneScriptProvider

class PopIntLocalHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.POP_INT_LOCAL
) {
    override fun handle(state: ScriptState) {
        state.intLocals[state.intOperand()] = state.popInt()
    }
}