// this file contains many package-visible classes that are used for identifying type-usages

public class TypeUsageTestData {
}

class CallMethodOnThis {
  void test7() {
  }

  void context() {
    this.test7(); // test calling a method on this
  }
}

class MethodParameter {
  void test1() {
  }

  void test2() {
  }

}

class LocalVariable {
  void cee() {
  }

  void test3() {
  }
}

class Field {
  void foo() {
  }

  void test4() {
  }
}

class Field42 {
  void foo42() {
  }

  void test42() {
  }
}

class A {
  void test3() {
  }
}

class Inheritance extends A {
  void cee() {
  }
}

class Main {
  Field instanceVar;
  Field42 instanceVar2;

  //	A instanceVar2;
  //	A instanceVar_test6; // new + method call
  //	
  public void context(MethodParameter x1, int x) {
    //		
    x1.test1(); // method parameter
    x1.test2(); // method parameter

    // local variables
    LocalVariable x2 = new LocalVariable();
    x2.test3();
    if (x < 10) {
      x2.cee(); // test if conditionals pose problems
    }

    Inheritance x24 = new Inheritance();
    x24.test3();
    if (x < 10) {
      x24.cee(); // test if conditionals pose problems
    }

    instanceVar.foo(); // cross-method field call

    instanceVar2.foo42(); // field
    instanceVar2.test42(); // field
  }

  public void context2(MethodParameter x1, int x) {
    instanceVar.test4(); // cross-method field call
    //				
    //		instanceVar2.test9(); // falsification test for test2
    //
    //		instanceVar_test6 = new A(); // falsification test
    //		instanceVar_test6.test6();
    //
    A x4 = null; // this gives a null type
    x4.test3();
    //		
    //		A x5 = x1; // local with a value from a method parameter
    //		x5.test5();// this is merged with x1, very good
    //
    //		new A() {
    //			public void internalMethod() {
    //				B x2 = new B(); // test anonymous class
    //			}
    //		};
    //
  }

  public void toto() {
    // should be impossible
    Object x4 = new Object(); // this gives a null type
    x4.hashCode();
    ((String) x4).substring(3);

    Object x5 = null; // this gives a null type
    x5.notify();
    x5.getClass();
    ((String) x5).matches("");

  }

}
