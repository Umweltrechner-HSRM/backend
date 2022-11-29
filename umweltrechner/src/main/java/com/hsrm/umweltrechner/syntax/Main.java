package com.hsrm.umweltrechner.syntax;

public class Main {
    public static void main(String[] args) {
        String formeln = """
                //something
                //even more
                x := 1
                y := x + 1
                goto first_if
                z := y * y + (1/2)
                :first_if
                if y > x goto label1
                // words // words // words
                a := 1.5 * 2 + 1,5
                b := -2 * 3 + +2
                c := 1 + 2 + 3 * 4 * y + (3*2/4 + (2*2)) - 1
                f := s1 * 2
                :label1
                x2 := 1 - 1 * 2 + 23 // test comment
                x3 := ( 3 + 3 ) * 2//more test comments
                _x4 := 5 * -5//hello!
                """;
        FormelInterpreter f = new FormelInterpreter();
        try {
            f.addSensor("s1", 10);
            f.checkSyntax(formeln);
            f.setEquations(formeln);
            f.calculate();
            System.out.println(f.getVariables());

        } catch (InterpreterException e) {
            System.out.println(e.getMessage());

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }
}
