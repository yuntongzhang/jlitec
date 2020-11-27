/* Test the following:
 *  - type error when while condition is not of type Bool
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

    Void notRight(Void v) {
        while (v) {
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