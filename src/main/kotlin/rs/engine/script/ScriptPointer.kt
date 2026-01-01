package rs.engine.script

import rs.engine.script.handlers.CommandHandler

enum class ScriptPointer {
    ActivePlayer,
    ActivePlayer2,
    ProtectedActivePlayer,
    ProtectedActivePlayer2,
    ActiveNpc,
    ActiveNpc2,
    ActiveLoc,
    ActiveLoc2,
    ActiveObj,
    ActiveObj2,
    _LAST;

    companion object {
        fun ScriptState.checkedHandler(pointer: Any) {
            when (pointer) {
                is ScriptPointer -> {
                    pointerCheck(pointer)
                }

                is Array<*> -> {
                    val arr = pointer as Array<ScriptPointer>
                    pointerCheck(arr[intOperand()])
                    pointerCheck(*arr)
                }

                is List<*> -> {
                    val list = pointer as List<ScriptPointer>
                    pointerCheck(list[intOperand()])
                    pointerCheck(*list.toTypedArray())
                }

                else -> {
                    throw IllegalArgumentException("Invalid pointer type: ${pointer::class}")
                }
            }
        }
    }
}