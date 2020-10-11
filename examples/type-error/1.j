/* Test the following:
 *  - type error when last statement in method body has diff type compared to the method return type.
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

    String notRight() {
        String a;
        a = "b";
    }

    Surface contain(Void nothing) {
        return this;
    }
}
