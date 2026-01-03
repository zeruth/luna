package rs.cache.config

import rs.cache.ConfigType
import rs.io.Packet
import java.io.File

class CategoryType(id: Int) : ConfigType(id){
    companion object {
        private val dir = File("./data/pack/server/").toPath()

        private var configNames = HashMap<String, Int>()
        private var configs: Array<CategoryType?> = emptyArray()

        fun load() {
            val dat = Packet.load(dir.resolve("category.dat").toFile())
            parse(dat)
        }

        fun parse(dat: Packet) {
            val count = dat.g2()
            configs = arrayOfNulls(count)

            for (id in 0 until count) {
                val config = CategoryType(id)
                config.decodeType(dat)

                configs[id] = config

                if (config.debugname != null) {
                    configNames[config.debugname!!] = id
                }
            }

            println("Loaded $count CategoryTypes")
        }

        fun get(id: Int) = configs[id]

        fun getId(name: String) = configNames[name]

        fun getByName(name: String): CategoryType? {
            val id = getId(name) ?: return null
            return get(id)
        }

        fun count() : Int {
            return configs.size
        }
    }


    override fun decode(code: Int, dat: Packet) {
        if (code == 1)
            debugname = dat.gjstr();
        else throw RuntimeException("Unhandled code $code")
    }
}