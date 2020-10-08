/* Test the following:
 *  - Report failure with duplicated parameter names in a method declaration
 **/

class Another_Main {
    Void main(Void void_fml, String sTR) {
        File fd;
        Surface sur;

        readln(fd);
        sur = new Surface().contain(new Line().contain(new Particle()));
        sur.transform();
        println(-(sur * 2 + 3));
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

    Surface contain(Line line) {
        return this;
    }
}

class Line {
    Line contain(Particle something, Line something) {
        return this;
    }
}

class Particle {
}
