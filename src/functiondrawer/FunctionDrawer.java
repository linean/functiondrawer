package functiondrawer;

import java.awt.*;

/**
 * Klasa startowa, inicjalizuje ona obiekt parsera matematycznego stringu, obiekt widoku i obiekt prezentera,
 * które kolejno sterują odpowiednimi częściami aplikacji. Dodatkowo klasa pełni funkcję modelu w koncepcji MVP i przejmuje błędy parsera.
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
     * Metoda od której aplikacja rozpoczyna pracę, zostaje tu utworzony obiekt głównej klasy FunctionDrawer, który
     * następnie steruje aplikacją
     */
    public static void main(String[] args) {
        new FunctionDrawer();
    }

    private FunctionDrawer() {
        startApp();
    }

    /**
     * Metoda tworzy obiekty sterujące elementami programu
     * mathParser - wykonuje parsowanie stringu matematycznego do równania
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
     * Metoda przyjmuje w parametrze wyrażenie matematyczne w postaci stringu i dwa zakresy w których ma je przetwożyć
     * Za pomocą obiektu parsera wykonuje obliczenia i rezultat zwraca do interfejsu prezentera
     * @param function - funkcja w postaci stringu
     * @param startValue - wartość początkowa
     * @param endValue - wartość końcowa
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
     * @return - aktualny postęp w procentach
     */
    private MathParser.ParserProgress onParserProgressChange() {
        return percentageProgress -> {
            if (presenterInterface != null)
                presenterInterface.onCalculationProgressChange(percentageProgress);
        };
    }


    /**
     * Metoda zostaje wywołana gdy parser natrafi na jakiś wyjątek. Jej zadaniem jej przesłanie odpowiedniego błędu do
     * prezentera
     * @param exception - błąd jaki wysątpił
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
