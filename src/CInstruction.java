import java.util.HashMap;

public class CInstruction extends Instruction {
    private String comp;
    private HashMap<String, String> compCodes;
    private String dest;
    private HashMap<String, String> destCodes;
    private String jump;
    private HashMap<String, String> jumpCodes;

    public CInstruction(String code) {
        if (code.startsWith("@")) {
            throw new IllegalArgumentException("C instructions must not start with @");
        }
        // TODO: use Gson or another library to import this data from a json file
        compCodes = new HashMap<String, String>();
        compCodes.put("0", "0101010");
        compCodes.put("1", "0111111");
        compCodes.put("-1", "0111010");
        compCodes.put("D", "0001100");
        compCodes.put("A", "0110000");
        compCodes.put("M", "1110000");
        compCodes.put("!D", "0001101");
        compCodes.put("!A", "0110001");
        compCodes.put("!M", "1110001");
        compCodes.put("-D", "0001111");
        compCodes.put("-A", "0110011");
        compCodes.put("-M", "1110011");
        compCodes.put("D+1", "0011111");
        compCodes.put("A+1", "0110111");
        compCodes.put("M+1", "1110111");
        compCodes.put("D+A", "0000010");
        compCodes.put("D+M", "1000010");
        compCodes.put("D-A", "0010011");
        compCodes.put("D-M", "1010011");
        compCodes.put("A-D", "0000111");
        compCodes.put("M-D", "1000111");
        compCodes.put("D&A", "0000000");
        compCodes.put("D&M", "1000000");
        compCodes.put("D|A", "0010101");
        compCodes.put("D|M", "1010101");

        destCodes = new HashMap<String, String>();
        destCodes.put("null", "000");
        destCodes.put("M", "001");
        destCodes.put("D", "010");
        destCodes.put("MD", "011");
        destCodes.put("A", "100");
        destCodes.put("AM", "101");
        destCodes.put("AD", "110");
        destCodes.put("AMD", "111");

        jumpCodes = new HashMap<String, String>();
        jumpCodes.put("null", "000");
        jumpCodes.put("JGT", "001");
        jumpCodes.put("JEQ", "010");
        jumpCodes.put("JGE", "011");
        jumpCodes.put("JLT", "100");
        jumpCodes.put("JNE", "101");
        jumpCodes.put("JLE", "110");
        jumpCodes.put("JMP", "111");
        try {
            parseCodeIntoParts(code);
        } catch (Exception e) {
            System.err.println(e);
        }

        assemblyCode = code;
        machineCode = "111" + compCodes.get(comp) + destCodes.get(dest) + jumpCodes.get(jump);
    }

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
    }

    public String toString() {
        StringBuilder assemblyBuilder = new StringBuilder();
        if (dest != null) {
            assemblyBuilder.append(dest + "=");
        }
        assemblyBuilder.append(comp);
        if (jump != null) {
            assemblyBuilder.append(";" + jump);
        }
        return assemblyBuilder.toString();
    }
}
