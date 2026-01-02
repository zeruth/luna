package rs.cache.config

import rs.io.Packet
import rs.cache.config.ScriptVarType
import java.io.File

class DbTableType(id: Int) : ConfigType(id){
    companion object {
        private val dir = File("./data/pack/server/").toPath()

        private var configNames = HashMap<String, Int>()
        private var configs: Array<DbTableType?> = emptyArray()

        fun load() {
            val dat = Packet.load(dir.resolve("dbtable.dat").toFile())
            parse(dat)
        }

        fun parse(dat: Packet) {
            val count = dat.g2()
            configs = arrayOfNulls(count)

            for (id in 0 until count) {
                val config = DbTableType(id)
                config.decodeType(dat)

                configs[id] = config

                if (config.debugname != null) {
                    configNames[config.debugname!!] = id
                }
            }

            println("Loaded $count DbTableTypes")
        }

        fun get(id: Int) = configs[id]

        fun getId(name: String) = configNames[name]

        fun count() : Int {
            return configs.size
        }
    }

    var types: Array<Array<Int>?> = emptyArray()
    var defaultValues: Array<Array<Any?>?>? = emptyArray()
    var columnNames: Array<String?> = emptyArray()
    var props: Array<Int> = emptyArray()

    override fun decode(code: Int, dat: Packet) {
        if (code == 1) {
            types = arrayOfNulls(dat.g1())

            var setting = dat.g1()

            while (setting != 255) {
                val column = setting and 0x7F
                val hasDefault = (setting and 0x80) != 0

                val columnTypes = IntArray(dat.g1())
                for (i in columnTypes.indices) {
                    columnTypes[i] = dat.g1()
                }
                types[column] = columnTypes.toTypedArray()

                if (hasDefault) {
                    if (defaultValues == null) {
                        defaultValues = arrayOfNulls(types.size)
                    }
                    defaultValues!![column] = decodeValues(dat, column)
                }

                setting = dat.g1()
            }
        } else if (code == 250) {
            this.debugname = dat.gjstr();
        } else if (code == 251) {
            columnNames = arrayOfNulls(dat.g1())

            for (i in columnNames.indices) {
                columnNames[i] = dat.gjstr()
            }
        } else if (code == 252) {
            val size = dat.g1()
            props = IntArray(size).toTypedArray()

            for (i in props.indices) {
                props[i] = dat.g1()
            }
        } else {
            throw IllegalArgumentException("Unrecognized dbtable config code: $code")
        }
    }

    fun decodeValues(dat: Packet, column: Int): Array<Any?> {
        val types = this.types[column]!!
        val fieldCount = dat.g1()
        val values = arrayOfNulls<Any>(fieldCount * types.size)

        for (fieldId in 0 until fieldCount) {
            for (typeId in types.indices) {
                val type = types[typeId]
                val index = typeId + fieldId * types.size

                values[index] = if (type == ScriptVarType.STRING) {
                    dat.gjstr()
                } else {
                    dat.g4s()
                }
            }
        }

        return values
    }

}