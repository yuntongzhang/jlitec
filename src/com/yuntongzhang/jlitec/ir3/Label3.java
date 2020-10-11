package com.yuntongzhang.jlitec.ir3;

import java.util.Objects;

public class Label3 extends Stmt3 {
    private static int counter = 1;
    private int number;

    private Label3(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public static Label3 genNewLabel() {
        Label3 result = new Label3(counter);
        counter++;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Label3 label3 = (Label3) o;
        return number == label3.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public void prettyPrint(int indentation) {
        String toPrint = ("Label " + number + ":").indent(indentation - 1);
        System.out.print(toPrint);
    }
}
