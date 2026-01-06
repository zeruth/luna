package rs.cache.config

import rs.cache.ConfigType
import rs.engine.entity.BlockWalk
import rs.engine.entity.MoveRestrict
import rs.engine.entity.NpcMode
import rs.engine.entity.NpcStat
import rs.io.JagFile
import rs.io.Packet
import java.io.File

class IdkType(id: Int) : ConfigType(id){
    companion object {
        private val dir = File("./data/pack/").toPath()

        private var configNames = HashMap<String, Int>()
        private var configs: Array<IdkType?> = emptyArray()

        fun load() {
            val server = Packet.load(dir.resolve("server/idk.dat").toFile())
            val jag = JagFile.load(dir.resolve("client/config").toFile())
            parse(server, jag)
        }

        fun parse(server: Packet, jag: JagFile) {
            val count = server.g2()
            val client = jag.read("idk.dat")!!
            client.position(2)

            configs = arrayOfNulls(count)

            for (id in 0 until count) {
                val config = IdkType(id)
                config.decodeType(server)
                config.decodeType(client)

                configs[id] = config

                if (config.debugname != null) {
                    configNames[config.debugname!!] = id
                }
            }

            println("Loaded ${configs.size} IdkTypes")
        }

        fun get(id: Int) = configs[id]

        fun getId(name: String) = configNames[name]

        fun getByName(name: String): IdkType? {
            val id = getId(name) ?: return null
            return get(id)
        }

        fun count() : Int {
            return configs.size
        }
    }

    var type = -1
    var models: Array<Int?>? = null
    var heads = arrayOfNulls<Int>(5).apply { for (i in indices) this[i] = -1 }
    var recol_s = arrayOfNulls<Int>(6).apply { for (i in indices) this[i] = 0 }
    var recol_d = arrayOfNulls<Int>(6).apply { for (i in indices) this[i] = 0 }
    var disable = false

    override fun decode(code: Int, dat: Packet) {
        when (code) {
            1 -> type = dat.g1()
            2 -> {
                val count = dat.g1()
                models = arrayOfNulls(count)

                for (i in 0 until count) {
                    models!![i] = dat.g2()
                }
            }
            3 -> disable = true
            40,41,42,43,44,45,46,47,48,49 -> recol_s[code - 40] = dat.g2()
            50,51,52,53,54,55,56,57,58,59 -> recol_d[code - 50] = dat.g2()
            60,61,62,63,64,65,66,67,68,69 -> heads[code - 60] = dat.g2()
            250 -> debugname = dat.gjstr()
            else -> throw RuntimeException("Unhandled code $code")
        }
    }
}