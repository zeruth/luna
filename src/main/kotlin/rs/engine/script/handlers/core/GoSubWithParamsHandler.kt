package rs.engine.script.handlers.core

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.ScriptState
import rs.engine.script.RuneScriptOpcodeHandler
import rs.engine.script.RuneScriptProvider

class GoSubWithParamsHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.GOSUB_WITH_PARAMS
) {
    override fun handle(state: ScriptState) {
        if (state.fp >= 50) {
            throw RuntimeException("stack overflow")
        }
        val id = state.intOperand()
        val proc = RuneScriptProvider.get(id) ?: throw RuntimeException("unable to find proc with id: $id")

        state.gosubFrame(proc)
    }
}