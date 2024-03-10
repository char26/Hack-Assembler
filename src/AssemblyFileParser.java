import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AssemblyFileParser {
    private List<String> cleanAssemblyCode;
    private Scanner fileReader;

    public AssemblyFileParser(String fileName) throws FileNotFoundException {
        cleanAssemblyCode = new ArrayList<>();

        File assemblyFile = new File(fileName);
        if (!assemblyFile.exists() && assemblyFile.length() == 0) {
            throw new FileNotFoundException(fileName + " does not exist or is empty.");
        }

        fileReader = new Scanner(assemblyFile);
        makeFirstPass();
        // makeSecondPass();
        fileReader.close();
    }

    public void makeFirstPass() {
        String rawLine, cleanLine;
        while (fileReader.hasNextLine()) {
            rawLine = fileReader.nextLine();
            cleanLine = clean(rawLine);
            if (!cleanLine.isEmpty()) {
                cleanAssemblyCode.add(cleanLine);
            }
        }

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

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (String line : cleanAssemblyCode) {
            output.append(line).append("\n");
        }
        return output.toString();
    }
}
