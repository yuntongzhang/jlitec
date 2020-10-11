/* Test the following:
 *  - complicated string literal
 *  - string+string, string+atom
 *  - operator precedence and associativity
 *  - nesting "{}" in if/else and while
 *  - while with empty body
 *  - other usual constructs
 **/

class YetAnotherMain {
    Void main() {
        while (true) {}
    }
}

class Demo {
    String a;
    String b;
    String c;

    String manipulateString() {
        a = "\"\r\t\b\n\2\45\087\xf\x09\x7D";
        b = null; //empty string
        c = "how\\about\\this";
        c = a + c;
        c = c + b;
        return c;
    }

    Void precedence(Int one, Int two, Int three, Int four) {
        return two >= - one - 1 || !false && three == 2 * 3 && 4 + four / 5 - 6 + 7 < new Demo() * 8;
    }

    Bool blocks() {
        Bool i;
        Bool j;

        if (true) {
            if (!false) {
                if (i) {
                    while (j) {
                        while (!i) {}
                    }
                } else {
                    if (j) {
                        return i;
                    } else {
                        return j;
                    }
                }
            } else {
                if (j) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            while (i && j || false) {
                while (!j && !i && true) {
                    return j;
                }
            }
        }

        return false;
    }
}
