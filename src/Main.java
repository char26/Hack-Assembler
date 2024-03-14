import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        String filePath = null;
        try {
            filePath = args[0];
        } catch (Exception e) {
            System.err.println("Ensure arg[0] is the file path the the assembly file.");
        }
        int lastSlash = filePath.lastIndexOf("/");
        int ext = filePath.lastIndexOf(".");
        String fileName = filePath.substring(lastSlash + 1, ext);

        try {
            AssemblyFileParser afp = new AssemblyFileParser(filePath);
            MachineCodeWriter.writeToBinaryFile(fileName + "-bin.txt", afp.getParsedAssemblyInstructions());
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

}
