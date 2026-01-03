package rs.cache.config

import rs.cache.ConfigType
import rs.io.Packet
import java.io.File

class DbRowType(id: Int) : ConfigType(id){
    companion object {
        private val dir = File("./data/pack/server/").toPath()

        private var configNames = HashMap<String, Int>()
        private var configs: Array<DbRowType?> = emptyArray()

        fun load() {
            val dat = Packet.load(dir.resolve("dbrow.dat").toFile())
            parse(dat)
        }

        fun parse(dat: Packet) {
            val count = dat.g2()
            configs = arrayOfNulls(count)

            for (id in 0 until count) {
                val config = DbRowType(id)
                config.decodeType(dat)

                configs[id] = config

                if (config.debugname != null) {
                    configNames[config.debugname!!] = id
                }
            }

            println("Loaded $count DbRowTypes")
        }

        fun get(id: Int) = configs[id]

        fun getId(name: String) = configNames[name]

        fun getByName(name: String): DbRowType? {
            val id = getId(name) ?: return null
            return get(id)
        }

        fun count() : Int {
            return configs.size
        }
    }

    var tableId = 0
    var types: Array<Array<Int?>?> = emptyArray()
    var columnValues: Array<Array<Any?>?> = emptyArray()


    override fun decode(code: Int, dat: Packet) {
        when (code) {
            3 -> {
                val numColumns = dat.g1()
                types = arrayOfNulls(numColumns)
                columnValues = arrayOfNulls(numColumns)

                var columnId = dat.g1()
                while (columnId != 255) {
                    val columnTypes = arrayOfNulls<Int>(dat.g1())

                    for (i in 0 until columnTypes.size) {
                        columnTypes[i] = dat.g1();
                    }

                    types[columnId] = columnTypes
                    columnValues[columnId] = decodeValues(dat, columnId)
                    columnId = dat.g1()
                }
            }
            4 -> {
                tableId = dat.g2()
            }
            250 -> {
                debugname = dat.gjstr()
            }
            else -> throw RuntimeException("Unhandled code $code")
        }
    }

    fun decodeValues(dat: Packet, column: Int): Array<Any?> {
        val types = types[column] ?: return emptyArray()
        val fieldCount = dat.g1()
        val values = arrayOfNulls<Any>(fieldCount * types.size)

        for (fieldId in 0 until fieldCount) {
            for (typeId in 0 until types.size) {
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