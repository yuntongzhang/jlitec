JFLEX_JAR   = lib/jflex-full-1.8.2.jar
CUP_JAR     = lib/java-cup-11b.jar
CUP_RUNTIME = lib/java-cup-11b-runtime.jar

SRC_DIR     = src/com/yuntongzhang/jlitec
BUILD_DIR   = build

MAIN_CLASS  = com.yuntongzhang.jlitec.Main


.PHONY : all compile run clean

all : $(SRC_DIR)/Lexer.java $(SRC_DIR)/Parser.java compile

$(SRC_DIR)/Lexer.java : $(SRC_DIR)/jlite.flex
	java -jar $(JFLEX_JAR) $^

$(SRC_DIR)/Parser.java : $(SRC_DIR)/jlite.cup
	java -jar $(CUP_JAR) -destdir $(SRC_DIR) -parser Parser -interface -locations $^

compile : $(SRC_DIR)/*/*.java $(SRC_DIR)/*.java
	javac -cp $(CUP_RUNTIME) -d $(BUILD_DIR) $(SRC_DIR)/*/*.java $(SRC_DIR)/*.java


# Usage: make run ARGS="[-O] <input-file>" [OUT=<output-file>]
# OUT=<output-file> is optional; if not specified, output to stdout
run:
ifdef OUT
	java -cp $(CUP_RUNTIME):$(BUILD_DIR) $(MAIN_CLASS) $(ARGS) > $(OUT)
else
	java -cp $(CUP_RUNTIME):$(BUILD_DIR) $(MAIN_CLASS) $(ARGS)
endif


clean :
	rm -rf $(BUILD_DIR)
	rm -f $(SRC_DIR)/Lexer.java $(SRC_DIR)/Parser.java $(SRC_DIR)/sym.java $(SRC_DIR)/*~
