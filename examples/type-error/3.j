/* Test the following:
 *  - type error when field assignment sides have diff types.
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
        l.name = 2;
    }

    Surface contain(Void nothing) {
        return this;
    }
}

class Line {
    String name;
}