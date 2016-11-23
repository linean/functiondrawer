package functiondrawer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Klasa po\u015brednicz\u0105ca mi\u0119dzy modelem "FunctionDrawer" a jego widokiem "View".
 * Zajmuje si\u0119 przekazaniem odpowiednich komunikat\u00f3w i warto\u015bci mi\u0119dzy nimi.
 *
 * Nie opisuj\u0119 poszczeg\u00f3lnych method, poniewa\u017c s\u0105 one jedynie interfejsami kt\u00f3re przekazuj\u0105 informacje mi\u0119dzy modelem a widokiem
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
