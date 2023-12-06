package nz.lae.stacksrc

import nz.lae.stacksrc.test.Assertions.assertStackTraceHasExpectedPrefix
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class KotlinTest {

  private fun doThrow() {
    throw AssertionError("testing failure")
  }

  @Test
  fun run() {
    val exception = assertThrows(AssertionError::class.java, ::doThrow)
    val expected = """
java.lang.AssertionError: testing failure
	at nz.lae.stacksrc.KotlinTest.doThrow(KotlinTest.kt:10)

	    9    private fun doThrow() {
	-> 10      throw AssertionError("testing failure")
	   11    }

""".trimIndent()
    assertStackTraceHasExpectedPrefix(
      expected,
      StackTraceDecorator.get().decorate(exception)
    )
  }
}
