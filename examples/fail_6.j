/* Test the failure case of:
 *  - Wrong operands for operator +
 *      - checks that string+arithmetic gives error
 **/

class Main {
    Void main() {
        return;
    }
}

class SomeOperation {
    Int a;
    Int b;
    String c;
    String d;
    Void test() {
        a = 1 + 2; // arith+arith
        a = 1 + b; // arith+atom
        c = "hi" + "hello"; // string+string
        c = "hi" + d; // string + atom
        c = "str" + 12; // string+arith => should give error
    }
}
