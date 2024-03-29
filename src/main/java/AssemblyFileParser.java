import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AssemblyFileParser {
    private List<String> cleanAssemblyCode;
    private Scanner fileReader;
    private List<Instruction> parsedAssemblyInstructions;

    /**
     * Parses an assembly file into an array of Instruction objects
     *
     * @param fileName the file name (or file path) of the .asm file to parse
     */
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

    /**
     * Cleans one line of assembly code. Cleaning includes removing
     * comments and whitespace.
     *
     * @param rawLine an uncleaned line of assembly code
     * @return the cleaned line of assembly code as a string
     */
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

    /**
     * Maps an assembly label to a ROM address.
     *
     * @param cleanLine a string of already cleaned assembly code
     * @param address   integer address that the label should map to
     */
    private void insertLabelInSymbolTable(String cleanLine, int address) throws Exception {
        assert cleanLine.startsWith("(");
        assert cleanLine.startsWith(")");
        int firstParen = cleanLine.indexOf("(");
        int secondParen = cleanLine.indexOf(")");
        if (firstParen == -1 || secondParen == -1) {
            throw new Exception("Labels need to be wrapped in parenthesis ().");
        }
        String labelName = cleanLine.substring(firstParen + 1, secondParen);
        SymbolTable.add(labelName, address);
    }

    /**
     * The first pass through the assembly file removes
     * whitespace, maps labels to addresses, and ignores
     * blank lines.
     */
    public void makeFirstPass() {
        String rawLine, cleanLine;
        int currentLine = 0;
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
    }

    /**
     * The second pass through the assembly file creates
     * Instruction objects for each line and maps variable names to
     * addresses, starting at 16.
     */
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

    /**
     * Returns a string of this object's contents
     *
     * @return this object as a string
     */
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (String line : cleanAssemblyCode) {
            output.append(line).append("\n");
        }
        return output.toString();
    }
}
