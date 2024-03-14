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
        parsedAssemblyInstructions = new ArrayList<>();

        File assemblyFile = new File(fileName);
        if (!assemblyFile.exists() && assemblyFile.length() == 0) {
            throw new FileNotFoundException(fileName + " does not exist or is empty.");
        }

        fileReader = new Scanner(assemblyFile);
        makeFirstPass();
        makeSecondPass();
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
        int currentLine = 1;
        while (fileReader.hasNextLine()) {
            rawLine = fileReader.nextLine();
            cleanLine = clean(rawLine);
            // If we encounter a label
            if (cleanLine.startsWith("(")) {
                try {
                    insertLabelInSymbolTable(cleanLine, currentLine);
                    // Once we insert the label, we can remove it.
                    cleanLine = "";
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
            if (!cleanLine.isEmpty()) {
                cleanAssemblyCode.add(cleanLine);
                currentLine++;
            }
        }
        System.out.println(SymbolTable.getAddress("INFINITE_LOOP"));
    }

    public void makeSecondPass() {
        int currentVariable = 16;

        for (int i = 0; i < cleanAssemblyCode.size(); i++) {
            String cleanLine = cleanAssemblyCode.get(i);
            // A couple of sanity checks to make sure the lines are actually clean
            assert (!cleanLine.isEmpty());
            assert (cleanLine.indexOf("//") == -1);

            // Handle A-Instructions
            if (cleanLine.startsWith("@")) {
                // If second character is a letter, we have either a jump to a label
                // or a variable. Need to replace the name with an address
                String firstChar = cleanLine.substring(1, 2);
                if (firstChar.matches("[a-zA-Z]")) {
                    if (SymbolTable.getAddress(cleanLine.substring(1)) == null) {
                        SymbolTable.add(cleanLine.substring(1), currentVariable);
                        currentVariable++;
                    }
                    // At this point we will have either just added the variable to the symbol
                    // table,
                    // or had already added the label in the first pass.
                    int address = SymbolTable.getAddress(cleanLine.substring(1));
                    cleanAssemblyCode.remove(i);
                    cleanAssemblyCode.add(i, "@" + address);
                    cleanLine = "@" + address;
                }

                AInstruction aInst = new AInstruction(cleanLine);
                parsedAssemblyInstructions.add(aInst);
            } else {
                CInstruction cInst = new CInstruction(cleanLine);
                parsedAssemblyInstructions.add(cInst);
            }
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
