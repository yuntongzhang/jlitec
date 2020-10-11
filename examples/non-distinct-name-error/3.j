/* Test the following:
 *  - Report failure with duplicated method names in a class
 **/

class Another_Main {
    Void main(Void void_fml, String sTR) {
        return;
    }

}

class Line {
    Line contain(Particle particle) {
        return this;
    }

    Particle contain(Particle particle) {
        return particle;
    }
}

class Particle {
}
