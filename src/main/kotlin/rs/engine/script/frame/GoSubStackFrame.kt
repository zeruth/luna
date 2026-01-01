package rs.engine.script.frame

import rs.engine.script.ScriptFile


class GoSubStackFrame(
    val script: ScriptFile,
    val pc: Int,
    val intLocals: Array<Int>,
    val stringLocals: Array<String>) {
}