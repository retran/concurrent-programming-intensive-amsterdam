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
        val node = Node<E>(element)
        var localTail: Node<E>
        var next: Node<E>?
        while (true) {
            localTail = tail.value
            next = localTail.next.value
            if (localTail == tail.value) {
                if (next == null) {
                    if (localTail.next.compareAndSet(null, node)) {
                        break
                    }
                } else {
                    tail.compareAndSet(localTail, next)
                }
            }
        }
        tail.compareAndSet(localTail, node)
    }

    override fun dequeue(): E? {
        var localHead: Node<E>
        var localTail: Node<E>
        var next: Node<E>?
        var value: E?
        while (true) {
            localHead = head.value
            localTail = tail.value
            next = localHead.next.value
            if (localHead == head.value) {
                if (localHead == localTail) {
                    if (next == null)
                    {
                        return null
                    }
                    tail.compareAndSet(localTail, next)
                }
                else {
                    value = next?.element
                    if (next != null) {
                        if (head.compareAndSet(localHead, next)) {
                            break;
                        }
                    }
                }
            }
        }
        return value
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
