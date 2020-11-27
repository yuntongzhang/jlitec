// test method call with more than 4 arguments

class Main {
    Void main() {
        Int m1;
        Int m2;
        Computer c;

        c = new Computer();
        c.a = 10;
        c.b = 3;

        m1 = c.computerSth(c.a, c.b, 2, 4, 6, 8); // 48
        m2 = c.computerSth(1, 3, 5, 7, 9, c.a); // 11
        println(m1 - m2); // 37
        return;
    }

}

class Computer {
    Int a;
    Int b;

    // want to make some args stored on stack in this method
    Int computerSth(Int c, Int d, Int e, Int f, Int g, Int h) {
        Int inter;
        inter = c * d * e - a * b;
        return inter + f + g + h;
    }
}