/* Test the failure case of:
 *  - empty if/else body
 **/

class Main {
    Void main() {
        if (true) {

        } else {
            return;
        }
    }
}
