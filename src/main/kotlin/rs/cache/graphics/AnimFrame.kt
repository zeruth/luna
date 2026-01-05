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
        val instances = mutableMapOf<Int, AnimFrame>()
        val order = ArrayList<Int>()

        fun load() {
            val count = OnDemand.cache.count(2)
            for (i in 0 until count) {
                val data = OnDemand.cache.read(2, i, true)
                if (data != null) {
                    unpack(data)
                }
            }

            println("Loaded ${instances.values.size} Animation Frames")
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
            val tmpSize = 500
            val basesTmp = IntArray(tmpSize)
            val xTmp = IntArray(tmpSize)
            val yTmp = IntArray(tmpSize)
            val zTmp = IntArray(tmpSize)

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
                                basesTmp[length] = cur
                                xTmp[length] = 0
                                yTmp[length] = 0
                                zTmp[length] = 0
                                length++
                                break
                            }
                            cur--
                        }
                    }

                    basesTmp[length] = group
                    val defaultValue = if (AnimBase.instances[baseId].types[group] == AnimBase.OP_SCALE) 128 else 0

                    xTmp[length] = if ((flags and 0x1) != 0) tran2.gsmart() else defaultValue
                    yTmp[length] = if ((flags and 0x2) != 0) tran2.gsmart() else defaultValue
                    zTmp[length] = if ((flags and 0x4) != 0) tran2.gsmart() else defaultValue

                    lastGroup = group
                    length++
                }

                frame.length = length
                frame.groups = basesTmp.copyOf(length)
                frame.x = xTmp.copyOf(length)
                frame.y = yTmp.copyOf(length)
                frame.z = zTmp.copyOf(length)

                instances[id] = frame
            }
        }
    }
}
