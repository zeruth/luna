package rs.io

import rs.util.DoublyLinkable
import rs.util.LinkList
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

class Packet(val data: ByteArray, private val order: ByteOrder = ByteOrder.BIG_ENDIAN) : DoublyLinkable() {

    companion object {
        private const val CRC32_POLYNOMIAL: Int = 0xEDB88320.toInt()

        private val crctable: IntArray = IntArray(256)
        val bitmask: IntArray = IntArray(33)

        private val cacheMin: LinkList<Packet> = LinkList()
        private val cacheMid: LinkList<Packet> = LinkList()
        private val cacheMax: LinkList<Packet> = LinkList()
        private val cacheBig: LinkList<Packet> = LinkList()
        private val cacheHuge: LinkList<Packet> = LinkList()
        private val cacheUnimaginable: LinkList<Packet> = LinkList()

        private var cacheMinCount: Int = 0
        private var cacheMidCount: Int = 0
        private var cacheMaxCount: Int = 0
        private var cacheBigCount: Int = 0
        private var cacheHugeCount: Int = 0
        private var cacheUnimaginableCount: Int = 0

        init {
            // bitmask init
            for (i in 0 until 32) {
                bitmask[i] = (1 shl i) - 1
            }
            bitmask[32] = -1 // 0xffffffff

            // CRC table init
            for (i in 0 until 256) {
                var remainder = i

                for (bit in 0 until 8) {
                    remainder = if ((remainder and 1) == 1) {
                        (remainder ushr 1) xor CRC32_POLYNOMIAL
                    } else {
                        remainder ushr 1
                    }
                }

                crctable[i] = remainder
            }
        }

        fun getcrc(src: ByteArray, offset: Int, length: Int): Int {
            var crc = -1 // 0xffffffff

            var i = offset
            while (i < length) {
                crc = (crc ushr 8) xor crctable[(crc xor (src[i].toInt() and 0xFF)) and 0xFF]
                i++
            }

            return crc.inv()
        }

        fun checkcrc(src: ByteArray, offset: Int, length: Int, expected: Int = 0): Boolean {
            return getcrc(src, offset, length) == expected
        }

        fun load(file: File, seekToEnd: Boolean = false) : Packet {
            val packet = Packet(file.readBytes(), ByteOrder.BIG_ENDIAN)
            if (seekToEnd) {
                packet.position(packet.data.size)
            }
            return packet
        }
    }

    private val view = ByteBuffer.wrap(data).order(order)

    var bitPos = 0

    fun position() = view.position()

    @Suppress("HasPlatformType")
    fun position(position: Int) = view.position(position)

    fun move(positions: Int) {
        position(position() + positions)
    }

    fun length() : Int {
        return view.limit()
    }

    fun available() : Int {
        return view.remaining()
    }

    fun alloc(type: Int): Packet {
        var cached: Packet? = null

        if (type == 0 && cacheMinCount > 0) {
            cached = cacheMin.removeHead()
            cacheMinCount--
        } else if (type == 1 && cacheMidCount > 0) {
            cached = cacheMid.removeHead()
            cacheMidCount--
        } else if (type == 2 && cacheMaxCount > 0) {
            cached = cacheMax.removeHead()
            cacheMaxCount--
        } else if (type == 3 && cacheBigCount > 0) {
            cached = cacheBig.removeHead()
            cacheBigCount--
        } else if (type == 4 && cacheHugeCount > 0) {
            cached = cacheHuge.removeHead()
            cacheHugeCount--
        } else if (type == 5 && cacheUnimaginableCount > 0) {
            cached = cacheUnimaginable.removeHead()
            cacheUnimaginableCount--
        }

        if (cached != null) {
            cached.position(0)
            return cached
        }

        return when (type) {
            0 -> Packet(ByteArray(100))
            1 -> Packet(ByteArray(5_000))
            2 -> Packet(ByteArray(30_000))
            3 -> Packet(ByteArray(100_000))
            4 -> Packet(ByteArray(500_000))
            5 -> Packet(ByteArray(2_000_000))
            else -> Packet(ByteArray(type))
        }
    }

    fun release() {
        position(0)

        when (length()) {
            100 -> if (cacheMinCount < 1_000) {
                cacheMin.addTail(this)
                cacheMinCount++
            }

            5_000 -> if (cacheMidCount < 250) {
                cacheMid.addTail(this)
                cacheMidCount++
            }

            30_000 -> if (cacheMaxCount < 50) {
                cacheMax.addTail(this)
                cacheMaxCount++
            }

            100_000 -> if (cacheBigCount < 10) {
                cacheBig.addTail(this)
                cacheBigCount++
            }

            500_000 -> if (cacheHugeCount < 5) {
                cacheHuge.addTail(this)
                cacheHugeCount++
            }

            2_000_000 -> if (cacheUnimaginableCount < 2) {
                cacheUnimaginable.addTail(this)
                cacheUnimaginableCount++
            }
        }
    }


    fun g1(): Int {
        return view.get().toInt() and 0xFF
    }

    fun g1b(): Int {
        return view.get().toInt()
    }

    fun g2(): Int {
        return view.getShort().toInt() and 0xFFFF
    }

    fun g2s(): Int {
        return view.getShort().toInt()
    }

    fun g3(): Int {
        move(3)
        return ((data[position() - 3].toInt() and 0xFF) shl 16) or
                ((data[position() - 2].toInt() and 0xFF) shl 8) or
                (data[position() - 1].toInt() and 0xFF)
    }

    fun gbool() : Boolean {
        return g1() == 1
    }


    fun gjstr(terminator: Int = 10): String {
        var pos = position()
        val length = length()  // matches DataView.byteLength
        val sb = StringBuilder()
        var b: Int

        while (pos < length) {
            b = view.get(pos).toInt() and 0xFF
            pos++  // increment like pos++ in TS

            if (b == terminator) break  // stop before appending terminator
            sb.append(b.toChar())
        }
        position(pos)

        return sb.toString()
    }

    fun g4s(): Int {
        return view.getInt()
    }

    fun gsmart(): Int {
        return if ((data[position()].toInt() and 0xFF) < 0x80) {
            g1() - 0x40
        } else {
            g2() - 0xC000
        }
    }

    fun gdata(dest: ByteArray, offset: Int, length: Int) {
        System.arraycopy(data, position(), dest, offset, length)
        position(position() + length)
    }

}
