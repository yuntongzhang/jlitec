/* Test the failure case of:
 *  - Wrong operand for unary operator -
 **/

class Main {
    Void main() {
        return;
    }
}

class SomeOperation {
    Int a;
    Int b;
    Void test() {
        a = - 2; // arith
        a = - b; // atom
        a = - "str"; // string => should give error
    }
}
