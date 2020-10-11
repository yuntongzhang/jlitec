/* Test the failure case of:
 *  - Wrong operand for unary operator !
 **/

class Main {
    Void main() {
        return;
    }
}

class SomeOperation {
    Bool a;
    Bool b;
    Void test() {
        a = !true; //bool literal
        a = !b; //atom
        a = !2; //integer literal => should give error
    }
}
