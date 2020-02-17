default:
	javac -d . src/Token.java
	javac -d . src/Reader.java
	javac -d . src/Extractor.java
	javac -d . src/Process.java
clean:
	rm -f *.class
