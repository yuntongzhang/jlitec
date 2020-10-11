/*
 * Success sample.
 * Checking several valid cases are working fine:
 * (1) same name can be used for a field and method of a class, and usages of them are not confused
 * (2) null can be used when a nullable type is expected
 * (3) arithmetic operation
 * (4) string operation
 * (5) chained field accesses
 * etc........
 **/

class Another_Main {
    Void main(Void void_fml, String sTR) {
        Arith ar;
        Int intRes;
        String strRes;

        intRes = ar.a + ar.inner.a + ar.c;
        strRes = ar.b + ar.inner.b + null + null;
    }

}

class Surface {
    Bool needToTransform;
    Surface replace; // field and method have same name

    Void transform() {
        Surface s;
        Int i;
        Void v;
        s = contain(v);

        while (((((!needToTransform))))) {
            return;
        }
        replace(s); // use of method
    }

    Surface contain(Void nothing) {
        return replace; // use of field
    }

    Void replace(Surface another) {
        String a;
        Arith arith;
        a = null;
        arith = null;
        return;
    }
}

class Arith {
    Int a;
    String b;
    Int c;
    SmallArith inner;
}

class SmallArith {
    Int a;
    String b;
    Void c;
}
