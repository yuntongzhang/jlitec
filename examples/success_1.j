/* Test the following:
 *  - binary/unary operators on atoms
 *  - atom with `new`
 *  - chaining of atoms
 *  - if/while conditions with a lot of parenthesis
 *  - class declaration without any var declaration or method declaration
 *  - method body without var declaration
 *  - other usual constructs
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
}

class Particle {

}
