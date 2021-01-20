package zad1;

public class Controller {

    private String modelName;

    Controller(String modelName) {
        this.modelName = modelName;
    }

    Controller readDataFrom(String fname) {
        //wczytuje dane do obliczeń z pliku o nazwie fname
        return this;
    }

    Controller runModel() {
        //uruchamia obliczenia modelowe
        return this;
    }

    Controller runScriptFromFile(String fname) {
        return this;
    }

    Controller runScript(String script) {
        return this;
    }

    String getResultsAsTsv() {
        //zwraca wyniki obliczeń (wszystkie zmienne z modelu oraz zmienne utworzone w skryptach) w postaci napisu,
        // którego kolejne wiersze zawierają nazwę zmiennej i jej wartosci,
        // rozdzielone znakami tabulacji.
        return "";
    }
}
