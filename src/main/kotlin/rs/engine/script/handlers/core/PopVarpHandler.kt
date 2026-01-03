package rs.engine.script.handlers.core

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.ScriptState
import rs.engine.script.RuneScriptOpcodeHandler
import rs.engine.script.RuneScriptProvider

class PopVarpHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.POP_VARP
) {
    override fun handle(state: ScriptState) {
        val secondary = (state.intOperand() shr 16) and 0x1
        val player = if (secondary != 0) state._activePlayer2 else state._activePlayer

        if (player == null)
            throw RuntimeException("No Active Player")

    }
}