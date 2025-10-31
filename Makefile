# Configuration des variables
SRC_DIR     = src
CLASS_DIR   = classes
MAIN_CLASS  = graphro.GraphROEleve
SOURCES     = $(wildcard $(SRC_DIR)/graphro/*.java)


all: compil run

compil:
	@echo "--- Compilation des sources Java ---"
	@mkdir -p $(CLASS_DIR)
	# Utilisation de $(SOURCES) pour compiler tous les fichiers
	javac -sourcepath $(SRC_DIR) -d $(CLASS_DIR) $(SOURCES)
	@echo "Compilation réussie. Les fichiers .class sont dans $(CLASS_DIR)."

run: compil
	@echo "--- Exécution de $(MAIN_CLASS) ---"
	java -classpath $(CLASS_DIR) $(MAIN_CLASS)

clean:
	@echo "--- Nettoyage des fichiers compilés ---"
	@rm -rf $(CLASS_DIR)
	@echo "Répertoire $(CLASS_DIR) supprimé."