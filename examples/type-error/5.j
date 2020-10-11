/* Test the following:
 *  - type error when if/else branches have diff type
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
        if (needToTransform) {
            contain(v); // type is Surface
        } else {
            return;    // type is Void
        }
    }

    Surface contain(Void nothing) {
        return this;
    }
}

class Line {
    String name;
}