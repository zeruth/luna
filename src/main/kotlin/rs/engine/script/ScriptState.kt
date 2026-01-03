package rs.engine.script

import io.luna.game.model.Entity
import io.luna.game.model.item.GroundItem
import io.luna.game.model.mob.Npc
import io.luna.game.model.mob.Player
import io.luna.game.model.`object`.GameObject
import rs.cache.config.DbTableType
import rs.engine.script.frame.GoSubStackFrame
import rs.engine.script.frame.JumpStackFrame
import kotlin.Array

open class ScriptState(
    var script: ScriptFile? = null,
    val args: Array<Any>,
) {
    constructor(script: ScriptFile) : this(script, emptyArray())

    companion object {
        const val ABORTED = -1
        const val RUNNING = 0
        const val FINISHED = 1
        const val SUSPENDED = 2
        const val PAUSEBUTTON = 3
        const val COUNTDIALOG = 4
        const val NPC_SUSPENDED = 5
        const val WORLD_SUSPENDED = 6

        fun of(id: Int) : String {
            return when (id) {
                -1 -> "ABORTED"
                0 -> "RUNNING"
                1 -> "FINISHED"
                2 -> "SUSPENDED"
                3 -> "PAUSEBUTTON"
                4 -> "COUNTDIALOG"
                5 -> "NPC_SUSPENDED"
                6 -> "WORLD_SUSPENDED"
                else -> throw IllegalArgumentException("Unknown id $id")
            }
        }
    }
    val trigger: Int = if (script?.info == null) -1 else script!!.info!!.lookupKey

    var execution = RUNNING
    val executionHistory = mutableListOf<Int>()

    open var pc = -1
    var opcount = -1

    var frames = Array<GoSubStackFrame?>(512) { null }
    var fp = 0

    var debugFrames: Array<JumpStackFrame> = emptyArray()
    var debugFp = 0

    var intStack = Array<Int?>(512) { null }
    var isp = 0

    var stringStack = Array<String?>(512) { null }
    var ssp = 0

    var intLocals: Array<Int> = emptyArray()
    var stringLocals: Array<String> = emptyArray()

    private var pointers = 0

    var self: Entity? = null

    var _activePlayer: Player? = null
    var _activePlayer2: Player? = null

    var _activeNpc: Npc? = null
    var _activeNpc2: Npc? = null

    var _activeLoc: GameObject? = null
    var _activeLoc2: GameObject? = null

    var _activeObj: GroundItem? = null
    var _activeObj2: GroundItem? = null

    var splitPages: Array<Array<String>> = emptyArray()
    var splitMesanim = -1

    var dbTable: DbTableType? = null
    var dbColumn = -1
    var dbRow = -1
    var dbRowQuery: Array<Int> = emptyArray()

    var timespent = 0

    var huntIterator = ArrayList<Entity>()
    var npcIterator = ArrayList<Npc>()
    var locIterator = ArrayList<GameObject>()
    var objIterator = ArrayList<GroundItem>()

    var lastInt = 0

    /**
     * Gets the active player. Automatically checks the operand to determine primary and secondary.
     */
    val activePlayer: Player
        get() : Player {
        val player = if (this.intOperand() == 0) this._activePlayer else this._activePlayer2
        if (player == null) {
            throw RuntimeException("Attempt to access null active_player")
        }
        return player;
    }

    fun activePlayer(player: Player) {
        _activePlayer = player;
    }


    fun pointerAdd(pointer: ScriptPointer) {
        pointers = pointers or (1 shl pointer.ordinal)
    }

    fun pointerGet(pointer: Int): Boolean {
        return (pointers and (1 shl pointer)) != 0
    }


    fun pointerCheck(vararg pointers: ScriptPointer) {
        for (i in pointers.indices) {
            val flag = 1 shl pointers[i].ordinal
            if ((this.pointers and flag) != flag) {
                throw RuntimeException(
                    "${script!!.name()}: Required pointer: ${pointerPrint(flag)}, current: ${pointerPrint(this.pointers)}"
                )
            }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun pointerPrint(flags: Int): String {
        var text = ""
        for (i in 0 until ScriptPointer._LAST.ordinal) {
            if ((flags and (1 shl i)) != 0) {
                text += "${ScriptPointer.entries[i].name}, "
            }
        }
        return if (text.isNotEmpty())
            text.substring(0, text.lastIndexOf(','))
        else
            text
    }

    fun pushInt(value: Int) {
        intStack[isp++] = value
    }

    fun pushString(value: String) {
        stringStack[ssp++] = value
    }

    fun popInt(): Int {
        val value = intStack[--isp]
        return value ?: 0
    }

    fun popInts(amount: Int): IntArray {
        val ints = IntArray(amount)
        for (i in amount - 1 downTo 0) {
            ints[i] = popInt()
        }
        return ints
    }

    fun popString(): String {
        return stringStack[--ssp] ?: ""
    }

    fun popFrame() {
        val frame = frames[--fp]!!
        pc = frame.pc
        script = frame.script
        intLocals = frame.intLocals
        stringLocals = frame.stringLocals
    }

    fun intOperand(): Int {
        return this.script!!.intOperands[this.pc]!!
    }

    fun stringOperand(): String {
        return this.script!!.stringOperands[this.pc]!!
    }

    fun reset() {
        pc = -1
        frames = emptyArray()
        fp = 0
        intStack = emptyArray()
        isp = 0
        stringStack = emptyArray()
        ssp = 0
        intLocals = emptyArray()
        stringLocals = emptyArray()
        pointers = 0
    }

    fun gosubFrame(proc: ScriptFile) {
        frames[fp++] = GoSubStackFrame(
            script!!,
            pc,
            intLocals,
            stringLocals
        )
        setupNewScript(proc)
    }

    fun setupNewScript(script: ScriptFile) {
        val argString = StringBuilder()

        val intLocals = Array(script.intLocalCount) { 0 }
        val intArgCount = script.intArgCount
        for (index in 0 until intArgCount) {
            val value = this.popInt()
            intLocals[intArgCount - index - 1] = value
            argString.append(" i(${value})")
        }

        val stringLocals = Array(script.stringLocalCount) { "" }
        val stringArgCount = script.stringArgCount
        for (index in 0 until stringArgCount) {
            val value = this.popString()
            stringLocals[stringArgCount - index - 1] = value
            argString.append(" s(${value})")
        }

        println("${script.name()}$argString")

        pc = -1
        this.script = script
        this.intLocals = intLocals
        this.stringLocals = stringLocals
    }
}