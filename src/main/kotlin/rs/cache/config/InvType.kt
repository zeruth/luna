package rs.cache.config

import ext.ArrayListExt.ensure
import rs.io.Packet
import java.io.File

class InvType(id: Int) : ConfigType(id){
    companion object {
        private val dir = File("./data/pack/server/").toPath()

        private var configNames = HashMap<String, Int>()
        private var configs: Array<InvType?> = emptyArray()

        const val SCOPE_TEMP = 0;
        const val SCOPE_PERM = 1;
        const val SCOPE_SHARED = 2;

        // commonly referenced in-engine
        var INV = -1;
        var WORN = -1;

        fun load() {
            val dat = Packet.load(dir.resolve("inv.dat").toFile())
            parse(dat)
        }

        fun parse(dat: Packet) {
            configNames = HashMap()
            val count = dat.g2()
            configs = arrayOfNulls(count)

            for (id in 0 until count) {
                val config = InvType(id)
                config.decodeType(dat)

                configs[id] = config

                if (config.debugname != null) {
                    configNames[config.debugname!!] = id
                }
            }

            INV = getId("inv")!!
            WORN = getId("worn")!!

            println("Loaded $count InvTypes")
        }

        fun get(id: Int) = configs[id]

        fun getId(name: String) = configNames[name]

        fun count() : Int {
            return configs.size
        }
    }

    var scope = 0;
    var size = 1;
    var stackall = false;
    var restock = false;
    var allstock = false;
    var stockobj = ArrayList<Int?>()
    var stockcount = ArrayList<Int?>()
    var stockrate = ArrayList<Int?>()
    var protect = true;
    var runweight = false; // inv contributes to weight
    var dummyinv = false; // inv only accepts objs with dummyitem=inv_only

    override fun decode(code: Int, dat: Packet) {
        if (code == 1) {
            scope = dat.g1()
        } else if (code == 2) {
            size = dat.g2()
        } else if (code == 3) {
            stackall = true
        } else if (code == 4) {
            val count = dat.g1()

            stockobj.ensure(count)
            stockcount.ensure(count)
            stockrate.ensure(count)

            for (i in 0 until count) {
                stockobj[i] = dat.g2()
                stockcount[i] = dat.g2()
                stockrate[i] = dat.g4s()
            }
        } else if (code == 5) {
            restock = true
        } else if (code == 6) {
            allstock = true
        } else if (code == 7) {
            protect = true
        } else if (code == 8) {
            runweight = true
        } else if (code == 9) {
            dummyinv = true
        } else if (code == 250) {
            debugname = dat.gjstr()
        } else {
            throw IllegalArgumentException("Unrecognized inv config code: $code")
        }
    }
}