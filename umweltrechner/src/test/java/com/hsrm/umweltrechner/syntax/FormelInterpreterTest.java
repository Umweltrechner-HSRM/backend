package com.hsrm.umweltrechner.syntax;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThrows;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.hsrm.umweltrechner.exceptions.interpreter.DivideByZeroException;
import com.hsrm.umweltrechner.exceptions.interpreter.DomainException;
import com.hsrm.umweltrechner.exceptions.interpreter.IllegalWriteException;
import com.hsrm.umweltrechner.exceptions.interpreter.IncorrectSyntaxException;
import com.hsrm.umweltrechner.exceptions.interpreter.InterpreterException;
import com.hsrm.umweltrechner.exceptions.interpreter.InvalidSymbolException;
import com.hsrm.umweltrechner.exceptions.interpreter.OutOfRangeException;
import com.hsrm.umweltrechner.exceptions.interpreter.UnknownSymbolException;

@SpringBootTest
public class FormelInterpreterTest {
  private final HashMap<String, Double> result = new HashMap<>();
  private FormelInterpreter f;
  private String formel;

  @Before
  public void setUp() {
    f = new FormelInterpreter();
  }

  public FormelInterpreterTest(){}

  @Test
  public void TestZeroDivision() throws UnknownSymbolException,
      IncorrectSyntaxException, IllegalWriteException {
    formel = """
        x = 5 / (1.5 / 3 - 0.5)
        """;
    f.setEquations(formel);

    Exception ex = assertThrows(DivideByZeroException.class, () -> {
      f.calculate();
    });
  }

  @Test
  public void TestMinValue() throws InterpreterException {
    formel = """
        x = s1 * 2
        x = x / 2
        """;
    f.addSensor("s1", Double.MIN_VALUE * 2);
    f.setEquations(formel);
    f.calculate();

    assertEquals(Double.MIN_VALUE * 2, f.getVariable("x"), 0.0);
  }

  @Test
  public void TestMinValueOutOfRange() throws UnknownSymbolException,
      IncorrectSyntaxException, IllegalWriteException, OutOfRangeException, InvalidSymbolException {
    formel = """
        x = s1 / 2
        """;
    Exception ex = assertThrows(OutOfRangeException.class, () -> {
      f.addSensor("s1", Double.MIN_VALUE);
    });

    f.addSensor("s1", Double.MIN_VALUE * 2);
    f.setEquations(formel);

    ex = assertThrows(OutOfRangeException.class, () -> {
      f.calculate();
    });
  }

  @Test
  public void TestMaxValue() throws InterpreterException {
    formel = """
        x = s1 / 2
        x = x * 2
        """;
    f.addSensor("s1", Double.MAX_VALUE / 2);
    f.setEquations(formel);
    f.calculate();

    assertEquals(Double.MAX_VALUE / 2, f.getVariable("x"), 0.0);
  }

  @Test
  public void TestMaxValueOutOfRange() throws UnknownSymbolException,
      IncorrectSyntaxException, IllegalWriteException, OutOfRangeException, InvalidSymbolException {
    formel = """
        x = s1 * 2
        """;
    Exception ex = assertThrows(OutOfRangeException.class, () -> {
      f.addSensor("s1", Double.MAX_VALUE);
    });

    f.addSensor("s1", Double.MAX_VALUE / 2);
    f.setEquations(formel);

    ex = assertThrows(OutOfRangeException.class, () -> {
      f.calculate();
      System.out.println(f.getVariables());
      System.out.println(Double.MAX_VALUE);
    });
  }

  @Test
  public void InvalidSensorName() throws InvalidSymbolException {
    Exception ex = assertThrows(InvalidSymbolException.class, () -> {
      f.addSensor("", 10);
    });

    ex = assertThrows(InvalidSymbolException.class, () -> {
      f.addSensor(" ", 10);
    });

    ex = assertThrows(InvalidSymbolException.class, () -> {
      f.addSensor("1sensor", 10);
    });

    ex = assertThrows(InvalidSymbolException.class, () -> {
      f.addSensor("sensor 1", 10);
    });

    ex = assertThrows(InvalidSymbolException.class, () -> {
      f.addSensor(" sensor1", 10);
    });

    ex = assertThrows(InvalidSymbolException.class, () -> {
      f.addSensor("sensor1 ", 10);
    });

    ex = assertThrows(InvalidSymbolException.class, () -> {
      f.addSensor("s€nsor1", 10);
    });
  }

  @Test
  public void EmptyString() throws InterpreterException {
    formel = "";
    f.setEquations(formel);
    f.calculate();

    assertEquals("leerer String", result, f.getVariables());
  }


  @Test
  public void EasyAssignment1() throws InterpreterException {
    formel = "x = 2";
    f.setEquations(formel);
    f.calculate();
    result.put("x", 2.0);

    assertEquals("Einfache Zuweisung", result, f.getVariables());
  }


  @Test
  public void EasyAssignment2() throws InterpreterException {
    formel = """
        x = 1
        Y = 3
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("Y", 3.0);
    result.put("x", 1.0);

    assertEquals("Einfache Zuweisung", result, f.getVariables());
  }


  @Test
  public void EasyCalculating1() throws InterpreterException {
    formel = """
        x = 3
        _y = x + 1
        z = _y * _y + (1/2)
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("z", 16.5);
    result.put("x", 3.0);
    result.put("_y", 4.0);

    assertEquals("Einfache Rechnung", result, f.getVariables());
  }


  @Test
  public void EasyCalculating2() throws InterpreterException {
    formel = "a = 1.5 * 2 + 1.5";
    f.setEquations(formel);
    f.calculate();
    result.put("a", 4.5);

    assertEquals("Einfache Rechnung", result, f.getVariables());
  }


  @Test
  public void CalculateWithSigns1() throws InterpreterException {
    formel = """
        d = -2 * 3 + +2
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("d", -4.0);

    assertEquals("Einfache Rechnung mit Vorzeichen", result, f.getVariables());
  }


  @Test
  public void CalculateWithSigns2() throws InterpreterException {
    formel = """
        d = -1 * -1 * -1 * -1 + 5
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("d", 6.0);

    assertEquals("Einfache Rechnung mit Vorzeichen", result, f.getVariables());
  }


  @Test
  public void CalculateWithSigns3() throws InterpreterException {
    formel = """
        d = -1 * - (2 + 5)
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("d", 7.0);

    assertEquals("Einfache Rechnung mit Vorzeichen", result, f.getVariables());
  }


  @Test
  public void EasyEquation1() throws InterpreterException {
    formel = """
        y = 2
        e = 1 + 2 + 3 * 4 * y
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("y", 2.0);
    result.put("e", 27.0);

    assertEquals("Einfache Gleichung mit Klammersetzung", result, f.getVariables());
  }


  @Test
  public void EasyEquation2() throws InterpreterException {
    formel = """
        e = 1 + 2 + 3 * 4 * 4 * 3 * 2
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("e", 291.0);

    assertEquals("Einfache Gleichung mit Klammersetzung", result, f.getVariables());
  }


  @Test
  public void DifficultEquation1() throws InterpreterException {
    formel = "x = 2 - (2-3) * (2 - 4) * (2 - 12)";
    f.setEquations(formel);
    f.calculate();
    result.put("x", 22.0);

    assertEquals("Schwerere Gleichung mit Klammersetzung", result, f.getVariables());
  }


  @Test
  public void DifficultEquation2() throws InterpreterException {
    formel = """
        y= 10
        x = 2 + (2 - 4) * (5 - 1000 / y) + 23
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("x", 215.0);
    result.put("y", 10.0);
    assertEquals("Schwerere Gleichung mit Klammersetzung", result, f.getVariables());
  }


  @Test
  public void Comment1() throws InterpreterException {
    formel = " //Kommentar";
    f.setEquations(formel);
    f.calculate();

    assertEquals("leere Formel mit nur Kommentar", result, f.getVariables());
  }

  @Test
  public void Comment2() throws InterpreterException {
    formel = "x  = 5 // Noch ein Kommentar //Hallo";
    f.setEquations(formel);
    f.calculate();
    result.put("x", 5.0);

    assertEquals("Zuweisung mit 2 Kommentaren", result, f.getVariables());
  }

  @Test
  public void Comment3() throws InterpreterException {
    formel = """
        x=5 //comment
        y=6
        //comment
        //another comment
        z=   12
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("x", 5.0);
    result.put("y", 6.0);
    result.put("z", 12.0);

    assertEquals("Mehrere Zuweisungen mit Kommentaren", result, f.getVariables());
  }

  @Test
  public void Goto1() throws InterpreterException {
    formel = """
        x= 5
        y= 6
        goto 1 //einfaches goto
        z= 12
        :1
        t= 3
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("x", 5.0);
    result.put("y", 6.0);
    result.put("t", 3.0);

    assertEquals("Einfaches goto, Zuweisung wird uebersprungen", result, f.getVariables());
  }

  @Test
  public void Goto2() throws InterpreterException {
    formel = """
        x= 5
        y= 6
        z= 1
        goto label
        z= 100  // wird übersprungen
        :label
        t= z + x + y // 1 + 5 + 6
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("x", 5.0);
    result.put("y", 6.0);
    result.put("z", 1.0);
    result.put("t", 12.0);

    assertEquals("Einfaches goto, Wert von z wird nicht veraendert", result, f.getVariables());
  }

  @Test
  public void Goto3() throws InterpreterException {
    formel = """
        x= 5
        y= 7
        goto label1
        z= 100  // wird übersprungen
        :label1
        t= x + y
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("x", 5.0);
    result.put("z", 100.0);
    result.put("t", 12.0);

    assertNotEquals("Einfaches goto, Wert von z wird nicht gesetzt", result, f.getVariables());
  }

  @Test
  public void IfGoto1() throws InterpreterException {
    formel = """
        x= 10
        y= 5
        if x < y goto l1  //false
        y= y + x         //geht hier rein
        :l1
        z= 10
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("x", 10.0);
    result.put("y", 15.0);
    result.put("z", 10.0);

    assertEquals("if -> comparison ->", result, f.getVariables());
  }

  @Test
  public void IfGoto2() throws InterpreterException {
    formel = """
        x1= 10
        x2= 2
        x3= 100
        if x1 > x2 goto l1  //true
        x4= 222            //skip //wird nicht definiert
        x5= 223            //skip //wird nicht definiert
        :l1
        x1= x1 + 100
        x2= x2 + 120
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("x1", 110.0);
    result.put("x2", 122.0);
    result.put("x3", 100.0);

    assertEquals("if-Abfragen", result, f.getVariables());
  }


  @Test
  public void IfGoto3() throws InterpreterException {
    formel = """
        x= 8
        y= 6
        if y <= x goto l1  //true
        y= y + x        //wird übersprungen
        :l1
        z= 10
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("x", 8.0);
    result.put("y", 6.0);
    result.put("z", 10.0);

    assertEquals("if-Abfragen", result, f.getVariables());
  }


  @Test
  public void IfGoto4() throws InterpreterException {
    formel = """
        x= 5
        y= 5
        if y == x goto l1       //true
        y= y - x               //skip
        :l1
        z= 10
        if z != 11 goto l2    //true
        z= 110               //skip
        :l2
        x= x + 1
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("x", 6.0);
    result.put("y", 5.0);
    result.put("z", 10.0);

    assertEquals("if-Abfragen mit Goto Equals NotEquals", result, f.getVariables());
  }


  @Test
  public void IfGoto5() throws InterpreterException {
    formel = """
        x= 10
        y= 10
        if y <= x goto l1  // true
        y= y + x         // skip
        :l1
        z= 10
        if z >= 10 goto l2 //true
        a= 1000
        :l2
        x= x + 1
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("x", 11.0);
    result.put("y", 10.0);
    result.put("z", 10.0);

    assertEquals("if-Abfragen", result, f.getVariables());
  }

  @Test
  public void UnknownVariable1() throws UnknownSymbolException,
      IncorrectSyntaxException, IllegalWriteException {
    formel = """
        x = 2 + (2 - 4) * (5 - 1000 / y) + 23
        """;
    f.setEquations(formel);

    Exception exception = assertThrows(UnknownSymbolException.class, f::calculate);
    assertEquals("UnknownSymbolException: Symbol 'y' is unknown or has not been initialized (line" +
        " " + 1 + ", character " + 32 + ")", exception.getMessage());
  }


  @Test
  public void IncorrectSyntax1() throws UnknownSymbolException, IncorrectSyntaxException,
      IllegalWriteException {
    formel = """
        x := 5
        """;
    f.setEquations(formel);

    Exception exception = assertThrows(IncorrectSyntaxException.class, f::calculate);
    assertEquals("IncorrectSyntaxException: Invalid assignment operator (line " + 1 + ", " +
        "character " + 3 + ")", exception.getMessage());
  }


  @Test
  public void IncorrectSyntax2() throws UnknownSymbolException, IncorrectSyntaxException,
      IllegalWriteException {
    formel = """
        x:= 5
        y = 100 //wird nicht gelesen
        """;
    f.setEquations(formel);

    Exception exception = assertThrows(IncorrectSyntaxException.class, f::calculate);
    assertEquals("IncorrectSyntaxException: Invalid assignment operator (line " + 1 + ", " +
        "character " + 2 + ")", exception.getMessage());
  }

  @Test
  public void IncorrectSyntax3() throws UnknownSymbolException, IncorrectSyntaxException,
      IllegalWriteException {
    formel = """
        -x = 5
        """;
    f.setEquations(formel);

    Exception exception = assertThrows(IncorrectSyntaxException.class, f::calculate);
    assertEquals("IncorrectSyntaxException: Unexpected character '-' (line " + 1 + ", character " + 1 + ")", exception.getMessage());
  }

  @Test
  public void IncorrectSyntax4() throws UnknownSymbolException, IncorrectSyntaxException,
      IllegalWriteException {
    formel = """
        x=3
        if 3 <= 5 goto l1  // true
        y= y + x         // skip
        :label1
        z= 10
        x= x + 1
        """;
    f.setEquations(formel);

    Exception exception = assertThrows(UnknownSymbolException.class, f::calculate);
    assertEquals("UnknownSymbolException: Symbol 'l1' is unknown or has not been initialized " +
        "(line " + 2 + ", character " + 20 + ")", exception.getMessage());
  }


  @Test
  public void PerformanceTest() throws InterpreterException {
    formel = """
        x1= 1 // comment
        x2= x1 + 1 // comment
        x3= x1 + 2 // comment
        x4= -x1 * -4 // comment
        x5= x1 + x1 + x2 + x1 // comment
        x6= x4 * x2 - x2 // comment
        x7= x6 * x6 - x5 * x5 - x4 // comment
        x8= x4 * x2 // comment
        x9= 3 * (4-1) // comment
        x10= 10 // comment
        x11= ((100 * x3) - (x4 * 50)) - x6 * 15 + 1 // comment
        x12= (1000 - x1 * 988) // comment
        x13= x1 * x2 * x3 * x2 + 1 // comment
        x14= x13 + x4 - x4 + x4 - x4 + 1 // comment
        x15= 100 - (x3 * 10) * x3 + 5 // comment
        x16= -(1000 * 2) + 2016 // comment
        x17= x13 * x2 - (x3 * x3) // comment
        x18= (-(-1000 * 2)/1000) * x9 // comment
        x19= -(-(x4 * x4 + 3)) // comment
        x20= -(-(-(-(20)))) // comment
        x21= (-3000 / -300) * 2 + 1 // comment
        x22= x21 + 2 -1 // comment
        x23= -23 * (-2) - 23 // comment
        x24= -(x22 * x3) + x7 * 13 - 1 // comment
        x25= x24 + 1 // comment
        x26= x1+25 // comment
        x27= (54 / 2) * 2 - 27 // comment
        x28= 7 * 4 // comment
        x29= 40 - 2 - 2 - 2 - 2 - 2 - 1 // comment
        x30= 12 - 2 + 5 * 4 // comment
        x31= 12 - 2 + 5 * 4 + 1 // comment
        x32= 4 * 4 * 2 // comment
        x33= 99 / 3 // comment
        x34= 2 * 3 * 4 + 10 // comment
        x35= 5 * 5 + 10 // comment
        x36= 6 * (3*2) // comment
        x37= 7.4 * 5 // comment
        x38= 9.5 * 4 // comment
        x39= 2 * 15 + 9 // comment
        x40= 40 / (8 * 5) * 40 // comment
        x41= 4 * 10 + 1 // comment
        x42= (100 / 10 - 3) * 6 // comment
        x43= 10 + 10 + 6 * 3 + 5 // comment
        x44= 44 / 4 * 2 + 22 // comment
        x45= 90 / (45 * 2) * 45 // comment
        x46= 22 * 2 + 2 // comment
        x47= 50 - 3 // comment
        x48= 25 * 2 - 2 // comment
        x49= 25 * 2 - 1 // comment
        x50= 25 * 2 // comment
        x51= 102 / 2 // comment
        x52= 104 / 2 // comment
        x53= 110 - 60 + 3 // comment
        x54= 110 - 56 // comment
        x55= 110 - 55 // comment
        """;
    f.setEquations(formel);
    f.calculate();
    String x = "x";
    for (int i = 1; i < 56; i++) {
      x = x + "" + i;
      result.put(x, (double) i);
      x = "x";
    }

    assertEquals("Viele Zuweisungen & Rechnungen", result, f.getVariables());
  }

  @Test
  public void Potenzen() throws InterpreterException {
    formel = """
        z=3**3
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("z", 27.0);

    assertEquals("Potenzen", result, f.getVariables());
  }

  @Test
  public void Potenzen2() throws InterpreterException {
    formel = """
        w= (1+3)**2
        x= 10**2
        y= 2**2
        z=3**3 + 1
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("w", 16.0);
    result.put("x", 100.0);
    result.put("y", 4.0);
    result.put("z", 28.0);

    assertEquals("Potenzen2", result, f.getVariables());
  }

  @Test
  public void MathMethods() throws InterpreterException {
    formel = """
        a= sin(90)
        b= cos(90)
        c= tan(0.5235987755982988)
        d= asin(0)
        e= acos(0.5)
        f= atan(0.9)
        g= sqrt(9)
        h= abs(-3980)
        i= floor(3.6)
        j= ceil(3.1)
        k= ln(2)
        l= log(1)
        m= pi()
        n= e()
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("a", 0.8939966636005579);
    result.put("b", -0.4480736161291701);
    result.put("c", 0.5773502691896257);
    result.put("d", 0.0);
    result.put("e", 1.0471975511965979);
    result.put("f", 0.7328151017865066);
    result.put("g", 3.0);
    result.put("h", 3980.0);
    result.put("i", 3.0);
    result.put("j", 4.0);
    result.put("k", 0.6931471805599453);
    result.put("l", 0.0);
    result.put("m", Math.PI);
    result.put("n", Math.E);

    assertEquals("Math Bibliothek", result, f.getVariables());
  }

  @Test
  public void Betrag() throws InterpreterException {
    formel = """
        x= |-10|
        y= |20|
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("x", 10.0);
    result.put("y", 20.0);

    assertEquals("Positive Zahlen", result, f.getVariables());
  }

  @Test
  public void Operator1() throws InterpreterException {
    formel = """
        if 2<3 goto label1
        w= 3         //skip
        x= 10*2      //skip
        :label1
        y= 2*2
        z=3*3
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("y", 4.0);
    result.put("z", 9.0);

    assertEquals("Operator1", result, f.getVariables());
  }

  @Test
  public void Operator2() throws InterpreterException {
    formel = """
        if 2<3 && 3==3 || 2>3 goto label1
        x= 10000*2   //skip
        :label1
        y= 5678
        z= 1*2*3*4*5
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("y", 5678.0);
    result.put("z", 120.0);

    assertEquals("Operator2", result, f.getVariables());
  }

  @Test
  public void Operator3() throws InterpreterException {
    formel = """
        if 2<=2 && 3<=3 || 4<2 goto label1
        x= 10000*100   //skip
        :label1
        z= 100
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("z", 100.0);

    assertEquals("Operator3", result, f.getVariables());
  }

  @Test
  public void Operator4() throws InterpreterException {
    formel = """
        if 3==2 || 3<=3 || 1>=1 goto label1
        x= 10000
        :label1
        z= 100
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("z", 100.0);

    assertEquals("Operator4", result, f.getVariables());
  }

  @Test
  public void Operator5() throws InterpreterException {
    formel = """
        if 3==3 && 100>3 && 10>=1 goto label1
        x= 10000
        :label1
        z= 100
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("z", 100.0);

    assertEquals("Operator5", result, f.getVariables());
  }
  // IncorrectSyntax?? ==>    if 3==2 && 3<=3 || 4<2 goto label1

  @Test
  public void NotOperator1() throws InterpreterException {
    formel = """
        if !2==2 goto label1
        x= 10000
        :label1
        z= 100
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("x", 10000.0);
    result.put("z", 100.0);

    assertEquals("Not Operator 1", result, f.getVariables());
  }

  @Test
  public void NotOperator2() throws InterpreterException {
    formel = """
        if !2<2 goto label1
        x = 10000*100
        :label1
        z = 100
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("z", 100.0);

    assertEquals("Not Operator 2", result, f.getVariables());
  }

  @Test
  public void NotOperator3() throws InterpreterException {
    formel = """
        // !falsch || falsch ==> wahr || falsch
        if !4<2 || 5>2 goto label1
        x= 10000
        :label1
        z= 100
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("z", 100.0);

    assertEquals("Not Operator 3", result, f.getVariables());
  }

  @Test
  public void NotOperator4() throws InterpreterException {
    //geht nicht: ==>   !2<=2 && 3<=3
    formel = """
        if !2<=2 || 2==3 goto label1
        x= 10000   //skip
        :label1
        z= 100
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("x", 10000.0);
    result.put("z", 100.0);

    assertEquals("Not Operator 4", result, f.getVariables());
  }

  @Test
  public void NotOperator5() throws InterpreterException {
    //geht nicht: ==>   !2<=2 && 3<=3 kein &&-Verknüpfung möglich
    formel = """
        // !w    || w   ==>   f || w
        if !2<=2 || 3<=3 goto label1
        x= 10000   //skip
        :label1
        z= 100
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("z", 100.0);

    assertEquals("Not Operator 5", result, f.getVariables());
  }

  @Test
  public void AngularFunctions() throws InterpreterException {
    formel = """
        a= sin(0.5 * pi()) //90 Grad
        b= cos(0.25 * pi())
        c= tan(0.25 * pi()) //
        """;

    f.setEquations(formel);
    f.calculate();
    result.put("a", 1.0);
    result.put("b", 0.7071067811865476);
    result.put("c", 0.9999999999999999);

    assertEquals("Winkelfunktionen", result, f.getVariables());
  }

  @Test
  public void TanOutOfRange() throws InterpreterException {
    formel = """
        x = tan(rad(90)) //Infinity da groesser
        """;

    f.setEquations(formel);

    Exception ex = assertThrows(OutOfRangeException.class, () -> {
      f.calculate();
    });

    formel = """
        x = tan(rad(-90)) //Infinity da kleiner
        """;

    f.setEquations(formel);

    ex = assertThrows(OutOfRangeException.class, () -> {
      f.calculate();
    });
  }

  @Test
  public void ReverseSine() throws InterpreterException {
    formel = """
        d= asin(1.1) //NaN da groesser
        """;

    f.setEquations(formel);

    Exception ex = assertThrows(OutOfRangeException.class, () -> {
      f.calculate();
    });
  }

  @Test
  public void ReverseCosine() throws InterpreterException {
    formel = """
        e= acos(1.1) //NaN da groesser 1
        """;

    f.setEquations(formel);

    Exception ex = assertThrows(OutOfRangeException.class, () -> {
      f.calculate();
    });
  }

  @Test
  public void ReverseTangent() throws InterpreterException {
    formel = """
        f= atan(2000000000000000000)
        """;

    f.setEquations(formel);
    f.calculate();
    result.put("f", Math.PI * 0.5);

    assertEquals("Arkustangens", result, f.getVariables());
  }

  @Test
  public void SquarerootTest() throws InterpreterException {
    formel = """
        x = sqrt(-9)
        """;
    f.setEquations(formel);

    Exception ex = assertThrows(DomainException.class, () -> {
      f.calculate();
    });
  }

  @Test
  public void MathMethods2() throws InterpreterException {
    formel = """
                    
        h= abs(-3980)
        i= floor(3.6)
        j= ceil(-0.5)
        k= ln(e())
        l= log(10)
        m= pi()
        n= e()
        """;

    f.setEquations(formel);
    f.calculate();
    result.put("h", 3980.0);
    result.put("i", 3.0);
    result.put("j", -0.0);
    result.put("k", 1.0);
    result.put("l", 1.0);
    result.put("m", Math.PI);
    result.put("n", Math.E);

    assertEquals("Math Bibliothek", result, f.getVariables());
  }

  @Test
  public void toDegree() throws InterpreterException {
    formel = """
        x = 90
        y = rad(x)
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("x", 90.0);
    result.put("y", Math.PI * 0.5);
    assertEquals("Grad in Bogenmass", result, f.getVariables());
  }

  @Test
  public void toRadians() throws InterpreterException {
    formel = """
        x = pi() * 0.5
        y = deg(x)
        """;
    f.setEquations(formel);
    f.calculate();
    result.put("y", 90.0);
    result.put("x", Math.PI * 0.5);
    assertEquals("Bogenmass in Grad", result, f.getVariables());
  }

  @After
  public void tearDown() {

  }
}
