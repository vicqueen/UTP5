package zad1;

import zad1.models.Bind;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.script.*;
import java.util.HashMap;
import java.util.List;

public class Controller {

    private String modelName;
    private int yearsCount;
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
                String[] fieldValue = lineSplit[1].split(" ");

                if (i == 0) {
                    yearsCount = fieldValue.length;
                    fieldsValues.put("LL", yearsCount );
                } else {
                    double[] values = new double[yearsCount];
                    double lastRememberedValue = 0;
                    for (int j = 0; j < yearsCount; j++) {
                        values[j] = j < fieldValue.length ? Double.parseDouble(fieldValue[j]) : lastRememberedValue;
                        lastRememberedValue = values[j];
                    }
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
            Object childClass = modelClass.newInstance();
            for (Field field : modelClass.getDeclaredFields()) {
                Bind bind = field.getAnnotation(Bind.class);
                if (bind != null) {
                    field.setAccessible(true);
                    field.set(childClass, fieldsValues.get(field.getName()));
                }
            }
            modelClass.getDeclaredMethods()[0].invoke(childClass);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }

        return this;
    }

    public Controller runScriptFromFile(String fname) {
        ScriptEngine engine = getScriptEngine();
        try {
            engine.eval(new FileReader(fname));
            ScriptContext scriptContext = engine.getContext();
        } catch (ScriptException | FileNotFoundException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Controller runScript(String script) {
        try {
            getScriptEngine().eval(script);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getResultsAsTsv() {
        //zwraca wyniki obliczeń (wszystkie zmienne z modelu oraz zmienne utworzone w skryptach) w postaci napisu,
        // którego kolejne wiersze zawierają nazwę zmiennej i jej wartosci,
        // rozdzielone znakami tabulacji.
        return "";
    }

    private ScriptEngine getScriptEngine() {
        ScriptEngineManager manager = new ScriptEngineManager();
        return manager.getEngineByName("groovy");
    }
}
