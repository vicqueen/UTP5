package zad1;

import zad1.models.Bind;

import javax.script.Bindings;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class FieldsService {

    private Class modelClass;
    private Object childClass;

    public FieldsService(Class modelClass, Object childClass) {
        this.modelClass = modelClass;
        this.childClass = childClass;
    }

    public void putValuesToClassFields(HashMap<String, Object> fieldsValues) {
        for (Field field : modelClass.getDeclaredFields()) {
            if (hasBindAnnotation(field)) {
                field.setAccessible(true);
                try {
                    field.set(childClass, fieldsValues.get(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void invokeFirstMethodOfClass() {
        try {
            modelClass.getDeclaredMethods()[0].invoke(childClass);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void putClassFieldsToBindings(Bindings bindings) {
        for (Field field : modelClass.getDeclaredFields()) {
            if (hasBindAnnotation(field)) {
                try {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    Object fieldValue = field.get(childClass);
                    bindings.put(fieldName, fieldValue);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void putScriptVariablesToClassFields(Bindings bindings) {
        ArrayList<String> bindingsToRemove = new ArrayList<String>();

        bindings.forEach( (variableName, variableValue) -> {
            if (!isVariableNameAOneLowercaseLetter(variableName)) {
                try {
                    Field field = modelClass.getDeclaredField(variableName);
                    if (hasBindAnnotation(field)) {
                        field.setAccessible(true);
                        field.set(childClass, variableValue);
                    }
                } catch (NoSuchFieldException e) {

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                bindingsToRemove.add(variableName);
            }
        });

        for (int i = 0; i < bindingsToRemove.size(); i++) {
            bindings.remove(bindingsToRemove.get(i));
        }
    }

    public void appendClassFields(StringBuilder stringBuilder, ArrayList<String> processedVariableNames) {
        for (Field field : modelClass.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                String fieldName = field.getName();

                if (!fieldName.equals("LL") && !processedVariableNames.contains(fieldName) && hasBindAnnotation(field)) {
                    stringBuilder.append("\n" + fieldName);
                    Object fieldValue = field.get(childClass);
                    appendValue(stringBuilder, fieldValue);
                    processedVariableNames.add(fieldName);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void appendValue(StringBuilder stringBuilder, Object value) {
        if (value.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(value); i++) {
                stringBuilder.append("\t" + Array.get(value, i));
            }
        } else {
            stringBuilder.append("\t" + value);
        }
    }

    private boolean isVariableNameAOneLowercaseLetter(String variableName) {
        return variableName.length() == 1 && Character.isLowerCase(variableName.toCharArray()[0]);
    }

    private boolean hasBindAnnotation(Field field) {
        Bind bind = field.getAnnotation(Bind.class);
        return bind != null;
    }
}
