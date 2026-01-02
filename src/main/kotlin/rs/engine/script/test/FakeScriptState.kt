package rs.engine.script.test

import rs.engine.script.ScriptFile
import rs.engine.script.ScriptState

class FakeScriptState(script: ScriptFile, args: Array<Any>) : ScriptState(script, args) {
    constructor(script: ScriptFile) : this(script, emptyArray())

    init {
        pc = 0
    }
}