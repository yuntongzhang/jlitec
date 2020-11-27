/* Test the following:
 *  - type error when if condition is not Bool type
 **/

class Another_Main {
    Void main(Void void_fml, String sTR) {
        String a;
        a = sTR;
    }
}

class Surface {
    Bool needToTransform;
    Void transform() {
        if (((((!needToTransform))))) {
            return;
        } else {
            needToTransform = false;
        }

        while (((((!needToTransform))))) {
            return;
        }
    }

    Void notRight(Line l) {
        if (l.name) {
            return;
        } else {
            return;
        }
    }

    Surface contain(Void nothing) {
        return this;
    }
}

class Line {
    String name;
}