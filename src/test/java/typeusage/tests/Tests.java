package typeusage.tests;


import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import typeusage.miner.Main;
import typeusage.miner.TypeUsage;

public class Tests {

  static TestTypeUsageCollector c;
  
  @BeforeClass
  public static  void setup() throws Exception {
    c = new TestTypeUsageCollector();
    c.setDirToProcess(Main.DEFAULT_DIR);
    c.run();
  }
  
  // utility method
  private TypeUsage tu() {
    return new TypeUsage();
  }
  
  @Test 
  public void testCallingMethodOnThis() {
    assertTrue(c.data.contains(tu().type("CallMethodOnThis").call("test7()")));
//    assertEquals("type:CallMethodOnThis2 call:<init>()", c.test.get("CallMethodOnThis2").toString());
  }

  @Test 
  public void testCallingMethodOnNull() {
    assertTrue(c.data.contains(tu().type("A").call("test3()")));
  }

  @Test 
  public void testCallingMethodOnMethodParameter() {
    assertTrue(c.data.contains(tu().type("MethodParameter").call("test1()").call("test2()")));
  }

  @Test 
  public void testCallingMethodOnLocalVariable() {
    assertTrue(c.data.contains(tu().type("LocalVariable").call("<init>()").call("cee()").call("test3()")));
  }

  @Test 
  public void testTypeUsageWithInheritance() {
    assertTrue(c.data.contains(tu().type("Inheritance").call("<init>()").call("cee()").call("test3()")));
  }

  @Test 
  public void testTypeUsageField() {
    // cross-method method calls on a field
    assertTrue(c.data.contains(tu().type("Field").call("foo()").call("test4()")));
    
    // same-method method calls on a field
    assertTrue(c.data.contains(tu().type("Field42").call("foo42()").call("test42()")));
  }
  
  @Test 
  public void testTypeUsageCast() {
    // the type-usage collector must correctly handle casts
    assertTrue(c.data.contains(tu().type("java.lang.Object").call("<init>()").call("hashCode()")));
    assertTrue(c.data.contains(tu().type("java.lang.String").call("substring()")));
    
    assertTrue(c.data.contains(tu().type("java.lang.Object").call("notify()").call("getClass()")));
    assertTrue(c.data.contains(tu().type("java.lang.String").call("matches()")));

  }

  @AfterClass
  public static  void after() {
    System.out.println(c.data);
  }
}
