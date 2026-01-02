package rs.engine.script

object RuneScriptOpcode {
    //Core (0-99)
    const val PUSH_CONSTANT_INT = 0
    const val PUSH_CONSTANT_STRING = 3
    const val BRANCH = 6
    const val BRANCH_EQUALS = 8
    const val BRANCH_GREATER_THAN = 10
    const val RETURN = 21
    const val BRANCH_GREATER_THAN_OR_EQUALS = 32
    const val PUSH_INT_LOCAL = 33
    const val POP_INT_LOCAL = 34
    const val GOSUB_WITH_PARAMS = 40

    // Server (1000-1999)

    // Player (2000-2499)
    const val BAS_READYANIM = 2002
    const val BAS_TURNONSPOT = 2004
    const val BAS_WALK_B = 2005
    const val BAS_WALK_F = 2006
    const val MES = 2070
    const val P_FINDUID = 2079
    const val STAFFMODLEVEL = 2104
    const val UID = 2115
    const val P_ANIMPROTECT = 2128

    // Npc (2500-2999)

    // Loc (3000-3499)

    // Obj (3500-4000)

    // Npc config (4000-4099)

    // Loc config (4100-4199)

    // Obj config (4200-4299)

    // Inventory (4300-4399)
    const val INV_GETOBJ = 4313
    const val INV_TOTAL = 4323

    // Enum (4400-4499)

    // String (4500-4599)

    // Number (4600-4699)

    // DB (7500-7599)

    // Debug (10000-11000)

    fun of(id: Int) : String {
        return when (id) {
            PUSH_CONSTANT_INT -> "PUSH_CONSTANT_INT"
            PUSH_CONSTANT_STRING -> "PUSH_CONSTANT_STRING"
            BRANCH -> "BRANCH"
            BRANCH_EQUALS -> "BRANCH_EQUALS"
            BRANCH_GREATER_THAN -> "BRANCH_GREATER_THAN"
            RETURN -> "RETURN"
            BRANCH_GREATER_THAN_OR_EQUALS -> "BRANCH_GREATER_THAN_OR_EQUALS"
            PUSH_INT_LOCAL -> "PUSH_INT_LOCAL"
            POP_INT_LOCAL -> "POP_INT_LOCAL"
            GOSUB_WITH_PARAMS -> "GOSUB_WITH_PARAMS"
            BAS_READYANIM -> "BAS_READYANIM"
            BAS_TURNONSPOT -> "BAS_TURNONSPOT"
            BAS_WALK_B -> "BAS_WALK_B"
            BAS_WALK_F -> "BAS_WALK_F"
            MES -> "MES"
            P_FINDUID -> "P_FINDUID"
            STAFFMODLEVEL -> "STAFFMODLEVEL"
            UID -> "UID"
            P_ANIMPROTECT -> "P_ANIMPROTECT"
            INV_GETOBJ -> "INV_GETOBJ"
            INV_TOTAL -> "INV_TOTAL"
            else -> throw IllegalArgumentException("Unknown opcode $id")
        }
    }
}