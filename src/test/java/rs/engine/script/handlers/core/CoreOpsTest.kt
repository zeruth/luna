package rs.engine.script.handlers.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import rs.engine.script.RuneScriptOpcode
import rs.engine.script.ScriptState.Companion.FINISHED
import rs.engine.script.ScriptState.Companion.RUNNING
import rs.engine.script.handlers.BranchEqualsHandler
import rs.engine.script.handlers.BranchGreaterThanHandler
import rs.engine.script.handlers.BranchGreaterThanOrEqualsHandler
import rs.engine.script.handlers.BranchHandler
import rs.engine.script.handlers.GoSubWithParamsHandler
import rs.engine.script.handlers.ReturnHandler
import rs.engine.script.test.FakeScriptFile
import rs.engine.script.test.FakeScriptState

class CoreOpsTest {
    @Test
    fun `BRANCH_EQUALS pc does not increment when a is greater than b`() {
        val script = FakeScriptFile.simple(
            opcodes = intArrayOf(RuneScriptOpcode.BRANCH_EQUALS),
            intOperands = intArrayOf(3)
        )

        val state = FakeScriptState(script)

        // stack: a = 10, b = 5
        state.pushInt(10)
        state.pushInt(5)

        BranchEqualsHandler().handle(state)

        assertEquals(0, state.pc)
    }

    @Test
    fun `BRANCH_EQUALS pc increments when a equals b`() {
        val script = FakeScriptFile.simple(
            opcodes = intArrayOf(RuneScriptOpcode.BRANCH_EQUALS),
            intOperands = intArrayOf(3)
        )
        val state = FakeScriptState(script)

        // stack: a = 7, b = 7
        state.pushInt(7)
        state.pushInt(7)

        BranchEqualsHandler().handle(state)

        assertEquals(3, state.pc)
    }

    @Test
    fun `BRANCH_EQUALS pc does not increment when a is less than b`() {
        val script = FakeScriptFile.simple(
            opcodes = intArrayOf(RuneScriptOpcode.BRANCH_EQUALS),
            intOperands = intArrayOf(3)
        )
        val state = FakeScriptState(script)

        // stack: a = 2, b = 9
        state.pushInt(2)
        state.pushInt(9)

        BranchEqualsHandler().handle(state)

        assertEquals(0, state.pc)
    }

    @Test
    fun `BRANCH_GREATER_THAN pc increments when a is greater than b`() {
        val script = FakeScriptFile.simple(
            opcodes = intArrayOf(RuneScriptOpcode.BRANCH_GREATER_THAN),
            intOperands = intArrayOf(3)
        )

        val state = FakeScriptState(script)

        // stack: a = 10, b = 5
        state.pushInt(10)
        state.pushInt(5)

        BranchGreaterThanHandler().handle(state)

        assertEquals(3, state.pc)
    }

    @Test
    fun `BRANCH_GREATER_THAN pc does not increment when a equals b`() {
        val script = FakeScriptFile.simple(
            opcodes = intArrayOf(RuneScriptOpcode.BRANCH_GREATER_THAN),
            intOperands = intArrayOf(3)
        )
        val state = FakeScriptState(script)

        // stack: a = 7, b = 7
        state.pushInt(7)
        state.pushInt(7)

        BranchGreaterThanHandler().handle(state)

        assertEquals(0, state.pc)
    }

    @Test
    fun `BRANCH_GREATER_THAN pc does not increment when a is less than b`() {
        val script = FakeScriptFile.simple(
            opcodes = intArrayOf(RuneScriptOpcode.BRANCH_GREATER_THAN),
            intOperands = intArrayOf(3)
        )
        val state = FakeScriptState(script)

        // stack: a = 2, b = 9
        state.pushInt(2)
        state.pushInt(9)

        BranchGreaterThanHandler().handle(state)

        assertEquals(0, state.pc)
    }

    @Test
    fun `BRANCH_GREATER_THAN_OR_EQUALS pc increments when a is greater than b`() {
        val script = FakeScriptFile.simple(
            opcodes = intArrayOf(RuneScriptOpcode.BRANCH_GREATER_THAN_OR_EQUALS),
            intOperands = intArrayOf(3)
        )

        val state = FakeScriptState(script)

        // stack: a = 10, b = 5
        state.pushInt(10)
        state.pushInt(5)

        BranchGreaterThanOrEqualsHandler().handle(state)

        assertEquals(3, state.pc)
    }

    @Test
    fun `BRANCH_GREATER_THAN_OR_EQUALS pc increments when a equals b`() {
        val script = FakeScriptFile.simple(
            opcodes = intArrayOf(RuneScriptOpcode.BRANCH_GREATER_THAN_OR_EQUALS),
            intOperands = intArrayOf(3)
        )
        val state = FakeScriptState(script)

        // stack: a = 7, b = 7
        state.pushInt(7)
        state.pushInt(7)

        BranchGreaterThanOrEqualsHandler().handle(state)

        assertEquals(3, state.pc)
    }

    @Test
    fun `BRANCH_GREATER_THAN_OR_EQUALS pc does not increment when a is less than b`() {
        val script = FakeScriptFile.simple(
            opcodes = intArrayOf(RuneScriptOpcode.BRANCH_GREATER_THAN_OR_EQUALS),
            intOperands = intArrayOf(3)
        )
        val state = FakeScriptState(script)

        // stack: a = 2, b = 9
        state.pushInt(2)
        state.pushInt(9)

        BranchGreaterThanOrEqualsHandler().handle(state)

        assertEquals(0, state.pc)
    }

    @Test
    fun `BRANCH adds int operand to pc`() {
        val script = FakeScriptFile.simple(
            opcodes = intArrayOf(RuneScriptOpcode.BRANCH),
            intOperands = intArrayOf(5)
        )

        val state = FakeScriptState(script).apply {
            pc = 0
        }

        BranchHandler().handle(state)

        assertEquals(5, state.pc)
    }

    @Test
    fun `RETURN returns if root frame pointer`() {
        val script = FakeScriptFile.simple(
            opcodes = intArrayOf(RuneScriptOpcode.BRANCH)
        )

        val state = FakeScriptState(script)

        assertEquals(0, state.fp)

        ReturnHandler().handle(state)

        assertEquals(FINISHED, state.execution)
    }

    @Test
    fun `RETURN pops frame if not root frame pointer`() {
        val script = FakeScriptFile.simple(
            opcodes = intArrayOf(RuneScriptOpcode.GOSUB_WITH_PARAMS, RuneScriptOpcode.BRANCH),
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