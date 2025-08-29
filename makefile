# Compilador e opções
JAVAC = javac
JAVA = java

# Arquivos-fonte e classe principal
SOURCES = $(wildcard *.java)
MAIN = Main

# Regra padrão: compilar e rodar
all: run

# Compila todos os .java
classes:
	$(JAVAC) $(SOURCES)

# Roda o programa principal (depende da compilação)
run: classes
	$(JAVA) $(MAIN)

# Limpa os .class
clean:
	rm -f *.class
