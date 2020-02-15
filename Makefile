default:
	javac -d . src/Token.java
	javac -d . src/Process.java
clean:
	rm -f *.class
