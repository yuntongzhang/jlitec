// test spilling

class Main {
    // want to force spilling in this method
    // reg allocation allocates to 8 registers
    Void main() {
        Int c;
        Int d;
        Int e;
        Int f;
        Int g;
        Int h;
        Int i;
        Int j;
        Int k;
        Int l;
        Int m;
        Int n;
        Int res1;
        Int res2;

        c = 1;
        d = 2;
        e = 3;
        f = 4;
        g = 5;
        h = 6;
        i = 7;
        j = 8;
        k = 9;
        l = 10;
        m = 11;
        n = 12;

        res1 = c + d + e + f + g + h + i + j + k + l + m + n; // 78
        res2 = c * d * e * f * g * (h - i) * (j + k) - l - m - n; // -2073

        println(res1 + res2); // -1995
    }

}
