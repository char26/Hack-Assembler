import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CInstruction extends Instruction {
    private String comp;
    private HashMap<String, String> compCodes;
    private String dest;
    private HashMap<String, String> destCodes;
    private String jump;
    private HashMap<String, String> jumpCodes;

    /**
     * Parses the CInstruction into parts and converts the assembly
     * into binary code.
     *
     * @param code string of assembly code
     */
    public CInstruction(String code) {
        if (code.startsWith("@")) {
            throw new IllegalArgumentException("C instructions must not start with @");
        }
        // Using Gson library to parse codes.json into different HashMaps
        Path path = Paths.get("src/json/codes.json");
        try {
            String jsonString = Files.readString(path, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, HashMap<String, String>>>() {
            }.getType();
            HashMap<String, HashMap<String, String>> map = gson.fromJson(jsonString, type);
            compCodes = map.get("comp");
            destCodes = map.get("dest");
            jumpCodes = map.get("jump");
            map = null;
        } catch (IOException e) {
            System.err.println(e);
        }

        try {
            parseCodeIntoParts(code);
        } catch (Exception e) {
            System.err.println(e);
        }

        assemblyCode = code;
        machineCode = "111" + compCodes.get(comp) + destCodes.get(dest) + jumpCodes.get(jump);
    }

    /**
     * Splits the code into dest (optional), comp, and jump (optional)
     *
     * @param code assembly code string to split into parts
     */
    private void parseCodeIntoParts(String code) throws Exception {
        int eqIndex = code.indexOf("=");
        if (eqIndex != -1) {
            dest = code.substring(0, eqIndex);
        } else {
            dest = "null";
        }
        int scIndex = code.indexOf(";");
        if (scIndex != -1) {
            jump = code.substring(scIndex + 1);
        } else {
            jump = "null";
        }
        // There are 4 scenarios to consider to find the comp part
        // 1) No destination and no jump (D)
        if (eqIndex == -1 && scIndex == -1) {
            comp = code;
        }
        // 2) No destination and a jump (0;JMP)
        else if (eqIndex == -1 && scIndex != -1) {
            comp = code.substring(0, scIndex);
        }
        // 3) A destination and no jump (D=M)
        else if (eqIndex != -1 && scIndex == -1) {
            comp = code.substring(eqIndex + 1);
        }
        // 4) A destination and a jump (D=M;JMP)
        else if (eqIndex != -1 && scIndex != -1) {
            comp = code.substring(eqIndex + 1, scIndex);
        }
        // Otherwise we have big problems
        else {
            throw new Exception("Something is wrong with comp parsing logic");
        }
        assert (comp != null);
    }

    /**
     * Returns the assembly and machine code of this instruction.
     *
     * @return the string representation of this instruction.
     */
    public String toString() {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("Assembly[");
        if (dest != null) {
            sBuilder.append(dest + "=");
        }
        sBuilder.append(comp);
        if (jump != null) {
            sBuilder.append(";" + jump);
        }
        sBuilder.append("], Machine[" + machineCode + "]");
        return sBuilder.toString();
    }
}
