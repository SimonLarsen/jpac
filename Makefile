CP=.:lwjgl/jar/lwjgl.jar:lwjgl/jar/lwjgl_util.jar:lwjgl/jar/slick-util.jar
LIB=java.library.path=lwjgl/native/linux

default:
	javac -classpath $(CP) *.java

.PHONY: run
run:
	java -classpath $(CP) -D$(LIB) Game
