// to test the cases for null, when it serves as empty string or null pointer

class Main {
    Void main() {
        String a;
        String s;
        Box b;

        s = "normal";
        a = null;

        // null treated as empty string
        a = s + a;
        println(a); // should print "normal"

        // null treated as null pointer
        b = null; // cannot use this according to language specification, so just test assignment
    }
}

class Box {
    Int size;
}