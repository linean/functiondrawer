package functiondrawer;

class ParserException extends Exception {
    final static int UNKNOWN_FUNCTION = 0;
    final static int UNKNOWN_CHAR = 1;
    final static int SQRT_PROBLEM = 2;
    final static int ZERO_DIVISION = 3;
    final static int MAX_REACHED = 4;
    private final int errorCode;
    private final String info;

    /**
     * Obsługa błędów parsera
     * @param errorCode - kod błędu
     * @param info - dodatkowa informacja o błędzie
     */
    ParserException(int errorCode, String info) {
        super("ParserException, errorCode: " + errorCode);
        this.errorCode = errorCode;
        this.info = info;
    }

    int getErrorCode() {
        return errorCode;
    }

    String getInfo() {
        return info;
    }
}