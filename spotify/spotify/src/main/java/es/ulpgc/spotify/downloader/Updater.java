package es.ulpgc.spotify.downloader;

import com.google.gson.JsonElement;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Updater {
    public void updateStrings(Statement statement, String tableName, String columnName, List<JsonElement> values, String setterColumnName, List<JsonElement> setterValues) {
        for (int i = 0; i < values.size(); i++) {
            String stringValue = String.valueOf(values.get(i));
            String setterStringValue = String.valueOf(setterValues.get(i));
            try {
                statement.execute("UPDATE " + tableName + " SET " + columnName + " = " + stringValue + " WHERE " + setterColumnName + " = " + setterStringValue);
            } catch (SQLException e) {
                System.out.println("Skipped: " + stringValue + " due to string contains possible quotes and could not escaped.");
            }
        }
    }

    public void updateArrayLists(Statement statement, String tableName, String columnName, List<ArrayList<JsonElement>> valuesArray, String setterColumnName, List<JsonElement> setterValues) {
        for (int i = 0; i < valuesArray.size(); i++) {
            String emptyString = "";
            ArrayList<JsonElement> valueArray = valuesArray.get(i);
            String setterStringValue = String.valueOf(setterValues.get(i));
            for (JsonElement value : valueArray) {
                emptyString += value.getAsString() + "; ";
            }
            try {
                statement.execute(String.format("UPDATE %s SET %s = '%s' WHERE %s = %s", tableName, columnName, emptyString, setterColumnName, setterStringValue));
            } catch (SQLException e) {
                System.out.println("Skipped: " + emptyString + " due to string contains possible quotes and could not escaped.");
            }
        }
    }

    public void updateMillis(Statement statement, String tableName, String columnName, List<String> strings, String setterColumnName, List<JsonElement> setterValues) throws SQLException {
        for (int i = 0; i < strings.size(); i++) {
            String value = strings.get(i);
            String setterStringValue = String.valueOf(setterValues.get(i));
            statement.execute(String.format("UPDATE %s SET %s = '%s' WHERE %s = %s", tableName, columnName, value, setterColumnName, setterStringValue));
        }
    }

    public void updateInteger(Statement statement, String tableName, String columnName, List<JsonElement> values, String setterColumnName, List<JsonElement> setterValues) throws SQLException {
        for (int i = 0; i < values.size(); i++) {
            int integer = values.get(i).getAsInt();
            String setterStringValue = String.valueOf(setterValues.get(i));
            statement.execute(String.format("UPDATE %s SET %s = %d WHERE %s = %s", tableName, columnName, integer, setterColumnName, setterStringValue));
        }
    }

    public void updateBoolean(Statement statement, String tableName, String columnName, List<JsonElement> values, String setterColumnName, List<JsonElement> setterValues) {
        for (int i = 0; i < values.size(); i++) {
            String stringValue = String.valueOf(values.get(i));
            String setterStringValue = String.valueOf(setterValues.get(i));
            try {
                if (Objects.equals(stringValue, "false"))
                    statement.execute(String.format("UPDATE %s SET %s = '%s' WHERE %s = %s", tableName, columnName, "false", setterColumnName, setterStringValue));
                if (Objects.equals(stringValue, "true"))
                    statement.execute(String.format("UPDATE %s SET %s = '%s' WHERE %s = %s", tableName, columnName, "true", setterColumnName, setterStringValue));
            } catch (SQLException e) {
                System.out.println("Name with quotes: " + stringValue);
            }
        }
    }
}
