all: | clean jar

jar:
	@./gradlew :jar

clean:
	@./gradlew clean