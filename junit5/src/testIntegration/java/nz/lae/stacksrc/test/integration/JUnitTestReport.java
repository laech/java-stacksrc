package nz.lae.stacksrc.test.integration;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;

@XmlRootElement
class JUnitTestReport {
  @XmlElement TestCase testcase;

  static class TestCase {
    @XmlElement Failure failure;
  }

  static class Failure {
    @XmlAttribute String message;
    @XmlValue String stackTrace;
  }
}
