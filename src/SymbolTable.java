import java.util.HashMap;

public class SymbolTable {
    private static HashMap<String, Integer> theTable;
    private static final String INITIAL_VALID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_.$:";
    private static final String ALL_VALID_CHARS = INITIAL_VALID_CHARS + "0123456789";

    static {
        // Initialize the symbol table with values
        theTable = new HashMap<>();
        // R0-15 maps to 0-15
        for (int i = 0; i < 16; i++) {
            theTable.put("R" + i, i);
        }
        theTable.put("SCREEN", 16384);
        theTable.put("KBD", 24576);
        theTable.put("SP", 0);
        theTable.put("LCL", 1);
        theTable.put("ARG", 2);
        theTable.put("THIS", 3);
        theTable.put("THAT", 4);
    }

    /**
     * Adds a new symbol to the hashmap
     *
     * @param symbol  a string of the symbol key
     * @param address an integer to map the symbol to (value in the hm)
     * @return a boolean stating whether the symbol was added
     */
    public static boolean add(String symbol, int address) {
        if (!isValidName(symbol) || theTable.containsKey(symbol)) {
            return false;
        } else {
            theTable.put(symbol, address);
            return true;
        }
    }

    /**
     * Gets the address corresponding with the symbol
     *
     * @param symbol the symbol to get the address of
     * @return the address of the symbol as an integer
     */
    public static Integer getAddress(String symbol) {
        return theTable.get(symbol);
    }

    /**
     * Determines whether or not a symbol is valid. A symbol is
     * valid only if it starts with a letter (upper or lower), or _.$:
     * The remaiming characters can include numbers, but only the
     * valid symbols _.$: (and letters).
     *
     * @param symbol the string symbol to check the validity of
     * @return a boolean stating whether a symbol is a valid name
     */
    private static boolean isValidName(String symbol) {
        String validChars = INITIAL_VALID_CHARS;
        for (int i = 0; i < symbol.length(); i++) {
            if (!validChars.contains(symbol.substring(i, i + 1))) {
                return false;
            }
            validChars = ALL_VALID_CHARS;
        }
        return true;
    }
}
