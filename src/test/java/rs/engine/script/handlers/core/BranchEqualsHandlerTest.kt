package rs.engine.script.handlers.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import rs.engine.script.test.FakeScriptFile
import rs.engine.script.RuneScriptOpcode
import rs.engine.script.test.FakeScriptState

class BranchEqualsHandlerTest {

    private val handler = BranchEqualsHandler()

    private val opcode = RuneScriptOpcode.BRANCH_EQUALS

    @Test
    fun `pc does not increment when a is greater than b`() {
        val script = FakeScriptFile.simple(
            opcodes = intArrayOf(opcode),
            intOperands = intArrayOf(3)
        )

        val state = FakeScriptState(script)

        // stack: a = 10, b = 5
        state.pushInt(10)
        state.pushInt(5)

        handler.handle(state)

        assertEquals(0, state.pc)
    }

    @Test
    fun `pc increments when a equals b`() {
        val script = FakeScriptFile.simple(
            opcodes = intArrayOf(opcode),
            intOperands = intArrayOf(3)
        )
        val state = FakeScriptState(script)

        // stack: a = 7, b = 7
        state.pushInt(7)
        state.pushInt(7)

        handler.handle(state)

        assertEquals(3, state.pc)
    }

    @Test
    fun `pc does not increment when a is less than b`() {
        val script = FakeScriptFile.simple(
            opcodes = intArrayOf(opcode),
            intOperands = intArrayOf(3)
        )
        val state = FakeScriptState(script)

        // stack: a = 2, b = 9
        state.pushInt(2)
        state.pushInt(9)

        handler.handle(state)

        assertEquals(0, state.pc)
    }
}
