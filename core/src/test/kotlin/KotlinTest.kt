package nz.lae.stacksrc.core

import nz.lae.stacksrc.StackTraceDecorator
import nz.lae.stacksrc.test.Assertions.assertStackTrace
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class KotlinTest {

  private fun doThrow() {
    throw AssertionError("testing failure")
  }

  @Test
  fun run() {
    val exception = assertThrows(AssertionError::class.java, ::doThrow)
    val expected =
      """
java.lang.AssertionError: testing failure
	at nz.lae.stacksrc.core.KotlinTest.doThrow(KotlinTest.kt:11)

	   10    private fun doThrow() {
	-> 11      throw AssertionError("testing failure")
	   12    }

"""
        .trimIndent()
    assertStackTrace(expected, StackTraceDecorator.create().decorate(exception))
  }
}
