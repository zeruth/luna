package rs.cache.config

import rs.cache.ConfigType
import rs.engine.entity.NpcMode
import rs.engine.entity.hunt.HuntCheckNotTooStrong
import rs.engine.entity.hunt.HuntModeType
import rs.engine.entity.hunt.HuntNobodyNear
import rs.engine.entity.hunt.HuntVis
import rs.io.Packet
import java.io.File

class ParamType(id: Int) : ConfigType(id){
    companion object {
        private val dir = File("./data/pack/server/").toPath()

        private var configNames = HashMap<String, Int>()
        private var configs: Array<ParamType?> = emptyArray()

        fun load() {
            val dat = Packet.load(dir.resolve("param.dat").toFile())
            parse(dat)
        }

        fun parse(dat: Packet) {
            val count = dat.g2()
            configs = arrayOfNulls(count)

            for (id in 0 until count) {
                val config = ParamType(id)
                config.decodeType(dat)

                configs[id] = config

                if (config.debugname != null) {
                    configNames[config.debugname!!] = id
                }
            }

            println("Loaded $count ParamTypes")
        }

        fun get(id: Int) = configs[id]

        fun getId(name: String) = configNames[name]

        fun getByName(name: String): ParamType? {
            val id = getId(name) ?: return null
            return get(id)
        }

        fun count() : Int {
            return configs.size
        }
    }



    var type = ScriptVarType.INT
    var defaultInt = -1
    var defaultString: String? = null
    var autodisable = true

    override fun decode(code: Int, dat: Packet) {
        when (code) {
            1 -> type = dat.g1()
            2 -> defaultInt = dat.g4s()
            4 -> autodisable = false
            5 -> defaultString = dat.gjstr()
            250 -> debugname = dat.gjstr()
            else -> throw RuntimeException("Unhandled code $code")
        }
    }
}