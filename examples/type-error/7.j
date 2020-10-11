/* Test the following:
 *  - type error when readln has non Int/Bool/String type.
 **/

class Another_Main {
    Void main(Void void_fml, String sTR) {
        Surface sur;

        readln(sur);
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

    Surface contain(Void nothing) {
        return this;
    }
}
