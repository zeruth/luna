package rs.engine.script.handlers.core

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.ScriptState
import rs.engine.script.RuneScriptOpcodeHandler
import rs.engine.script.RuneScriptProvider

class BranchHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.BRANCH
) {
    override fun handle(state: ScriptState) {
        state.pc += state.intOperand()
    }
}