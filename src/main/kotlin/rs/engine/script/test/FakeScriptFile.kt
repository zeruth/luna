package rs.engine.script.test

import rs.engine.script.RuneScriptOpcode
import rs.engine.script.ScriptFile
import rs.engine.script.ScriptInfo

object FakeScriptFile {

    fun simple(
        id: Int = 0,
        opcodes: IntArray,
        intLocals: IntArray = IntArray(opcodes.size),
        intOperands: IntArray = IntArray(opcodes.size),
        stringOperands: Array<String?> = arrayOfNulls(opcodes.size)
    ): ScriptFile {
        val script = ScriptFile(id)

        // Minimal ScriptInfo so name() etc. don’t explode
        script.info = ScriptInfo().apply {
            scriptName = "test_script_$id"
            sourceFilePath = "test"
            lookupKey = 0
        }

        // Zero locals/args unless a test needs them
        script.intLocalCount = 0
        script.stringLocalCount = 0
        script.intArgCount = 0
        script.stringArgCount = 0

        // Copy opcode stream
        for (i in opcodes.indices) {
            script.opcodes[i] = opcodes[i]
            script.intOperands[i] = intOperands.getOrNull(i)
            script.stringOperands[i] = stringOperands.getOrNull(i)
        }

        return script
    }

    fun branchScript(
        jumpOffset: Int
    ): ScriptFile =
        simple(
            opcodes = intArrayOf(
                RuneScriptOpcode.BRANCH_GREATER_THAN,
                RuneScriptOpcode.RETURN
            ),
            intOperands = intArrayOf(
                jumpOffset,
                0
            )
        )

}