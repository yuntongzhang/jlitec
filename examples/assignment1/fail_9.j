/* Test the failure case of:
 *  - ascii value in string literal out of bound (> 255)
 **/

class Main {
    Void main() {
        return;
    }
}

class SomeOperation {
    String a;
    Void test() {
        a = "\126"; // ok
        a = "\x5d"; // ok
        a = "\300"; // not ok
    }
}
