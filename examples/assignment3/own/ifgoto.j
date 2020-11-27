// test ifGotos with different kinds of conditions

class IfGotoMain {
    Void main() {
        Bool a;
        Bool b;
        Int intOne;
        Int intTwo;

        intOne = 10;
        if (intOne > 5) {
            println("intOne is greater than 5"); // print this
        } else {
            println("intOne is less than 5");
        }

        intTwo = 10;
        if (intOne <= intTwo) {
            println("compare two vars: should be printed"); // print this
        } else {
            println("compare two vars: should NOT be printed");
        }

        if (5 > 3) {
            println("if condition is folded, and this branch should always be taken"); // print this
        } else {
            println("should never be printed");
        }

        if (12 != 12) {
            println("if condition is folded, and this branch should never be taken");
        } else {
            println("should always be printed"); // print this
        }

        return;
    }
}