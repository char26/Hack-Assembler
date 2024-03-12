import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AssemblyFileParser {
    private List<String> cleanAssemblyCode;
    private Scanner fileReader;
    private List<Instruction> parsedAssemblyInstructions;

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

    public List<Instruction> getParsedAssemblyInstructions() {
        return parsedAssemblyInstructions;
    }

    private void insertLabelInSymbolTable(String cleanLine, int address) throws Exception {
        int firstParen = cleanLine.indexOf("(");
        int secondParen = cleanLine.indexOf(")");
        if (firstParen == -1 || secondParen == -1) {
            throw new Exception("Labels need to be wrapped in parenthesis ().");
        }
        String labelName = cleanLine.substring(firstParen + 1, secondParen);
        SymbolTable.add(labelName, address);
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

    public void makeSecondPass() {
        String cleanLine;
        int currentVariable = 16;
        while (fileReader.hasNextLine()) {
            cleanLine = fileReader.nextLine();
            // A couple of sanity checks to make sure the lines are actually clean
            assert (!cleanLine.isEmpty());
            assert (cleanLine.indexOf("//") == -1);
            // If we encounter a label
            if (cleanLine.startsWith("(")) {
                try {
                    insertLabelInSymbolTable(cleanLine, currentVariable);
                } catch (Exception e) {
                    System.err.println(e);
                }
                currentVariable++;
            }

            // Handle A-Instructions
            // TODO: handle jumps using @LABEL
            if (cleanLine.startsWith("@")) {
                AInstruction aInst = new AInstruction(cleanLine);
                parsedAssemblyInstructions.add(aInst);
            }
            // TODO: C-Instruction
        }
    }

    public String toString() {
        StringBuilder output = new StringBuilder();
        for (String line : cleanAssemblyCode) {
            output.append(line).append("\n");
        }
        return output.toString();
    }
}
