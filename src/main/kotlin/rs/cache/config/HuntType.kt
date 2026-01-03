package rs.cache.config

import rs.cache.ConfigType
import rs.engine.entity.NpcMode
import rs.engine.entity.hunt.HuntCheckNotTooStrong
import rs.engine.entity.hunt.HuntModeType
import rs.engine.entity.hunt.HuntNobodyNear
import rs.engine.entity.hunt.HuntVis
import rs.io.Packet
import java.io.File

class HuntType(id: Int) : ConfigType(id){
    companion object {
        private val dir = File("./data/pack/server/").toPath()

        private var configNames = HashMap<String, Int>()
        private var configs: Array<HuntType?> = emptyArray()

        fun load() {
            val dat = Packet.load(dir.resolve("hunt.dat").toFile())
            parse(dat)
        }

        fun parse(dat: Packet) {
            val count = dat.g2()
            configs = arrayOfNulls(count)

            for (id in 0 until count) {
                val config = HuntType(id)
                config.decodeType(dat)

                configs[id] = config

                if (config.debugname != null) {
                    configNames[config.debugname!!] = id
                }
            }

            println("Loaded $count HuntTypes")
        }

        fun get(id: Int) = configs[id]

        fun getId(name: String) = configNames[name]

        fun getByName(name: String): HuntType? {
            val id = getId(name) ?: return null
            return get(id)
        }

        fun count() : Int {
            return configs.size
        }
    }

    fun checkHuntCondition(value: Int, condition: String, checkValue: Int): Boolean {
        when (condition) {
            ">" -> return value > checkValue
            "<" -> return value < checkValue
            "=" -> return value == checkValue
            "!" -> return value != checkValue
        }
        return false
    }

    var type = HuntModeType.OFF
    var checkVis = HuntVis.OFF
    var checkNotTooStrong = HuntCheckNotTooStrong.OFF
    var checkNotBusy = false
    var findKeepHunting = false
    var findNewMode = NpcMode.NONE
    var nobodyNear = HuntNobodyNear.PAUSEHUNT
    var checkNotCombat = -1
    var checkNotCombatSelf = -1
    var checkAfk = true
    var rate = 1
    var checkCategory = -1
    var checkNpc = -1
    var checkObj = -1
    var checkLoc = -1
    var checkInv = -1
    var checkObjParam = -1
    var checkInvCondition = ""
    var checkInvVal = -1

    class CheckVar(varId: Int, condition: String, value: Int)

    val checkVars = ArrayList<CheckVar>()


    override fun decode(code: Int, dat: Packet) {
        when (code) {
            1 -> type = HuntModeType.of(dat.g1())
            2 -> checkVis = HuntVis.of(dat.g1())
            3 -> checkNotTooStrong = HuntCheckNotTooStrong.of(dat.g1())
            4 -> checkNotBusy = true
            5 -> findKeepHunting = true
            6 -> findNewMode = dat.g1()
            7 -> nobodyNear = HuntNobodyNear.of(dat.g1())
            8 -> checkNotCombat = dat.g2()
            9 -> checkNotCombatSelf = dat.g2()
            10 -> checkAfk = false
            11 -> rate = dat.g2()
            12 -> checkCategory = dat.g2()
            13 -> checkNpc = dat.g2()
            14 -> checkObj = dat.g2()
            15 -> checkLoc = dat.g2()
            16 -> {
                checkInv = dat.g2()
                checkObj = dat.g2()
                checkInvCondition = dat.gjstr()
                checkInvVal = dat.g4s()
            }
            17 -> {
                checkInv = dat.g2()
                checkObjParam = dat.g2()
                checkInvCondition = dat.gjstr()
                checkInvVal = dat.g4s()
            }
            18,19,20 -> checkVars.add(CheckVar(dat.g2(), dat.gjstr(), dat.g4s()))
            250 -> debugname = dat.gjstr()
            else -> throw RuntimeException("Unhandled code $code")
        }
    }
}