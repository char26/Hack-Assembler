public class AInstruction extends Instruction {
    public AInstruction(String code) {
        if (!code.startsWith("@") || code.length() < 2)
            throw new IllegalArgumentException("A instructions must start with @ and be followed by an address");
        assemblyCode = code;
        int address = Integer.parseInt(assemblyCode.substring(1));
        machineCode = decimalToBinary(address);
    }

    public String decimalToBinary(int address) {
        StringBuilder binary = new StringBuilder(16);
        while (address != 0) {
            binary.insert(0, address % 2);
            address /= 2;
        }
        while (binary.length() < 16) {
            binary.insert(0, "0");
        }

        return binary.toString();
    }

    public String toString() {
        return "A-Instruction [Assembly = " + assemblyCode + ", Machine = " + machineCode + "]";
    }
}
