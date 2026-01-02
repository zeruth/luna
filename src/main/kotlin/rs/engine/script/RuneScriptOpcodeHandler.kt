package rs.engine.script

import rs.engine.script.ScriptPointer.Companion.check

open class RuneScriptOpcodeHandler(
    val opcode: Int,
    vararg val pointers: ScriptPointer? = emptyArray()
) {
    open fun handle(state: ScriptState) {}
    fun process(state: ScriptState) {
        state.check(pointers)
        handle(state)
    }
}