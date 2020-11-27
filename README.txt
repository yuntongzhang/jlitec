Instructions on running the project:

First, build the project with:
    `make`

Then run the built Main program with:
    `make run ARGS="[-O] <input-file>" [OUT=<output-file>]`

`-O` is optional here. Specifying it would turn on the major optimizations.
`OUT=<output-file>` is optional here. if it is not specified, output is written to stdout.

To clean the build, run:
    `make clean`
