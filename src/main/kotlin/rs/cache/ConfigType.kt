package rs.cache

import rs.io.Packet

open class ConfigType(val id: Int) {
    var debugname: String? = null

    open fun decode(code: Int, dat: Packet) {}

    fun decodeType(dat: Packet) {
        while (dat.available() > 0) {
            val code = dat.g1()
            if (code == 0) {
                break
            }

            decode(code, dat)
        }
    }

    open fun postDecode() {}
}