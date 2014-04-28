default: clean build run

build:
	javac -cp "*" *.java

run:
	java -cp "*":./ CTFServer 4444

clean:
	$(RM) *.class *.ctxt lobbies.json lobbies/*.json
