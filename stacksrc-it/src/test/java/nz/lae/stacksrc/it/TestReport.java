package nz.lae.stacksrc.it;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
class TestReport {

  @XmlElement(name = "testcase")
  TestCase testCase;

  static class TestCase {
    @XmlElement(name = "failure")
    Failure failure;
  }

  static class Failure {
    @XmlAttribute(name = "message")
    String message;
  }
}
