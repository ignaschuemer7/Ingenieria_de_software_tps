# Nombre del ejecutable
EXEC = tests

# Archivos fuente de Haskell
SRC = Tests.hs Palet.hs Route.hs Stack.hs Truck.hs

# Comando de compilación
GHC = ghc -o $(EXEC)

# Archivos auxiliares generados por GHC
AUX_FILES = *.hi *.o

# Regla para compilar el programa
$(EXEC): $(SRC)
	$(GHC) $(SRC)
	rm -f $(AUX_FILES)

# Regla para limpiar archivos auxiliares
clean:
	rm -f $(EXEC) $(AUX_FILES) 

# Regla para limpiar y recompilar
rebuild: clean $(EXEC)

.PHONY: clean rebuild
