/* Test the following:
 *  - Report failure with duplicated method names in a class
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
    Line contain(Particle particle) {
        return this;
    }

    Surface contain(Particle particle) {
        return new Surface();
    }
}

class Particle {
}
