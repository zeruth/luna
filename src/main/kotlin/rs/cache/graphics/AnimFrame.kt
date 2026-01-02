package rs.cache.graphics

import rs.engine.OnDemand
import rs.io.Packet

class AnimFrame {

    var delay: Int = 0
    var base: Int = 0
    var length: Int = 0

    var groups: IntArray = IntArray(0)
    var x: IntArray = IntArray(0)
    var y: IntArray = IntArray(0)
    var z: IntArray = IntArray(0)

    companion object {
        var instances = emptyArray<AnimFrame?>()
        val order = ArrayList<Int>()

        fun load() {
            instances = arrayOfNulls(/* grow as needed */ 10000)

/*            val count = OnDemand.cache.count(2)
            for (i in 0 until count) {
                val data = OnDemand.cache.read(2, i, true)
                if (data != null) {
                    unpack(data)
                }
            }*/
        }

        fun unpack(src: ByteArray) {
            val meta = Packet(src)
            meta.position(src.size - 8)

            var offset = 0

            val head = Packet(src)
            head.position(offset)
            offset += meta.g2() + 2

            val tran1 = Packet(src)
            tran1.position(offset)
            offset += meta.g2()

            val tran2 = Packet(src)
            tran2.position(offset)
            offset += meta.g2()

            val del = Packet(src)
            del.position(offset)
            offset += meta.g2()

            val baseData = Packet(src)
            baseData.position(offset)
            val baseId = AnimBase.unpack(baseData)

            val total = head.g2()

            val bases = IntArray(500)
            val xTmp = IntArray(500)
            val yTmp = IntArray(500)
            val zTmp = IntArray(500)

            for (i in 0 until total) {
                val id = head.g2()
                order.add(id)

                val frame = AnimFrame()
                frame.delay = del.g1()
                frame.base = baseId

                val groupCount = head.g1()
                var lastGroup = -1
                var length = 0

                for (group in 0 until groupCount) {
                    val flags = tran1.g1()
                    if (flags == 0) continue

                    if (AnimBase.instances[baseId].types[group] != AnimBase.OP_BASE) {
                        var cur = group - 1
                        while (cur > lastGroup) {
                            if (AnimBase.instances[baseId].types[cur] == AnimBase.OP_BASE) {
                                bases[length] = cur
                                xTmp[length] = 0
                                yTmp[length] = 0
                                zTmp[length] = 0
                                length++
                                break
                            }
                            cur--
                        }
                    }

                    bases[length] = group

                    var defaultValue = 0
                    if (AnimBase.instances[baseId].types[group] == AnimBase.OP_SCALE) {
                        defaultValue = 128
                    }

                    xTmp[length] =
                        if ((flags and 0x1) != 0) tran2.gsmart() else defaultValue
                    yTmp[length] =
                        if ((flags and 0x2) != 0) tran2.gsmart() else defaultValue
                    zTmp[length] =
                        if ((flags and 0x4) != 0) tran2.gsmart() else defaultValue

                    lastGroup = group
                    length++
                }

                frame.length = length
                frame.groups = IntArray(length)
                frame.x = IntArray(length)
                frame.y = IntArray(length)
                frame.z = IntArray(length)

                for (j in 0 until length) {
                    frame.groups[j] = bases[j]
                    frame.x[j] = xTmp[j]
                    frame.y[j] = yTmp[j]
                    frame.z[j] = zTmp[j]
                }

                if (id >= instances.size) {
                    // optional: resize if you want safety
                }
                instances[id] = frame
            }
        }
    }
}
