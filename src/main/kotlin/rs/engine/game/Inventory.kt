package rs.engine.game

import rs.cache.config.InvType

class Inventory(
    val type: Int,          // inv ID
    val capacity: Int,
    val stackType: Int = NORMAL_STACK
) {

    companion object {
        const val STACK_LIMIT = 0x7fffffff // Int.MAX_VALUE

        const val NORMAL_STACK = 0
        const val ALWAYS_STACK = 1
        const val NEVER_STACK = 2

        fun fromType(inv: Int): Inventory {
            if (inv == -1) {
                throw IllegalArgumentException("Invalid inventory type")
            }

            val typeDef = InvType.get(inv)!!

            var stackType = NORMAL_STACK
            if (typeDef.stackall) {
                stackType = ALWAYS_STACK
            }

            val container = Inventory(inv, typeDef.size, stackType)

            val stockObj = typeDef.stockobj
            val stockCount = typeDef.stockcount

            if (stockObj.isNotEmpty()) {
                for (i in stockObj.indices) {
                    container.set(
                        i,
                        Item(
                            id = stockObj[i]!!,
                            count = stockCount[i]!!
                        )
                    )
                }
            }

            return container
        }
    }

    val items: Array<Item?> = arrayOfNulls(capacity)

    var update: Boolean = false

    fun get(slot: Int): Item? {
        return items[slot]
    }

    fun set(slot: Int, item: Item?) {
        items[slot] = item
        update = true
    }

    fun validSlot(slot: Int) : Boolean{
        return slot >= 0 && slot < this.capacity;
    }

    fun getItemCount(id: Int): Int {
        var count = 0

        for (i in 0 until capacity) {
            val item = items[i]
            if (item != null && item.id == id) {
                count += item.count
            }
        }

        return minOf(STACK_LIMIT, count)
    }
}

class Item(val id: Int, val count: Int)
