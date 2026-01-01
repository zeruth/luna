package rs.engine.script

object RuneScriptOpcode {
    const val PUSH_CONSTANT_STRING = 3
    const val RETURN = 21
    const val MES = 2070

    fun of(id: Int) : String {
        return when (id) {
            PUSH_CONSTANT_STRING -> "PUSH_CONSTANT_STRING"
            RETURN -> "RETURN"
            MES -> "MES"
            else -> throw IllegalArgumentException("Unknown id $id")
        }
    }
}