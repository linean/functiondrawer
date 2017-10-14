package functiondrawer;

import java.awt.*;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa obsługuje algorytm rekurencyjny, którego zadaniem jest przekształcenie wpisanego wzoru matematycznego na obliczalną funkcję.
 * Poprzez zmianę parametru o zdeklarowany krok, wykonuje serię obliczeń i zwraca listę puntków należących do funkcji.
 * Algorym rekurencyjny jest dość prostym algorytmem i opiera się na ponownym wywołuwaniu odpowiednich metod aż do zakończenia wyrażenia.
 * Po poprawnym przetworzeniu określonej częśći równania zostaje ona wycięta i przetwarzanie biegnie dalej.
 */
class MathParser {
    private int actualPosition = -1, actualChar, progress = 0, stepCount = 0;
    private String equalisation;
    private ParserProgress parserProgress;


    /**
     * Główna metoda obiektu, wylicza ilość kroków jakie będzie musiała podjąć by wyliczyć wszystkie punkty funkcji.
     * Na ich podstawie zwraca aktualy progress.
     * Poprawia równanie matematyczne.
     * Dla kolejnych wartości generuje działanie i wyzwala je w parserze.
     * Zwraca listę punktów funkcji w danym przedziale.
     *
     * @param startValue - wartość początkowa
     * @param endValue - wartość końcowa
     * @param equalisation - wyrażenie w postaci stringu
     * @param step - krok co jaki wykonywane jest obliczenie
     * @param parserProgress - aktualny postęp operacji
     * @return - lista punktów należących do funkcji
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
     * Dodaje brakujące znaki mnożenia
     * @param string - wyrażenie matematyczne
     * @return - poprawione wyrażenie matematyczne
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
     * Sprawdza czy wartość nie przekracza zakresu (maksymalny zakres ustaliłem na moment pojawienia się notacji naukowych)
     * @param value - sprawdzana wartość
     */
    private boolean hasPositiveScientificNotation(double value) {
        String valueString = String.valueOf(value);
        return (valueString.contains("E") || valueString.contains("e")) &&
                (!valueString.contains("E-") && !valueString.contains("e-"));
    }

    /**
     * Oblicza aktualny postęp
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
     * Sprawdza czy następuje dodawanie czy odejmowaie
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
     * Usuwa znak z równania
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
     * Sprawdza czy występuje mnożenie lub dzielenie
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
     * Sprawdza ewentualną negacje funkcji
     * Bada czy podane wyrażenie jest znanie i jeśli tak przetważa je
     * Odczytuje wartość dla wyrażenia z nawiasów
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
     * Oblicza funkcję dla podanego parametru na podstawie jej nazwy
     * @param function - funkcja matematyczna
     * @param x - wartość dla której będzie obliczana funkcja
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
