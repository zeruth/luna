package rs.engine.entity

object NpcMode {
    // Default mode
    const val NULL = -1
    // Do nothing
    const val NONE = 0
    // Wander around the NPC's spawn point
    const val WANDER = 1
    // Patrol between a list of points
    const val PATROL = 2
    // Retreat from its target
    const val PLAYERESCAPE = 3
    // Follow its target
    const val PLAYERFOLLOW = 4
    // Face its target while within maxrange distance
    const val PLAYERFACE = 5
    // Face its target while within 1 tile distance
    const val PLAYERFACECLOSE = 6

    // Execute [ai_opplayerXnpc] script
    const val OPPLAYER1 = 7
    const val OPPLAYER2 = 8
    const val OPPLAYER3 = 9
    const val OPPLAYER4 = 10
    const val OPPLAYER5 = 11

    // Execute [ai_applayerXnpc] script
    const val APPLAYER1 = 12
    const val APPLAYER2 = 13
    const val APPLAYER3 = 14
    const val APPLAYER4 = 15
    const val APPLAYER5 = 16

    // Execute [ai_oplocXnpc] script
    const val OPLOC1 = 17
    const val OPLOC2 = 18
    const val OPLOC3 = 19
    const val OPLOC4 = 20
    const val OPLOC5 = 21

    // Execute [ai_aplocXnpc] script
    const val APLOC1 = 22
    const val APLOC2 = 23
    const val APLOC3 = 24
    const val APLOC4 = 25
    const val APLOC5 = 26

    // Execute [ai_opobjXnpc] script
    const val OPOBJ1 = 27
    const val OPOBJ2 = 28
    const val OPOBJ3 = 29
    const val OPOBJ4 = 30
    const val OPOBJ5 = 31

    // Execute [ai_apobjXnpc] script
    const val APOBJ1 = 32
    const val APOBJ2 = 33
    const val APOBJ3 = 34
    const val APOBJ4 = 35
    const val APOBJ5 = 36

    // Execute [ai_opnpcXnpc] script
    const val OPNPC1 = 37
    const val OPNPC2 = 38
    const val OPNPC3 = 39
    const val OPNPC4 = 40
    const val OPNPC5 = 41

    // Execute [ai_apnpcXnpc] script
    const val APNPC1 = 42
    const val APNPC2 = 43
    const val APNPC3 = 44
    const val APNPC4 = 45
    const val APNPC5 = 46

    // Execute the [ai_queueXnpc] script
    const val QUEUE1 = 47
    const val QUEUE2 = 48
    const val QUEUE3 = 49
    const val QUEUE4 = 50
    const val QUEUE5 = 51
    const val QUEUE6 = 52
    const val QUEUE7 = 53
    const val QUEUE8 = 54
    const val QUEUE9 = 55
    const val QUEUE10 = 56
    const val QUEUE11 = 57
    const val QUEUE12 = 58
    const val QUEUE13 = 59
    const val QUEUE14 = 60
    const val QUEUE15 = 61
    const val QUEUE16 = 62
    const val QUEUE17 = 63
    const val QUEUE18 = 64
    const val QUEUE19 = 65
    const val QUEUE20 = 66
}