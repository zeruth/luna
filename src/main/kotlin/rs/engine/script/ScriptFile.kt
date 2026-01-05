package rs.engine.script

import me.filby.neptune.serverscript.compiler.ServerScriptOpcode
import rs.io.Packet
import rs.engine.script.ScriptInfo

class ScriptFile(val id: Int) {
    var info: ScriptInfo? = null

    var intLocalCount = 0
    var stringLocalCount = 0
    var intArgCount = 0
    var stringArgCount = 0

    var switchTables = Array<MutableMap<Int, Int>?>(512) { null }

    var opcodes = Array<Int?>(2056) { null }
    var intOperands = Array<Int?>(2056) { null }
    var stringOperands = Array<String?>(2056) { null }

    fun name() : String {
        return info!!.scriptName
    }

    companion object {
        private fun isLargeOperand(opcode: Int): Boolean {
            if (opcode > 100) return false

            return when (opcode) {
                ServerScriptOpcode.RETURN.id,
                ServerScriptOpcode.POP_INT_DISCARD.id,
                ServerScriptOpcode.POP_STRING_DISCARD.id,
                ServerScriptOpcode.GOSUB.id,
                ServerScriptOpcode.JUMP.id -> false
                else -> true
            }
        }

        fun decode(id: Int, stream: Packet): ScriptFile {
            val length = stream.length()
            if (length < 16) {
                throw RuntimeException("Invalid length")
            }

            stream.position(length - 2)

            val trailerLen = stream.g2()
            val trailerPos = length - trailerLen - 12 - 2

            if (trailerPos !in 0 until length) {
                throw RuntimeException("Trailer length")
            }

            stream.position(trailerPos)

            val script = ScriptFile(id)
            val _instructions = stream.g4s()
            script.intLocalCount = stream.g2()
            script.stringLocalCount = stream.g2()
            script.intArgCount = stream.g2()
            script.stringArgCount = stream.g2()

            val switches = stream.g1()

            for (i in 0 until switches) {
                val count = stream.g2()
                val table = mutableMapOf<Int, Int>()

                for (j in 0 until count) {
                    val key = stream.g4s()
                    val offset = stream.g4s()
                    table[key] = offset
                }

                script.switchTables[i] = table
            }

            stream.position(0)

            script.info = ScriptInfo()

            script.info!!.scriptName = stream.gjstr(0)
            script.info!!.sourceFilePath = stream.gjstr(0)
            script.info!!.lookupKey = stream.g4s()

            val parameterTypeCount = stream.g1()
            for (i in 0 until parameterTypeCount) {
                script.info!!.parameterTypes.add(stream.g1());
            }

            val lineNumberTableLength = stream.g2()
            for (i in 0 until lineNumberTableLength) {
                script.info!!.pcs.add(stream.g4s());
                script.info!!.lines.add(stream.g4s());
            }

            var instr = 0
            while (trailerPos > stream.position()) {
                val opcode = stream.g2()

                if (opcode == ServerScriptOpcode.PUSH_CONSTANT_STRING.id) {
                    script.stringOperands[instr] = stream.gjstr(0)
                } else if (isLargeOperand(opcode)) {
                    script.intOperands[instr] = stream.g4s();
                } else {
                    script.intOperands[instr] = stream.g1();
                }

                script.opcodes[instr++] = opcode;
            }

            return script
        }
    }
}