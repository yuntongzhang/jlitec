/* Test the following:
 *  - type error when var assignment sides have diff types.
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

    Void notRight() {
        String a;
        a = 2;
    }

    Surface contain(Void nothing) {
        return this;
    }
}
