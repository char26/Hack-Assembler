import java.io.FileNotFoundException;

public class Main {
    public static final String PROGRAM_NAME = "../Max";

    public static void main(String[] args) {
        try {
            AssemblyFileParser afp = new AssemblyFileParser(PROGRAM_NAME + ".asm");
            MachineCodeWriter.writeToBinaryFile("machine-code.txt", afp.getParsedAssemblyInstructions());
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }

    }

}
