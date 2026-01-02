package ext

object ArrayListExt {
    fun <T> ArrayList<T?>.ensure(count: Int) {
        clear()
        ensureCapacity(count)
        repeat(count) { add(null) }
    }

    fun <T> ArrayList<T?>.length(): Int {
        return size - 1
    }
}