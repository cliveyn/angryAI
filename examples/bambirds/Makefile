swiplpath = $(shell which swipl)

all:
	find . -name "*.java" > sources.txt
	javac -cp jars/Jama-1.0.2.jar:jars/WebSocket.jar:jars/commons-codec-1.7.jar:jars/dyn4j-3.2.1.jar:jars/json-simple-1.1.1.jar:jars/junit-4.12.jar @sources.txt

run_physic:
	java -classpath jars/dyn4j-3.2.1.jar:jars/Jama-1.0.2.jar:jars/WebSocket.jar:jars/commons-codec-1.7.jar:jars/json-simple-1.1.1.jar:jars/junit-4.12.jar:src main.BamBirdPhysic $(swiplpath)

run_bambirds:
	java -classpath jars/dyn4j-3.2.1.jar:jars/Jama-1.0.2.jar:jars/WebSocket.jar:jars/commons-codec-1.7.jar:jars/json-simple-1.1.1.jar:jars/junit-4.12.jar:src main.BamBird $(swiplpath) 127.0.0.1