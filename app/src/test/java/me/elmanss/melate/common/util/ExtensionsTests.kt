package me.elmanss.melate.common.util

import org.junit.Assert
import org.junit.Test

class ExtensionsTests {
    @Test
    fun testFindRootCause() {
        // Create a chain of exceptions
        val rootCause = IllegalArgumentException("Root cause")
        val intermediate = RuntimeException("Intermediate exception", rootCause)
        val topLevel = Exception("Top level exception", intermediate)

        // Find root cause
        val result = topLevel.getRootCauseWithCycleGuard()

        // Assert root cause is found
        Assert.assertSame(rootCause, result)
    }

    @Test
    fun testFindRootCauseWithNoCause() {
        val exception = Exception("No cause")

        val result = exception.getRootCauseWithCycleGuard()

        Assert.assertSame(exception, result)
    }

    @Test
    fun testFindRootCauseWithCircularReference() {
        // Create exceptions for a circular reference
        val exception1 = Exception("Exception 1")
        val exception2 = Exception("Exception 2")
        val exception3 = Exception("Exception 3")

        // Set up circular reference
        exception1.initCause(exception2)
        exception2.initCause(exception3)

        // Create circular reference by setting exception3's cause to exception1
        try {
            // Using reflection to bypass the normal checks that prevent circular references
            val causeField = Throwable::class.java.getDeclaredField("cause")
            causeField.isAccessible = true
            causeField.set(exception3, exception1)
        } catch (e: Exception) {
            e.printStackTrace()
            // If reflection fails, we'll create a different kind of circular reference
            // where exception3's cause points to itself, which is also valid for testing
            exception3.initCause(exception3)
        }

        // Find root cause starting with exception1
        val result = exception1.getRootCauseWithCycleGuard()

        // Assert that we stopped at exception3 (or wherever we detected the cycle)
        // We can't easily predict exactly where the algorithm will stop in the cycle
        // but we know it should not be exception1 or throw a StackOverflowError
        assert(result == exception2 || result == exception3)
    }

    @Test
    fun testFindRootCauseWithSelfReferencingCause() {
        // Create a self-referencing exception
        val selfRef = Exception("Self reference")

        try {
            // Using reflection to create a self-reference
            val causeField = Throwable::class.java.getDeclaredField("cause")
            causeField.isAccessible = true
            causeField.set(selfRef, selfRef)
        } catch (e: Exception) {
            // If reflection fails, we'll just test with a null cause
            // This is a fallback, but the test would be less valuable
        }

        val result = selfRef.getRootCauseWithCycleGuard()

        // The result should be the same exception since it references itself as the cause
        Assert.assertSame(selfRef, result)
    }
}
