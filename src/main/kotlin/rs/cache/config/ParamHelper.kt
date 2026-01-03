package rs.cache.config

import rs.io.Packet

typealias ParamMap = MutableMap<Int, Any>

interface ParamHolder {
    val params: ParamMap?
}

object ParamHelper {
    fun getStringParam(id: Int, holder: ParamHolder, defaultValue: String? = null): String {
        val value = holder.params?.get(id)
        return if (value is String) value else defaultValue ?: "null"
    }

    fun getIntParam(id: Int, holder: ParamHolder, defaultValue: Int): Int {
        val value = holder.params?.get(id)
        return if (value is Int) value else defaultValue
    }

    fun decodeParams(dat: Packet): ParamMap {
        val count = dat.g1()
        val params: ParamMap = mutableMapOf()

        repeat(count) {
            val key = dat.g3()
            val isString = dat.gbool()

            if (isString) {
                params[key] = dat.gjstr()
            } else {
                params[key] = dat.g4s()
            }
        }

        return params
    }
}
