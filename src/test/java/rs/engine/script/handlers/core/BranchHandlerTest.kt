package rs.engine.script.handlers.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import rs.engine.script.test.FakeScriptFile
import rs.engine.script.RuneScriptOpcode
import rs.engine.script.test.FakeScriptState

class BranchHandlerTest {

    private val handler = BranchHandler()

    private val opcode = RuneScriptOpcode.BRANCH

    @Test
    fun `branch opcode adds int operand to pc`() {
        val script = FakeScriptFile.simple(
            opcodes = intArrayOf(opcode),
            intOperands = intArrayOf(5)
        )

        val state = FakeScriptState(script).apply {
            pc = 0
        }

        handler.handle(state)

        assertEquals(5, state.pc)
    }
}
