package day2

import AbstractQueueTest
import org.jetbrains.kotlinx.lincheck.annotations.*

class FAABasedQueueSimplifiedTest : AbstractQueueTest(FAABasedQueueSimplified())
class FAABasedQueueTest : AbstractQueueTest(FAABasedQueue())

class MSQueueWithOnlyLogicalRemoveTest : AbstractQueueWithRemoveTest(MSQueueWithOnlyLogicalRemove())
class MSQueueWithLinearTimeNonParallelRemoveTest : AbstractQueueWithNonParallelRemoveTest(MSQueueWithLinearTimeNonParallelRemove())
class MSQueueWithLinearTimeRemoveTest : AbstractQueueWithRemoveTest(MSQueueWithLinearTimeRemove())
class MSQueueWithConstantTimeRemoveTest : AbstractQueueWithRemoveTest(MSQueueWithConstantTimeRemove())

abstract class AbstractQueueWithRemoveTest(
    private val queue: QueueWithRemove<Int>,
    checkObstructionFreedom: Boolean = true
) : AbstractQueueTest(queue, checkObstructionFreedom) {
    @Operation
    fun remove(@Param(name = "element") element: Int) = queue.remove(element)

    @Validate
    fun checkNoRemovedElements() = queue.checkNoRemovedElements()
}

abstract class AbstractQueueWithNonParallelRemoveTest(
    private val queue: QueueWithRemove<Int>,
    checkObstructionFreedom: Boolean = true
) : AbstractQueueTest(queue, checkObstructionFreedom) {
    @Operation(nonParallelGroup = "remove")
    fun remove(@Param(name = "element") element: Int) = queue.remove(element)

    @Validate
    fun checkNoRemovedElements() = queue.checkNoRemovedElements()
}


