package zad1;

import zad1.models.Bind;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Controller {

    private String modelName;
    private HashMap<String, Object> fieldsValues;

    public Controller(String modelName) {
        this.modelName = modelName;
        fieldsValues = new HashMap<String, Object>();
    }

    public Controller readDataFrom(String fname) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(fname), StandardCharsets.UTF_8);

            for (int i = 0; i < lines.size(); i++) {
                String[] lineSplit = lines.get(i).split("\t");
                Object[] fieldValue = lineSplit[1].split(" ");

                if (i == 0) {
                    fieldsValues.put("LL", fieldValue.length );
                } else {
                    double[] values = Arrays.stream(fieldValue).mapToDouble(s -> Double.parseDouble((String) s)).toArray();
                    fieldsValues.put(lineSplit[0], values);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this;
    }

    public Controller runModel() {
        try {
            Class modelClass = Class.forName("zad1.models." + modelName);
            for (Field field : modelClass.getDeclaredFields()) {
                Bind bind = field.getAnnotation(Bind.class);
                if (bind != null) {
                    field.setAccessible(true);
                    Object childClass = modelClass.newInstance();
                    field.set(childClass, fieldsValues.get(field.getName()));
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
