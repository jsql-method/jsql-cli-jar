package io.jsql.taskRunners.common;

import java.util.regex.Pattern;

// Enum pomocniczy ustalający regexy,
// powinien być używany na początku tylko raz
public enum Command {
    // Zarejestrowane komendy
    SELECT("select"),
    SELECT_ONE("selectOne"),
    REMOVE("remove"),
    DELETE("delete"),
    UPDATE("update"),
    INSERT("insert"),
    APPEND("append"),
    NAMED_QUERY("namedQuery"),
    SET("set"),
    QUERY("query");

    // Odpowiadający token
    String str;

    // Prefix komend
    final static String PREFIX = "\\.";

    // Konstruktor budujący enum
    Command(String str)
    {
        this.str = str;
    }

    // Metoda zwraca zbudowany ciąg dla regexa
    public static String getRegex()
    {
        String result = PREFIX;
        boolean first = true;

        result += "(?:";
        for(Command c : values())
        {
            if(first)
            {
                result += c.str; first = false;
            }
            else result += "|" + c.str;
        }
        result += ")(?:\\((?:[\\\"\\'].*[\\\"\\'],)?\\s*(?:\\\")([^\\\"]*?)(?:\\\")\\)|\\((?:[\\\"\\'].*[\\\"\\'],)?\\s*(?:\\')([^\\']*?)(?:\\')\\))";
        return result;
    }

    // Metoda zwraca skompilowany pattern regexowy
    public static Pattern getPattern()
    {
        return Pattern.compile(getRegex());
    }

}
