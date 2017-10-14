package functiondrawer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Klasa pośrednicząca między modelem "FunctionDrawer" a jego widokiem "View".
 * Zajmuje się przekazaniem odpowiednich komunikatów i wartości między nimi.
 *
 * Nie opisuję poszczególnych method, ponieważ są one jedynie interfejsami które przekazują informacje między modelem a widokiem
 */

class Presenter implements View.UserAction, FunctionDrawer.PresenterInterface {
    private final View view;
    private final FunctionDrawer model;

    Presenter(View view, FunctionDrawer model) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void onSaveButtonClick(BufferedImage image) {
        boolean result = new FileSaver().saveGraphic(image);
        if (result)
            view.showMessage(Strings.fileSavedSuccess);
        else
            view.showError(Strings.fileSavedError);

        view.disableSaveButton();
    }

    @Override
    public void onDrawButtonClick(String equalisation, int startValue, int endValue) {
        view.disableSaveButton();
        view.disableDrawButton();

        model.calculateFunction(equalisation, startValue, endValue);
    }

    @Override
    public void onCalculationSuccess(List<Point.Double> result) {
        view.drawFunction(result);
        view.enableSaveButton();
        view.enableDrawButton();
    }

    @Override
    public void onCalculationError(String message) {
        view.showError(message);
        view.enableDrawButton();
        view.disableSaveButton();
    }

    @Override
    public void onCalculationProgressChange(int progress) {
        view.setProgress(progress);
    }
}
