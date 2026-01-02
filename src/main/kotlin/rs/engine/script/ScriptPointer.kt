package rs.engine.script

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
        val ActivePlayers =  arrayOf(ActivePlayer, ActivePlayer2)
        val ProtectedActivePlayers =  arrayOf(ProtectedActivePlayer, ProtectedActivePlayer2)
        val ActiveNpcs =  arrayOf(ActiveNpc, ActiveNpc2)
        val ActiveLocs =  arrayOf(ActiveLoc, ActiveLoc2)
        val ActiveObjs =  arrayOf(ActiveObj, ActiveObj2)

        fun ScriptState.check(pointer: Any) {
            when (pointer) {
                is ScriptPointer -> {
                    pointerCheck(pointer)
                }

                is Array<*> -> {
                    val arr = pointer as? Array<ScriptPointer>
                    if (arr?.isNotEmpty() == true) {
                        pointerCheck(arr[intOperand()])
                        pointerCheck(*arr)
                    }
                }

                is List<*> -> {
                    val list = pointer as? List<ScriptPointer>
                    if (list?.isNotEmpty() == true) {
                        pointerCheck(list[intOperand()])
                        pointerCheck(*list.toTypedArray())
                    }
                }

                else -> {
                    throw IllegalArgumentException("Invalid pointer type: ${pointer::class}")
                }
            }
        }
    }
}