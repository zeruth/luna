package rs.engine.script

import io.luna.game.model.Entity
import io.luna.game.model.item.GroundItem
import io.luna.game.model.mob.Npc
import io.luna.game.model.mob.Player
import io.luna.game.model.`object`.GameObject
import rs.engine.script.ScriptPointer.Companion.check
import rs.engine.script.handlers.*

object RuneScriptRunner {
    val handlers = HashMap<Int, RuneScriptOpcodeHandler?>()

    init {
        //Core (0-99)
        handlers[RuneScriptOpcode.PUSH_CONSTANT_INT] = PushConstIntHandler
        handlers[RuneScriptOpcode.POP_VARP] = PopVarpHandler
        handlers[RuneScriptOpcode.PUSH_CONSTANT_STRING] = PushConstStringHandler
        handlers[RuneScriptOpcode.BRANCH] = BranchHandler
        handlers[RuneScriptOpcode.BRANCH_EQUALS] = BranchEqualsHandler
        handlers[RuneScriptOpcode.BRANCH_GREATER_THAN] = BranchGreaterThanHandler
        handlers[RuneScriptOpcode.RETURN] = ReturnHandler
        handlers[RuneScriptOpcode.BRANCH_GREATER_THAN_OR_EQUALS] = BranchGreaterThanOrEqualsHandler
        handlers[RuneScriptOpcode.PUSH_INT_LOCAL] = PushIntLocalHandler
        handlers[RuneScriptOpcode.POP_INT_LOCAL] = PopIntLocalHandler
        handlers[RuneScriptOpcode.GOSUB_WITH_PARAMS] = GoSubWithParamsHandler

        // Server (1000-1999)

        // Player (2000-2499)
        handlers[RuneScriptOpcode.BAS_READYANIM] = BasReadyAnimHandler
        handlers[RuneScriptOpcode.BAS_TURNONSPOT] = BasTurnOnSpotHandler
        handlers[RuneScriptOpcode.BAS_WALK_F] = BasWalkFHandler
        handlers[RuneScriptOpcode.MES] = MesHandler
        handlers[RuneScriptOpcode.P_FINDUID] = PFindUidHandler
        handlers[RuneScriptOpcode.STAFFMODLEVEL] = StaffModLevelHandler
        handlers[RuneScriptOpcode.UID] = UidHandler
        handlers[RuneScriptOpcode.P_ANIMPROTECT] = PAnimProtectHandler

        // Npc (2500-2999)

        // Loc (3000-3499)

        // Obj (3500-4000)

        // Npc config (4000-4099)

        // Loc config (4100-4199)

        // Obj config (4200-4299)

        // Inventory (4300-4399)
        handlers[RuneScriptOpcode.INV_GETOBJ] = InvGetObjHandler
        handlers[RuneScriptOpcode.INV_TOTAL] = InvTotalHandler

        // Enum (4400-4499)

        // String (4500-4599)

        // Number (4600-4699)

        // DB (7500-7599)

        // Debug (10000-11000)
    }

    fun init(
        script: ScriptFile,
        self: Entity? = null,
        target: Entity? = null,
        args: Array<Any> = emptyArray()
    ): ScriptState {
        val state = ScriptState(script, args)
        state.self = self

        when (self) {
            is Player -> {
                state._activePlayer = self
                state.pointerAdd(ScriptPointer.ActivePlayer)
            }
            is Npc -> {
                state._activeNpc = self
                state.pointerAdd(ScriptPointer.ActiveNpc)
            }
            is GameObject -> {
                state._activeLoc = self
                state.pointerAdd(ScriptPointer.ActiveLoc)
            }
            is GroundItem -> {
                state._activeObj = self
                state.pointerAdd(ScriptPointer.ActiveObj)
            }
        }

        when (target) {
            is Player -> {
                if (self is Player) {
                    state._activePlayer2 = target
                    state.pointerAdd(ScriptPointer.ActivePlayer2)
                } else {
                    state._activePlayer = target
                    state.pointerAdd(ScriptPointer.ActivePlayer)
                }
            }
            is Npc -> {
                if (self is Npc) {
                    state._activeNpc2 = target
                    state.pointerAdd(ScriptPointer.ActiveNpc2)
                } else {
                    state._activeNpc = target
                    state.pointerAdd(ScriptPointer.ActiveNpc)
                }
            }
            is GameObject -> {
                if (self is GameObject) {
                    state._activeLoc2 = target
                    state.pointerAdd(ScriptPointer.ActiveLoc2)
                } else {
                    state._activeLoc = target
                    state.pointerAdd(ScriptPointer.ActiveLoc)
                }
            }
            is GroundItem -> {
                if (self is GroundItem) {
                    state._activeObj2 = target
                    state.pointerAdd(ScriptPointer.ActiveObj2)
                } else {
                    state._activeObj = target
                    state.pointerAdd(ScriptPointer.ActiveObj)
                }
            }
        }

        return state
    }

    fun execute(state: ScriptState?, reset: Boolean = false, benchmark: Boolean = false): Int {
        if (state == null || state.script == null || state.script!!.info == null) {
            return ScriptState.ABORTED
        }

        try {
            if (reset) {
                state.reset()
            }

            if (state.execution != ScriptState.RUNNING) {
                state.executionHistory.add(state.execution)
            }
            state.execution = ScriptState.RUNNING

            val start = System.nanoTime() / 1000 // microseconds
            while (state.execution == ScriptState.RUNNING) {
                if (state.pc >= state.script!!.opcodes.filterNotNull().size || state.pc < -1) {
                    throw IllegalStateException("Invalid program counter: ${state.pc}, max expected: ${state.script!!.opcodes.size}")
                }

                // if we're benchmarking we don't care about the opcount
                if (!benchmark && state.opcount > 500_000) {
                    throw IllegalStateException("Too many instructions")
                }

                state.opcount++
                val innerOp = state.script!!.opcodes[++state.pc]
                println("Executing inner opcode: ${RuneScriptOpcode.of(innerOp!!)}")
                executeInner(state, innerOp)
            }

            val time = ((System.nanoTime() / 1000) - start).toInt()
            if (time > 1000) {
                val message = "Warning [cpu time]: Script: ${state.script!!.name()}, time: ${time}us, opcount: ${state.opcount}"
/*                if (state.self is Player) {
                    state.self.wrappedMessageGame(message)
                } else {
                    println(message)
                }*/
                println(message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            state.execution = ScriptState.ABORTED
        }

        return state.execution
    }

    fun executeInner(state: ScriptState, opcode: Int?) {
        val handler = handlers[opcode] ?: throw IllegalStateException("Unknown opcode: $opcode")
        state.check(handler.pointers)
        handler.handle(state)
    }
}