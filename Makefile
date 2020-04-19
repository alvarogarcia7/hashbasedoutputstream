.PHONY: test
test: clean
	./mvnw test

.PHONY: clean
clean:
	./mvnw clean

