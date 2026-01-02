package rs.engine.script.handlers.core

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.ScriptState
import rs.engine.script.RuneScriptOpcodeHandler
import rs.engine.script.RuneScriptProvider

class BranchGreaterThanOrEqualsHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.BRANCH_GREATER_THAN_OR_EQUALS
) {
    override fun handle(state: ScriptState) {
        val b = state.popInt()
        val a = state.popInt()
        if (a >= b) {
            state.pc += state.intOperand()
        }
    }
}