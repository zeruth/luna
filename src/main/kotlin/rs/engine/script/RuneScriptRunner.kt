package rs.engine.script

import io.luna.game.model.Entity
import io.luna.game.model.item.GroundItem
import io.luna.game.model.mob.Npc
import io.luna.game.model.mob.Player
import io.luna.game.model.`object`.GameObject
import rs.engine.script.handlers.CommandHandler
import rs.engine.script.handlers.MesHandler
import rs.engine.script.handlers.PushConstStringHandler
import rs.engine.script.handlers.ReturnHandler

object RuneScriptRunner {
    val handlers = HashMap<Int, CommandHandler?>()

    init {
        handlers[RuneScriptOpcode.PUSH_CONSTANT_STRING] = PushConstStringHandler()
        handlers[RuneScriptOpcode.RETURN] = ReturnHandler()
        handlers[RuneScriptOpcode.MES] = MesHandler()
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
        handler.handle(state)
    }
}