package nz.lae.stacksrc.test;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;

@SuppressWarnings("NullAway.Init")
@XmlRootElement
class JUnitTestReport {
  @XmlElement TestCase testcase;

  static class TestCase {
    @XmlElement Failure failure;
  }

  static class Failure {
    @XmlValue String stackTrace;
  }
}
