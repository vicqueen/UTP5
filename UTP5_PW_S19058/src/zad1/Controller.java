package zad1;

import zad1.models.Bind;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class Controller {

    private String modelName;
    private String data;

    public Controller(String modelName) {
        this.modelName = modelName;
    }

    public Controller readDataFrom(String fname) {
        //wczytuje dane do obliczeń z pliku o nazwie fname
        return this;
    }

    public Controller runModel() {
        //uruchamia obliczenia modelowe
        try {
            Class modelClass = Class.forName("zad1.models." + modelName);
            for (Field field : modelClass.getDeclaredFields()) {
                Bind bind = field.getAnnotation(Bind.class);
                if (bind != null) {
                    field.setAccessible(true);
                    Object childClass = modelClass.newInstance();
                    field.set(childClass, 5);
                    modelClass.getDeclaredMethods()[0].invoke(childClass);
                }
            }

        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

        return this;
    }

    public Controller runScriptFromFile(String fname) {
        return this;
    }

    public Controller runScript(String script) {
        return this;
    }

    public String getResultsAsTsv() {
        //zwraca wyniki obliczeń (wszystkie zmienne z modelu oraz zmienne utworzone w skryptach) w postaci napisu,
        // którego kolejne wiersze zawierają nazwę zmiennej i jej wartosci,
        // rozdzielone znakami tabulacji.
        return "";
    }
}
