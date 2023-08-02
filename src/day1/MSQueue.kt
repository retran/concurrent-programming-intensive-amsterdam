package day1

import kotlinx.atomicfu.*

class MSQueue<E> : Queue<E> {
    private val head: AtomicRef<Node<E>>
    private val tail: AtomicRef<Node<E>>

    init {
        val dummy = Node<E>(null)
        head = atomic(dummy)
        tail = atomic(dummy)
    }

    override fun enqueue(element: E) {
        TODO("implement me")
    }

    override fun dequeue(): E? {
        TODO("implement me")
    }

    // FOR TEST PURPOSE, DO NOT CHANGE IT.
    override fun validate() {
        check(tail.value.next.value == null) {
            "`tail.next` should be `null`"
        }
    }

    private class Node<E>(
        var element: E?
    ) {
        val next = atomic<Node<E>?>(null)
    }
}
