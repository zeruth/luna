package rs.cache.config

import rs.cache.ConfigType
import rs.io.Packet
import java.io.File

class EnumType(id: Int) : ConfigType(id){
    companion object {
        private val dir = File("./data/pack/server/").toPath()

        private var configNames = HashMap<String, Int>()
        private var configs: Array<EnumType?> = emptyArray()

        fun load() {
            val dat = Packet.load(dir.resolve("enum.dat").toFile())
            parse(dat)
        }

        fun parse(dat: Packet) {
            val count = dat.g2()
            configs = arrayOfNulls(count)

            for (id in 0 until count) {
                val config = EnumType(id)
                config.decodeType(dat)

                configs[id] = config

                if (config.debugname != null) {
                    configNames[config.debugname!!] = id
                }
            }

            println("Loaded $count EnumTypes")
        }

        fun get(id: Int) = configs[id]

        fun getId(name: String) = configNames[name]

        fun getByName(name: String): EnumType? {
            val id = getId(name) ?: return null
            return get(id)
        }

        fun count() : Int {
            return configs.size
        }
    }

    var inputtype = ScriptVarType.INT
    var outputtype = ScriptVarType.INT
    var defaultInt = 0
    var defaultString: String? = null
    val values = HashMap<Int, Any>()

    override fun decode(code: Int, dat: Packet) {
        when (code) {
            1 -> inputtype = dat.g1()
            2 -> outputtype = dat.g1()
            3 -> defaultString = dat.gjstr()
            4 -> defaultInt = dat.g4s()
            5 -> {
                val count = dat.g2()

                for (id in 0 until count) {
                    values[dat.g4s()] = dat.gjstr()
                }
            }
            6 -> {
                val count = dat.g2()

                for (id in 0 until count) {
                    values[dat.g4s()] = dat.g4s()
                }
            }
            250 -> debugname = dat.gjstr()
            else -> throw RuntimeException("Unhandled code $code")
        }
    }
}