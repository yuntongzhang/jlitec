// test string concat (const+const, var+const, var+var)

class StringOpsMain {
    Void main() {
        String a;
        String b;
        String c;
        String const;

        a = "this is a string";
        b = "this is b string";

        const = "const front + " + "const back";
        println(const); // should print "const front + const back";

        a = a + " added with a tail";
        println(a); // should print "this is a string added with a tail";

        c = " with a var tail";
        a = b + c;
        println(a); // should print "this is b string with a var tail";
    }

}