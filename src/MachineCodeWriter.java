import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MachineCodeWriter {
    public static void writeToBinaryFile(String fileName, List<Instruction> parsedAssemblyInstructions) {
        try {
            FileWriter fw = new FileWriter(fileName);
            for (Instruction inst : parsedAssemblyInstructions) {
                System.out.println(inst.machineCode);
                fw.write(inst.machineCode + "\n");
            }
            fw.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
