package rs.engine.script.handlers.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import rs.engine.script.test.FakeScriptFile
import rs.engine.script.RuneScriptOpcode
import rs.engine.script.ScriptState.Companion.FINISHED
import rs.engine.script.ScriptState.Companion.RUNNING
import rs.engine.script.test.FakeScriptState

class ReturnHandlerTest {

    private val opcode = RuneScriptOpcode.BRANCH

    @Test
    fun `return opcode returns if root frame pointer`() {
        val script = FakeScriptFile.simple(
            opcodes = intArrayOf(opcode)
        )

        val state = FakeScriptState(script)

        assertEquals(0, state.fp)

        ReturnHandler().handle(state)

        assertEquals(FINISHED, state.execution)
    }

    @Test
    fun `return opcode pops frame if not root frame pointer`() {
        val script = FakeScriptFile.simple(
            opcodes = intArrayOf(RuneScriptOpcode.GOSUB_WITH_PARAMS, opcode),
            intOperands = intArrayOf(0)
        )

        val state = FakeScriptState(script)

        // Go into sub script
        GoSubWithParamsHandler().handle(state)

        // Return from sub script
        ReturnHandler().handle(state)

        // Should still be running root script
        assertEquals(RUNNING, state.execution)
    }
}
