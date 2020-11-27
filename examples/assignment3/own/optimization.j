// to demo the effect of optimizations

class Main {
    Void main() {
        Int a;
        Int b;
        Int c;
        Int d;
        Int e;
        Int f;
        Int g;

        a = 2 + 3; // 5
        b = 10; // 10
        c = a; // 5
        d = a + b; // 15
        e = d; // 15
        d = 2 + 3; // 5
        f = e; // 15
        g = c + d; // 10

        println(g); // should print 10
    }
}