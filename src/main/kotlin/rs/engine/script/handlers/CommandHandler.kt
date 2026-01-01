package rs.engine.script.handlers

import rs.engine.script.ScriptState

open class CommandHandler(val opcode: Int) {
    open fun handle(state: ScriptState) {}
}