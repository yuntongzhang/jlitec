/* Test the failure case of:
 *  - Wrong operands for boolean operator
 **/

class Main {
    Void main() {
        return;
    }
}

class SomeOperation {
    Void test() {
        return true || 1 + 2;
    }
}
