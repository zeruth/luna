package rs.engine.script.handlers

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.RuneScriptOpcodeHandler
import rs.engine.script.RuneScriptProvider
import rs.engine.script.ScriptState

class BranchEqualsHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.BRANCH_EQUALS
) {
    override fun handle(state: ScriptState) {
        val b = state.popInt()
        val a = state.popInt()

        if (a == b)
            state.pc += state.intOperand()
    }
}

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

class BranchHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.BRANCH
) {
    override fun handle(state: ScriptState) {
        state.pc += state.intOperand()
    }
}

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

class PopIntLocalHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.POP_INT_LOCAL
) {
    override fun handle(state: ScriptState) {
        state.intLocals[state.intOperand()] = state.popInt()
    }
}

class PopVarpHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.POP_VARP
) {
    override fun handle(state: ScriptState) {
        val secondary = (state.intOperand() shr 16) and 0x1
        val player = if (secondary != 0) state._activePlayer2 else state._activePlayer

        if (player == null)
            throw RuntimeException("No Active Player")

        //TODO
    }
}

class PushConstIntHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.PUSH_CONSTANT_INT
) {
    override fun handle(state: ScriptState) {
        state.pushInt(state.intOperand())
    }
}

class PushConstStringHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.PUSH_CONSTANT_STRING
) {
    override fun handle(state: ScriptState) {
        state.pushString(state.stringOperand())
    }
}

class PushIntLocalHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.PUSH_INT_LOCAL
) {
    override fun handle(state: ScriptState) {
        state.pushInt(state.intLocals[state.intOperand()])
    }
}

class ReturnHandler : RuneScriptOpcodeHandler(
    RuneScriptOpcode.RETURN
) {
    override fun handle(state: ScriptState) {
        if (state.fp == 0) {
            state.execution = ScriptState.FINISHED
            return
        }

        state.popFrame()
    }
}