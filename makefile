all:
	javac -d bin src/*.java

stop:
	pkill java 2>/dev/null; true
	pkill rmiregistry

clean:
	rm -rf bin/*.class
