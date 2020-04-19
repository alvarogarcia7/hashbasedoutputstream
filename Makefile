.PHONY: test
test:
	./mvnw test

.PHONY: clean
clean:
	./mvnw clean

.PHONY: install-hooks
install-hooks:
	cp -R .githooks/* .git/hooks

