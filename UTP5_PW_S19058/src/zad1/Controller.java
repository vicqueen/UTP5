package zad1;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Controller {

    private String modelName;
    private String firstLineToBeDisplayed;
    private int yearsCount;
    private HashMap<String, Object> fieldsValues;
    private Class modelClass;
    private Object childClass;
    private FieldsService fieldsService;
    private Bindings bindings;

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
                    firstLineToBeDisplayed = lines.get(i).replace(' ', '\t');
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
            modelClass = Class.forName("zad1.models." + modelName);
            childClass = modelClass.newInstance();
            fieldsService = new FieldsService(modelClass, childClass);
            fieldsService.putValuesToClassFields(fieldsValues);
            fieldsService.invokeFirstMethodOfClass();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        return this;
    }

    public Controller runScriptFromFile(String fname) {
        ScriptEngine engine = getScriptEngine();
        bindings = engine.createBindings();
        fieldsService.putClassFieldsToBindings(bindings);

        try {
            engine.eval(new FileReader(fname), bindings);
        } catch (ScriptException | FileNotFoundException e) {
            e.printStackTrace();
        }
        fieldsService.putScriptVariablesToClassFields(bindings);

        return this;
    }

    public Controller runScript(String script) {
        ScriptEngine engine = getScriptEngine();
        bindings = engine.createBindings();
        fieldsService.putClassFieldsToBindings(bindings);

        try {
            getScriptEngine().eval(script, bindings);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        fieldsService.putScriptVariablesToClassFields(bindings);

        return this;
    }

    public String getResultsAsTsv() {
        StringBuilder stringBuilder = new StringBuilder(firstLineToBeDisplayed);
        ArrayList<String> processedVariableNames = new ArrayList<String>();
        fieldsService.appendClassFields(stringBuilder, processedVariableNames);

        bindings.forEach( (variableName, variableValue) -> {
            if (variableName != "LL" && !processedVariableNames.contains(variableName)) {
                stringBuilder.append("\n" + variableName);
                fieldsService.appendValue(stringBuilder, variableValue);
                processedVariableNames.add(variableName);
            }
        });

        return stringBuilder.toString();
    }

    private ScriptEngine getScriptEngine() {
        ScriptEngineManager manager = new ScriptEngineManager();
        return manager.getEngineByName("groovy");
    }
}
