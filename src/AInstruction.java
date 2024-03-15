public class AInstruction extends Instruction {
    public AInstruction(String code) {
        /**
         * Parses address and converts assembly code to machine code
         *
         * @param code assembly code to convert to AInstruction object
         */
        if (!code.startsWith("@") || code.length() < 2)
            throw new IllegalArgumentException("A instructions must start with @ and be followed by an address");
        assemblyCode = code;
        int address = Integer.parseInt(assemblyCode.substring(1));
        machineCode = decimalToBinary(address);
    }

    public String decimalToBinary(int address) {
        /**
         * Converts an integer to a binary string
         *
         * @param address the integer address to convert to binary
         * @return the converted int as a binary string
         */
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
        /**
         * Returns the assembly and machine code of this instruction as a string
         * 
         * @return this object as a string
         */
        return "A-Instruction [Assembly = " + assemblyCode + ", Machine = " + machineCode + "]";
    }
}
