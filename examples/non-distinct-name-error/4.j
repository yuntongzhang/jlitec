/* Test the following:
 *  - Report failure with duplicated parameter names in a method declaration
 **/

class Another_Main {
    Void main(Void void_fml, String sTR) {
        return;
    }

}

class Line {
    Line contain(Particle something, Line something) {
        return this;
    }
}

class Particle {
}
