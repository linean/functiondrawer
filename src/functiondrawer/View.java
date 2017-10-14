package functiondrawer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Klasa tworząca i zarządzająca widokiem.
 * Całość została oparta o JFrame i layout GridBag, który umożliwia dowolne skalowanie ona aplikacji
 * i zapewnia dostoswanie zawartości do jego rozmiarów.
 * Odbierane tu są komendy użytkownika, jak również wyświetlane są efekty pracy programu.
 */

class View {

    private final static int MARGIN_BOTTOM = 10;
    private final static int MARGIN_BELOW_PANEL = 10;
    private JFrame frame;
    private UserAction userAction;
    private DrawingPanel drawingPanel;
    private JTextField equalisationField, startValueField, endValueField;
    private JButton btnDraw, btnSave;
    private JLabel functionTitle, sectionTitle, xInterval, functionLeftSide;
    private GridBagConstraints drawingPanelGrid, equalisationFieldGrid, startValueFieldGird, endValueFieldGird,
            btnDrawGrid, btnSaveGird, functionTitleGrid, sectionTitleGrid, xIntervalGrid, functionLeftSideGrid;
    private Integer startValue, endValue;
    private String equalisation;

    /**
     * Utworzenie obiektu widoku automatycznie inicjalizuje go i ustawia jako widoczny
     */
    View() {
        createFrame();
        initComponents();
        initGrids();
        addToFrame();
        setListeners();
        setDrawingPanel();
        frame.setVisible(true);
    }

    void showError(String message) {
        drawingPanel.setErrorMessage(message);
    }

    void showMessage(String message) {
        drawingPanel.setMessage(message);
    }

    void drawFunction(List<Point.Double> pointList) {
        drawingPanel.setFunctionPoints(pointList);
    }

    void enableSaveButton() {
        btnSave.setEnabled(true);
    }

    void disableSaveButton() {
        btnSave.setEnabled(false);
    }

    void disableDrawButton() {
        btnDraw.setEnabled(false);
    }

    void enableDrawButton() {
        btnDraw.setEnabled(true);
        btnDraw.setText(Strings.drawButton);
    }

    void setProgress(int progress) {
        btnDraw.setText(Strings.done + " " + progress + "%");
    }

    void setUserActionListener(UserAction userAction) {
        this.userAction = userAction;
    }

    /**
     * Metoda tworzy ramkę JFrame i ustawia w niej GridBagLayout
     * Ustawione są domyślne rozmiary okna, jego tytuł i ikona
     */
    private void createFrame() {
        frame = new JFrame();
        frame.setBounds(100, 100, 750, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(createGirdBagLayout());
        frame.setTitle(Strings.appName);
        try {
            frame.setIconImage(new ImageIcon(FunctionDrawer.class.getResource("/functiondrawer/app_icon.png")).getImage());
        } catch (NullPointerException ignored) {
        }
    }

    /**
     * Metoda tworzy layout ramki
     * Składa się on z 7 kolumn i 3 wierszy.
     * Pierwszy wiersz wraz z ostatnią kolumną są dynamicznie dostosowywane do rozmiaru okna, pozostałe zachowują tą samą wielkość
     */
    private GridBagLayout createGirdBagLayout() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[]{0, 0, 0};
        gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
        gridBagLayout.rowWeights = new double[]{1.0, 0.0, 0.0};
        return gridBagLayout;
    }

    /**
     * Metoda tworzy wszystkie komponenety, które zostaną użyte w widoku
     */
    private void initComponents() {
        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

        drawingPanel = new DrawingPanel();
        functionTitle = new JLabel(Strings.enterFunctionName);
        sectionTitle = new JLabel(Strings.enterSection);
        xInterval = new JLabel(Strings.xSection);
        functionLeftSide = new JLabel(Strings.functionLeftSide);

        equalisationField = new JTextField();
        equalisationField.setColumns(15);
        equalisationField.setToolTipText(Strings.help);
        startValueField = new JTextField();
        startValueField.setColumns(4);
        startValueField.setToolTipText(Strings.onlyInteger);
        endValueField = new JTextField();
        endValueField.setColumns(4);
        endValueField.setToolTipText(Strings.onlyInteger);

        btnDraw = new JButton(Strings.drawButton);
        btnSave = new JButton(Strings.saveButton);
        btnSave.setEnabled(false);
    }

    /**
     * Metoda tworzy parametry siatki dla każdego z wyżej utworzonych komponentów,
     * tak by móć umieścić je w ramce w odpowiednich miejscach
     */
    private void initGrids() {
        drawingPanelGrid = new GridBagConstraints();
        drawingPanelGrid.gridx = 0;
        drawingPanelGrid.gridy = 0;
        drawingPanelGrid.gridwidth = 7;
        drawingPanelGrid.insets = new Insets(0, 0, MARGIN_BELOW_PANEL, 0);
        drawingPanelGrid.fill = GridBagConstraints.BOTH;

        functionTitleGrid = new GridBagConstraints();
        functionTitleGrid.gridwidth = 2;
        functionTitleGrid.gridx = 0;
        functionTitleGrid.gridy = 1;
        functionTitleGrid.insets = new Insets(0, 5, 5, 20);
        functionTitleGrid.anchor = GridBagConstraints.WEST;

        sectionTitleGrid = new GridBagConstraints();
        sectionTitleGrid.gridx = 2;
        sectionTitleGrid.gridy = 1;
        sectionTitleGrid.gridwidth = 3;
        sectionTitleGrid.anchor = GridBagConstraints.WEST;
        sectionTitleGrid.insets = new Insets(0, 0, 5, 5);

        equalisationFieldGrid = new GridBagConstraints();
        equalisationFieldGrid.gridx = 1;
        equalisationFieldGrid.gridy = 2;
        equalisationFieldGrid.fill = GridBagConstraints.HORIZONTAL;
        equalisationFieldGrid.insets = new Insets(0, 0, 10, 20);

        startValueFieldGird = new GridBagConstraints();
        startValueFieldGird.gridx = 2;
        startValueFieldGird.gridy = 2;
        startValueFieldGird.fill = GridBagConstraints.HORIZONTAL;
        startValueFieldGird.insets = new Insets(0, 0, MARGIN_BOTTOM, 5);

        xIntervalGrid = new GridBagConstraints();
        xIntervalGrid.gridx = 3;
        xIntervalGrid.gridy = 2;
        xIntervalGrid.insets = new Insets(0, 0, MARGIN_BOTTOM, 5);

        endValueFieldGird = new GridBagConstraints();
        endValueFieldGird.gridx = 4;
        endValueFieldGird.gridy = 2;
        endValueFieldGird.insets = new Insets(0, 0, MARGIN_BOTTOM, 15);
        endValueFieldGird.fill = GridBagConstraints.HORIZONTAL;

        btnDrawGrid = new GridBagConstraints();
        btnDrawGrid.gridx = 5;
        btnDrawGrid.gridy = 1;
        btnDrawGrid.gridheight = 2;
        btnDrawGrid.fill = GridBagConstraints.VERTICAL;
        btnDrawGrid.insets = new Insets(0, 0, MARGIN_BOTTOM, 5);

        btnSaveGird = new GridBagConstraints();
        btnSaveGird.gridx = 6;
        btnSaveGird.gridy = 1;
        btnSaveGird.gridheight = 2;
        btnSaveGird.insets = new Insets(0, 0, MARGIN_BOTTOM, 10);
        btnSaveGird.fill = GridBagConstraints.VERTICAL;
        btnSaveGird.anchor = GridBagConstraints.EAST;

        functionLeftSideGrid = new GridBagConstraints();
        functionLeftSideGrid.insets = new Insets(0, 10, MARGIN_BOTTOM, 5);
        functionLeftSideGrid.gridx = 0;
        functionLeftSideGrid.gridy = 2;
        functionLeftSideGrid.anchor = GridBagConstraints.EAST;
    }

    /**
     * Metoda dodaje komponenty do ramki wraz z ich parametrami lokalizacji
     */
    private void addToFrame() {
        frame.getContentPane().add(drawingPanel, drawingPanelGrid);
        frame.getContentPane().add(functionTitle, functionTitleGrid);
        frame.getContentPane().add(sectionTitle, sectionTitleGrid);
        frame.getContentPane().add(functionLeftSide, functionLeftSideGrid);
        frame.getContentPane().add(equalisationField, equalisationFieldGrid);
        frame.getContentPane().add(startValueField, startValueFieldGird);
        frame.getContentPane().add(xInterval, xIntervalGrid);
        frame.getContentPane().add(endValueField, endValueFieldGird);
        frame.getContentPane().add(btnDraw, btnDrawGrid);
        frame.getContentPane().add(btnSave, btnSaveGird);
    }


    /**
     * Metoda ustawia nasłuchiwanie naciśnięcia przycisków
     */
    private void setListeners() {
        btnDraw.addActionListener(arg0 -> {
            if (userAction != null && readValues()) {
                userAction.onDrawButtonClick(equalisation, startValue, endValue);
                resetValues();
            }
        });

        btnSave.addActionListener(arg0 -> {
            if (userAction != null) {
                userAction.onSaveButtonClick(drawingPanel.getBufferedGraphic());
            }
        });
    }

    private void setDrawingPanel() {
        drawingPanel.setBackground(new java.awt.Color(255, 255, 255));
    }

    /**
     * Metoda odczytuje wartości które mogą zostać wpisane przez użytkownika
     * Zwraca błąd jeśli są nieprawidłowe
     * @return - true jeśli wszystkie pola są poprawne
     */
    private boolean readValues() {
        equalisation = equalisationField.getText();
        if (equalisation.length() < 1) {
            showError(Strings.enterFunctionWarning);
            return false;
        }

        try {
            startValue = Integer.valueOf(startValueField.getText());
            endValue = Integer.valueOf(endValueField.getText());

            if (startValue > endValue)
                switchValues();

            return true;

        } catch (NumberFormatException e) {
            if (startValue == null)
                showError(Strings.lowSectionWarning);

            else if (endValue == null)
                showError(Strings.highSectionWarning);

            return false;
        }
    }

    /**
     * Zmienia wartości przedziału, jeśli zostały wpisane nieprawidłowo
     */
    private void switchValues() {
        int temp = startValue;
        startValueField.setText(String.valueOf(endValue));
        endValueField.setText(String.valueOf(startValue));
        startValue = endValue;
        endValue = temp;
    }

    private void resetValues() {
        equalisation = "";
        startValue = null;
        endValue = null;
    }

    interface UserAction {
        void onDrawButtonClick(String equalisation, int startValue, int endValue);

        void onSaveButtonClick(BufferedImage image);
    }
}
