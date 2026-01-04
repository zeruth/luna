package rs.util

@Suppress("UNCHECKED_CAST")
class LinkList<T : Linkable> {

    private val sentinel: Linkable = Linkable()

    var cursor: Linkable? = null

    init {
        sentinel.next = sentinel
        sentinel.prev = sentinel
    }

    fun addTail(node: T) {
        if (node.prev != null) {
            node.unlink()
        }
        node.prev = sentinel.prev
        node.next = sentinel
        node.prev?.next = node
        node.next?.prev = node
    }

    fun addHead(node: T) {
        if (node.prev != null) {
            node.unlink()
        }
        node.prev = sentinel
        node.next = sentinel.next
        node.prev?.next = node
        node.next?.prev = node
    }

    fun removeHead(): T? {
        val node = sentinel.next as T?
        if (node === sentinel) {
            return null
        }
        node?.unlink()
        return node
    }

    fun head(): T? {
        val node = sentinel.next as T?
        if (node === sentinel) {
            cursor = null
            return null
        }
        cursor = node?.next
        return node
    }

    fun tail(): T? {
        val node = sentinel.prev as T?
        if (node === sentinel) {
            cursor = null
            return null
        }
        cursor = node?.prev
        return node
    }

    fun next(): T? {
        val node = cursor as T?
        if (node === sentinel) {
            cursor = null
            return null
        }
        cursor = node?.next
        return node
    }

    fun prev(): T? {
        val node = cursor as T?
        if (node === sentinel) {
            cursor = null
            return null
        }
        cursor = node?.prev
        return node
    }

    fun clear() {
        while (true) {
            val node = sentinel.next as T?
            if (node === sentinel) {
                return
            }
            node?.unlink()
        }
    }

    fun all(reverse: Boolean = false): Iterable<T> = Iterable {
        object : Iterator<T> {

            private var nextNode: T? =
                if (reverse) tail() else head()

            override fun hasNext(): Boolean = nextNode != null

            override fun next(): T {
                val current = nextNode
                    ?: throw NoSuchElementException()

                val save = cursor
                nextNode = if (reverse) prev() else next()
                cursor = save

                return current
            }
        }
    }
}
