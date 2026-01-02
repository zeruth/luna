package rs.engine.script.handlers.core

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.ScriptState
import rs.engine.script.RuneScriptOpcodeHandler

class BranchGreaterThanHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.BRANCH_GREATER_THAN
) {
    override fun handle(state: ScriptState) {
        val b = state.popInt()
        val a = state.popInt()
        if (a > b) {
            state.pc += state.intOperand()
        }
    }
}