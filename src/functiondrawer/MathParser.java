package functiondrawer;

import java.awt.*;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa obs\u0142uguje algorytm rekurencyjny, kt\u00f3rego zadaniem jest przekszta\u0142cenie wpisanego wzoru matematycznego na obliczaln\u0105 funkcj\u0119.
 * Poprzez zmian\u0119 parametru o zdeklarowany krok, wykonuje seri\u0119 oblicze\u0144 i zwraca list\u0119 puntk\u00f3w nale\u017c\u0105cych do funkcji.
 * Algorym rekurencyjny jest do\u015b\u0107 prostym algorytmem i opiera si\u0119 na ponownym wywo\u0142uwaniu odpowiednich metod a\u017c do zako\u0144czenia wyra\u017cenia.
 * Po poprawnym przetworzeniu okre\u015blonej cz\u0119\u015b\u0107i r\u00f3wnania zostaje ona wyci\u0119ta i przetwarzanie biegnie dalej.
 */
class MathParser {
    private int actualPosition = -1, actualChar, progress = 0, stepCount = 0;
    private String equalisation;
    private ParserProgress parserProgress;


    /**
     * G\u0142\u00f3wna metoda obiektu, wylicza ilo\u015bc krok\u00f3w jakie b\u0119dzie musia\u0142a podj\u0105\u0107 by wyliczy\u0107 wszystkie punkty funkcji.
     * Na ich podstawie zwraca aktualy progress.
     * Poprawia r\u00f3wnanie matematyczne.
     * Dla kolejnych warto\u015bci generuje dzia\u0142anie i wyzwala je w parserze.
     * Zwraca list\u0119 punkt\u00f3w funkcji w danym przedziale.
     *
     * @param startValue - warto\u015b\u0107 pocz\u0105tkowa
     * @param endValue - warto\u015b\u0107 ko\u0144cowa
     * @param equalisation - wyra\u017cenie w postaci stringu
     * @param step - krok co jaki wykonywane jest obliczenie
     * @param parserProgress - aktualny post\u0119p operacji
     * @return - lista punkt\u00f3w nale\u017c\u0105cych do funkcji
     */
    List<Point2D.Double> parseAndGetResult(String equalisation, int startValue, int endValue, float step, ParserProgress parserProgress) throws ParserException {
        this.actualPosition = -1;
        this.parserProgress = parserProgress;

        List<Point.Double> resultList = new ArrayList<>();
        equalisation = addMultiplicationIfMissing(equalisation);
        stepCount = (int) (Math.ceil(endValue - startValue) / step);

        for (double i = startValue, stepProgress = 0; i <= endValue; i += step, stepProgress++) {
            this.equalisation = equalisation.replace("x", new BigDecimal(i).toPlainString());
            double calculatedValue = calculateEqualisation();

            if (hasPositiveScientificNotation(calculatedValue))
                throw new ParserException(ParserException.MAX_REACHED, "");

            resultList.add(new Point.Double(i, calculatedValue));
            changeProgress((int) stepProgress);
            actualPosition = -1;
        }

        return resultList;
    }

    /**
     * Dodaje brakuj\u0105ce znaki mno\u017cenia
     * @param string - wyra\u017cenie matematyczne
     * @return - poprawione wyra\u017cenie matematyczne
     */
    private String addMultiplicationIfMissing(String string) {
        StringBuilder builder = new StringBuilder();
        char previousChar = ' ';

        for (int i = 0; i < string.length(); i++) {
            char actualChar = string.charAt(i);
            if (actualChar == 'x' && Character.isDigit(previousChar))
                builder.append("*");

            if (actualChar != ' ')
                previousChar = actualChar;

            builder.append(actualChar);
        }
        return builder.toString();
    }

    /**
     * Sprawdza czy warto\u015b\u0107 nie przekracza zakresu (maksymalny zakres ustali\u0142em na moment pojawienia si\u0119 notacji naukowych)
     * @param value - sprawdzana warto\u015b\u0107
     */
    private boolean hasPositiveScientificNotation(double value) {
        String valueString = String.valueOf(value);
        return (valueString.contains("E") || valueString.contains("e")) &&
                (!valueString.contains("E-") && !valueString.contains("e-"));
    }

    /**
     * Oblicza aktualny post\u0119p
     * @param actualStep - aktualnie wykonany krok
     */
    private void changeProgress(int actualStep) {
        int previousProgress = progress;
        progress = (int) Math.ceil(((float) actualStep / (float) stepCount) * 100f);

        if (progress > previousProgress && parserProgress != null && progress <= 100)
            parserProgress.onProgressChange(progress);
    }

    /**
     * Rozpoczyna algorym rekurencyjny.
     */
    private double calculateEqualisation() throws ParserException {
        setActualChar();
        Double calculatedValue = parseExpression();

        if (actualPosition < equalisation.length())
            throw new ParserException(ParserException.UNKNOWN_CHAR, String.valueOf((char) actualChar));

        return calculatedValue;
    }

    /**
     * Ustawia aktualny znak
     */
    private void setActualChar() {
        actualChar = (++actualPosition < equalisation.length()) ? equalisation.charAt(actualPosition) : -1;
    }

    /**
     * Sprawdza czy nast\u0119puje dodawanie czy odejmowaie
     */
    private double parseExpression() throws ParserException {
        double calculatedValue = parseTerm();
        for (; ; ) {
            if (isAddition())
                calculatedValue += parseTerm();

            else if (isSubtraction())
                calculatedValue -= parseTerm();

            else return calculatedValue;
        }
    }

    private boolean isAddition() {
        return removeChar('+');
    }

    private boolean isSubtraction() {
        return removeChar('-');
    }

    /**
     * Usuwa znak z r\u00f3wnania
     */
    private boolean removeChar(int charToRemove) {
        iterateUntilNotEmpty();

        if (actualChar == charToRemove) {
            setActualChar();
            return true;
        }
        return false;
    }

    private void iterateUntilNotEmpty() {
        while (actualChar == ' ') setActualChar();
    }

    /**
     * Sprawdza czy wyst\u0119puje mno\u017cenie lub dzielenie
     */
    private double parseTerm() throws ParserException {
        double calculatedValue = parseFactor();
        for (; ; ) {
            if (isMultiplication())
                calculatedValue *= parseFactor();

            else if (isDivision()) {
                double value = parseFactor();
                if (value == 0)
                    throw new ParserException(ParserException.ZERO_DIVISION, "");
                calculatedValue /= value;

            } else if(isExponentiation())
                calculatedValue = Math.pow(calculatedValue, parseFactor());
            else return calculatedValue;
        }
    }

    private boolean isMultiplication() {
        return removeChar('*');
    }

    private boolean isDivision() {
        return removeChar('/');
    }

    private boolean isExponentiation() {
        return removeChar('^');
    }

    /**
     * Sprawdza ewentualn\u0105 negacje funkcji
     * Bada czy podane wyra\u017cenie jest znanie i je\u015bli tak przetwa\u017ca je
     * Odczytuje warto\u015b\u0107 dla wyra\u017cenia z nawias\u00f3w
     */
    private double parseFactor() throws ParserException {
        double calculatedValue;
        int startPos = this.actualPosition;

        if (isUnaryPlus())
            return parseFactor();

        if (isUnaryMinus())
            return -parseFactor();

        if (isParenthesis()) {
            calculatedValue = parseExpression();
            removeChar(')');

        } else if (isCharNumber()) {
            calculatedValue = readNumber(startPos);

        } else if (isCharLetter()) {
            String function = readFunction(startPos);
            calculatedValue = parseFactor();
            calculatedValue = calculateFunction(function, calculatedValue);

        } else
            throw new ParserException(ParserException.UNKNOWN_CHAR, String.valueOf((char) actualChar));

        return calculatedValue;
    }

    private boolean isUnaryPlus() {
        return removeChar('+');
    }

    private boolean isUnaryMinus() {
        return removeChar('-');
    }

    private boolean isParenthesis() {
        return removeChar('(');
    }

    private boolean isCharNumber() {
        return (actualChar >= '0' && actualChar <= '9') || actualChar == '.';
    }

    private double readNumber(int startPos) {
        while ((actualChar >= '0' && actualChar <= '9') || actualChar == '.') setActualChar();
        return Double.parseDouble(equalisation.substring(startPos, this.actualPosition));
    }

    private boolean isCharLetter() {
        return actualChar >= 'a' && actualChar <= 'z';
    }

    private String readFunction(int startPos) {
        while (actualChar >= 'a' && actualChar <= 'z') setActualChar();
        return equalisation.substring(startPos, this.actualPosition);
    }

    /**
     * Oblicza funkcj\u0119 dla podanego parametru na podstawie jej nazwy
     * @param function - funkcja matematyczna
     * @param x - warto\u015b\u0107 dla kt\u00f3rej b\u0119dzie obliczana funkcja
     */
    private double calculateFunction(String function, double x) throws ParserException {
        switch (function) {
            case Strings.sqrtFunction:
                double sqrt = Math.sqrt(x);
                if (Double.isNaN(sqrt))
                    throw new ParserException(ParserException.SQRT_PROBLEM, String.valueOf(x));
                return sqrt;

            case Strings.sineFunction:
                return Math.sin(x);

            case Strings.cosineFunction:
                return Math.cos(x);

            case Strings.tangentFunction:
                return Math.tan(x);

            case Strings.logarithmFunction:
                return Math.log(x);

            default:
                throw new ParserException(ParserException.UNKNOWN_FUNCTION, function);
        }
    }

    interface ParserProgress {
        void onProgressChange(int percentageProgress);
    }
}
