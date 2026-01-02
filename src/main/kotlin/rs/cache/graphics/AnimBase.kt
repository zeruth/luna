package rs.cache.graphics

import rs.io.Packet

class AnimBase {

    var length: Int = 0
    var types: IntArray = IntArray(0)
    var labels: Array<IntArray> = emptyArray()

    companion object {
        val instances = ArrayList<AnimBase>()
        val order = ArrayList<String>() // kept for parity, unused here

        const val OP_BASE = 0
        const val OP_TRANSLATE = 1
        const val OP_ROTATE = 2
        const val OP_SCALE = 3
        const val OP_ALPHA = 5

        fun unpack(dat: Packet): Int {
            val length = dat.g1()

            val types = IntArray(length)
            val labels = Array(length) { IntArray(0) }

            for (i in 0 until length) {
                types[i] = dat.g1()
            }

            for (i in 0 until length) {
                val labelCount = dat.g1()
                val labelArray = IntArray(labelCount)
                for (j in 0 until labelCount) {
                    labelArray[j] = dat.g1()
                }
                labels[i] = labelArray
            }

            val base = AnimBase()
            base.length = length
            base.types = types
            base.labels = labels

            instances.add(base)
            return instances.size - 1
        }
    }
}
