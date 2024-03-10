package src;

import java.util.List;
import java.util.Scanner;

public class AssemblyFileParser {
    private List<String> cleanAssemblyCode;
    private Scanner fileReader;

    public AssemblyFileParser(String fileName) {

    }

    public String clean(String rawLine) {
        String cleanLine = rawLine.replaceAll("\\s+", " ").trim();
        // I am replacing spaces with empty strings here to account for something like
        // D = M where there are spaces between the instructions
        cleanLine = rawLine.replaceAll(" ", "");

        int commentIndex = cleanLine.indexOf("//");
        if (commentIndex != -1) {
            cleanLine = cleanLine.substring(0, commentIndex);
        }
        return cleanLine;
    }
}
