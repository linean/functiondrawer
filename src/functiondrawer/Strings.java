package functiondrawer;

/**
 * Dodatkowa klasa przechowująca teksty użyte w aplikacji.
 */
class Strings {
    final static String appName = "Program do rysowania wykresu funkcji";
    final static String fileSavedSuccess = "Zapisano wykres";
    final static String fileSavedError = "Nie udało się zapisać wykresu";
    final static String defaultSaveFileName = "wykres";
    final static String sqrtFunction = "sqrt";
    final static String sineFunction = "sin";
    final static String cosineFunction = "cos";
    final static String tangentFunction = "tan";
    final static String logarithmFunction = "log";
    final static String unexpectedChar = "Nieoczekiwany znak:";
    final static String unknownFunction = "Nieznana funkcja:";
    final static String drawButton = "Wyznacz wykres";
    final static String enterFunctionName = "Podaj funkcję:";
    final static String enterSection = "Podaj przedział:";
    final static String saveButton = "Zapisz";
    final static String xSection = "\u2264 x \u2264";
    final static String enterFunctionWarning = "Podaj wzór funkcji.";
    final static String lowSectionWarning = "Podaj poprawny dolny zakres przedziału.";
    final static String highSectionWarning = "Podaj poprawny górny zakres przedziału.";
    final static String toHighValue = "Funkcja przybiera zbyt duże wartości. Spróbuj zmniejszyć przedział.";
    final static String functionLeftSide = "y = ";
    final static String done = "Wykonano:";
    final static String unableToCalculateSqrt = "Nie można policzyć pierwiastka z";
    final static String zeroDivisionWarning = "Wykryto dzielenie przez zero.";
    final static String unableToFinishCalculation = "Wystąpił problem podczas wykonywania obliczenia";
    final static String onlyInteger = "Tylko liczba całkowita";

    final static String help = "<html><p><strong>Dostępne funkcje:</strong></p>\n" +
            "<p><strong>+</strong> &nbsp;dodawanie<br /><strong>-</strong> &nbsp;odejmowanie<br /><strong>*</strong> &nbsp;mnożenie<br /><strong>/</strong> &nbsp;dzielenie<br /><strong>^n</strong> &nbsp;potęgowanie do wykładnika n<br /><strong>sqrt(wartość)</strong> &nbsp;pierwiatek<br /><strong>log(wartość)</strong> &nbsp;logarytm&nbsp;<br /><strong>sin(wartość)</strong> sinus&nbsp;<br /><strong>cos(wartość)</strong> cosinus<br /><strong>tan(wartość)</strong> tangens</p>\n" +
            "<p style=\"text-align: left; padding-left: 120px;\">Wykonał <em><strong>Maciej Sady</strong></em></p></html>";
}
