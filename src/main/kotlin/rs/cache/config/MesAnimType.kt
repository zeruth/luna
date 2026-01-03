package rs.cache.config

import rs.cache.ConfigType
import rs.engine.entity.NpcMode
import rs.engine.entity.hunt.HuntCheckNotTooStrong
import rs.engine.entity.hunt.HuntModeType
import rs.engine.entity.hunt.HuntNobodyNear
import rs.engine.entity.hunt.HuntVis
import rs.io.Packet
import java.io.File

class MesAnimType(id: Int) : ConfigType(id){
    companion object {
        private val dir = File("./data/pack/server/").toPath()

        private var configNames = HashMap<String, Int>()
        private var configs: Array<MesAnimType?> = emptyArray()

        fun load() {
            val dat = Packet.load(dir.resolve("mesanim.dat").toFile())
            parse(dat)
        }

        fun parse(dat: Packet) {
            val count = dat.g2()
            configs = arrayOfNulls(count)

            for (id in 0 until count) {
                val config = MesAnimType(id)
                config.decodeType(dat)

                configs[id] = config

                if (config.debugname != null) {
                    configNames[config.debugname!!] = id
                }
            }

            println("Loaded $count MesAnimTypes")
        }

        fun get(id: Int) = configs[id]

        fun getId(name: String) = configNames[name]

        fun getByName(name: String): MesAnimType? {
            val id = getId(name) ?: return null
            return get(id)
        }

        fun count() : Int {
            return configs.size
        }
    }


    var len = Array(4) { -1 }

    override fun decode(code: Int, dat: Packet) {
        when (code) {
            1,2,3,4 -> len[code - 1] = dat.g2()
            250 -> debugname = dat.gjstr()
            else -> throw RuntimeException("Unhandled code $code")
        }
    }
}