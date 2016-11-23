package functiondrawer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Klasa tworz\u0105ca i zarz\u0105dzaj\u0105ca widokiem.
 * Ca\u0142o\u015bc zosta\u0142a oparta o JFrame i layout GridBag, kt\u00f3ry umo\u017cliwia dowolne skalowanie ona aplikacji
 * i zapewnia dososowanie zawarto\u015bci do jego rozmiar\u00f3w.
 * Odbierane tu s\u0105 komendy zastosowane przez u\u017cytkownika, jak r\u00f3wnie\u017c wy\u015bwietlane s\u0105 efekty pracy programu.
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
     * Metoda tworzy ramk\u0119 JFrame i ustawia w niej GridBagLayout
     * Ustawione s\u0105 domy\u015blne rozmiary okna, jego tytu\u0142 i ikona
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
     * Sk\u0142ada si\u0119 on z 7 kolumn i 3 wierszy.
     * Pierwszy wiersz wraz z ostatni\u0105 kolumn\u0105 s\u0105 dynamicznie dostosowywane do rozmiaru okna, pozosta\u0142e zachowuj\u0105 t\u0105 sam\u0105 wielko\u015bc
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
     * Metoda tworzy wszystkie komponenety, kt\u00f3re zostan\u0105 u\u017cyte w widoku
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
     * Metoda tworzy parametry siatki dla ka\u017cdego z wy\u017cej utworzonych komponent\u00f3w,
     * tak by m\u00f3\u0107 umie\u015bci\u0107 je w ramce w odpowiednich miejscach
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
     * Metoda ustawia nas\u0142uchiwanie naci\u015bni\u0119cia przycisk\u00f3w
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
     * Metoda odczytuje warto\u015bci kt\u00f3re mog\u0105 zosta\u0107 wpisane przez u\u017cytkownika
     * Zwraca b\u0142\u0105d je\u015bli s\u0105 nieprawid\u0142owe
     * @return - true je\u015bli wszystkie pola s\u0105 poprawne
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
     * Zmienia warto\u015bci przedzia\u0142u, je\u015bli zosta\u0142y wpisane nieprawid\u0142owo
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
