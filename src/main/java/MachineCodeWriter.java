import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MachineCodeWriter {
    /**
     * Writes a List of Instruction objects in their binary form to a binary file.
     *
     * @param fileName                   the name (or path) of the file to write to.
     * @param parsedAssemblyInstructions a list of Instructions
     */
    public static void writeToBinaryFile(String fileName, List<Instruction> parsedAssemblyInstructions) {
        try {
            DataOutputStream os = new DataOutputStream(new FileOutputStream(fileName + "-bin.hack"));
            for (Instruction inst : parsedAssemblyInstructions) {
                os.writeBytes(inst.machineCode + "\n");
            }
            os.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
