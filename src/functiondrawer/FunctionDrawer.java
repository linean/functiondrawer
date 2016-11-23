package functiondrawer;

import java.awt.*;

/**
 * Klasa startowa, inicjalizuje ona obiekt parsera matematycznego stringu, obiekt widoku i obiekt prezentera,
 * kt\u00f3re kolejno steruj\u0105 odpowiednimi cz\u0119\u015bciami aplikacji. Dodatkowo klasa pe\u0142ni funkcj\u0119 modelu w koncepcji MVP i przejmuje b\u0142\u0119dy parsera.
 *
 * by Maciej Sady
 */
public class FunctionDrawer {
    private final static float PARSER_STEP = 0.01f;
    private View view;
    private PresenterInterface presenterInterface;
    private MathParser mathParser;
    private Presenter presenter;

    /**
     * Metoda od kt\u00f3rej aplikacja rozpoczyna prac\u0119, zostaje tu utworzony obiekt g\u0142\u00f3wnej klasy FunctionDrawer, kt\u00f3ry
     * nast\u0119pnie steruje aplikacj \u0105
     */
    public static void main(String[] args) {
        new FunctionDrawer();
    }

    private FunctionDrawer() {
        startApp();
    }

    /**
     * Metoda tworzy obiekty steruj \u0105ce elementami programu
     * mathParser - wykonuje parsowanie stringu matematycznego do r\u00f3wnania
     * view - steruje widokiem i  tworzy UI
     * presenter - kontroluje view
     */
    private void startApp() {
        EventQueue.invokeLater(() -> {
            try {
                mathParser = new MathParser();
                view = new View();
                presenter = new Presenter(view, this);

                presenterInterface = presenter;
                view.setUserActionListener(presenter);


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Metoda przyjmuje w parametrze wyra\u017cenie matematyczne w postaci stringu i dwa zakresy w kt\u00f3rych ma je przetwo\u017cy\u0107
     * Za pomoc \u0105 obiektu parsera wykonuje obliczenia i rezultat zwraca do interfejsu prezentera
     * @param function - funkcja w postaci stringu
     * @param startValue - warto\u015b\u0107 pocz \u0105tkowa
     * @param endValue - warto\u015b\u0107 ko\u0144cowa
     */
    void calculateFunction(String function, int startValue, int endValue) {
        new Thread(() -> {
            java.util.List<Point.Double> result;
            try {
                result = mathParser.parseAndGetResult(function, startValue, endValue, PARSER_STEP, onParserProgressChange());
                if (presenterInterface != null)
                    presenterInterface.onCalculationSuccess(result);


            } catch (ParserException e) {
                onParserError(e);
            }
        }).start();
    }

    /**
     * Metoda odbiera aktualny stan parsera wyliczony w procentach i przekazuje do go prezentera
     * @return - aktualny post\u0119p w procentach
     */
    private MathParser.ParserProgress onParserProgressChange() {
        return percentageProgress -> {
            if (presenterInterface != null)
                presenterInterface.onCalculationProgressChange(percentageProgress);
        };
    }


    /**
     * Metoda zostaje wywo\u0142ana gdy parser natrafi na jaki\u015b wyj \u0105tek. Jej zadaniem jej przes\u0142anie odpowiedniego b\u0142\u0119du do
     * prezentera
     * @param exception - b\u0142 \u0105d jaki wys \u0105tpi\u0142
     */
    private void onParserError(ParserException exception) {
        if (presenterInterface != null)
            switch (exception.getErrorCode()) {
                case ParserException.UNKNOWN_CHAR:
                    presenterInterface.onCalculationError(Strings.unexpectedChar + " " + exception.getInfo());
                    break;

                case ParserException.UNKNOWN_FUNCTION:
                    presenterInterface.onCalculationError(Strings.unknownFunction + " " + exception.getInfo());
                    break;

                case ParserException.SQRT_PROBLEM:
                    presenterInterface.onCalculationError(Strings.unableToCalculateSqrt + " " + exception.getInfo());
                    break;

                case ParserException.ZERO_DIVISION:
                    presenterInterface.onCalculationError(Strings.zeroDivisionWarning);
                    break;

                case ParserException.MAX_REACHED:
                    presenterInterface.onCalculationError(Strings.toHighValue);
                    break;

                default:
                    presenterInterface.onCalculationError(Strings.unableToFinishCalculation);
            }
    }

    interface PresenterInterface {
        void onCalculationSuccess(java.util.List<Point.Double> result);

        void onCalculationError(String message);

        void onCalculationProgressChange(int progress);
    }
}
