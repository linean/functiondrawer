package functiondrawer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa obsługuję JPanel umieszczony w aplikacji. Jej zadaniem jest nayrsowanie układu współrzędnych i wykresu funkcji
 * na podstawie obliczonych przez parser punktów. Dodatkowo umożliwia ona wyświetlanie informacji tekstowych w przypadku
 * wystąpienia komunikatów dla uzytkownika.
 * Na podstawie listy obliczonych punktów, zostaje automatycznie wyznaczony zakres i skala ryskunku który zostanie wykonany.
 * Kroki układu współrzędnych dobierane są tak by ich ilośc nie przekraczała dziesięciu.
 */

class DrawingPanel extends JPanel {
    private static final int MARKER_HEIGHT = 3;
    private static final int MARKER_NUMBER_MARGIN = 5;
    private static final float DRAWING_SIDE_MARGIN = 0.2f;
    private static final int MESSAGE_FONT_SIZE = 20;
    private static final Color COORDINATE_COLOR = Color.BLACK;
    private static final Color FUNCTION_COLOR = Color.RED;
    private static final Color ERROR_COLOR = Color.RED;
    private static final Color MESSAGE_COLOR = Color.GREEN;
    private Graphics2D graphic;
    private int width, height;
    private String errorMessage = "", message = "";
    private List<Point.Double> pointsList = new ArrayList<>();
    private float xScale, yScale;
    private int biggestX, biggestY;

    /**
     * Ustawia wiadomość błędu - kolor czerwony
     * @param message - wiadomośc do wyświetlenia
     */
    void setErrorMessage(String message) {
        if (message != null) {
            this.errorMessage = message;
            repaint();
        }
    }

    /**
     * Ustawia wiadomość - kolor zielony
     * @param message - wiadomośc do wyświetlenia
     */
    void setMessage(String message) {
        if (message != null) {
            this.message = message;
            repaint();
        }
    }

    /**
     * Ustawia punkty na podstawie których ma zostać narysowany wykres
     * @param pointsList - lista puntków
     */
    void setFunctionPoints(List<Point.Double> pointsList) {
        this.pointsList = pointsList;
        repaint();
    }

    /**
     * @return aktualnie narysowaną grafikę w postaci zabuforowanego obrazu
     */
    BufferedImage getBufferedGraphic() {
        BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();
        paint(g);

        return bufferedImage;
    }

    /**
     * Główna metoda obiektu, zostaje wywołana każdorazowo gdy JPanel ma zostać narysowany,
     * steruje ona błędami i rysowaniem funkcji
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        graphic = (Graphics2D) g;

        measureScreen();

        if (errorMessage.length() > 0)
            showErrorMessage();

        else if (message.length() > 0)
            showMessage();

        else if (pointsList.size() > 0) {
            setCoordinateCenter();
            setLimits();
            setScale();
            drawCoordinateLines();
            drawMarkers();
            drawFunction();
        }
    }

    /**
     * Zwraca aktualną wielkość ekranu
     */
    private void measureScreen() {
        width = getWidth();
        height = getHeight();
    }

    /**
     * Wyświetla błąd
     */
    private void showErrorMessage() {
        graphic.setPaint(ERROR_COLOR);
        graphic.setFont(new Font("TimesRoman", Font.PLAIN, MESSAGE_FONT_SIZE));
        graphic.drawString(errorMessage, 10, height - 10);
        errorMessage = "";
    }

    /**
     * Wyświetla informację
     */
    private void showMessage() {
        graphic.setPaint(MESSAGE_COLOR);
        graphic.setFont(new Font("TimesRoman", Font.PLAIN, MESSAGE_FONT_SIZE));
        graphic.drawString(message, 10, height - 10);
        message = "";
    }

    /**
     * Ustala środek JPanel'u
     */
    private void setCoordinateCenter() {
        graphic.translate(width / 2, height / 2);
    }

    /**
     * Na podstawie listy przesłanych punktów ustala maksymalną istniejącą wartość X i Y
     */
    private void setLimits() {
        biggestX = (int) Math.ceil(getBiggestAbsX());
        biggestY = (int) Math.ceil(getBiggestAbsY());

        biggestX += getDivider(biggestX);
        biggestY += getDivider(biggestY);
    }

    /**
     * Zwraca największą wartość bezwzględną liczby X
     */
    private double getBiggestAbsX() {
        double result = 0;
        for (Point.Double point : pointsList)
            if (Math.abs(point.getX()) > result)
                result = Math.abs(point.getX());
        return result;
    }

    /**
     * Zwraca największą wartość bezwzględną liczby Y
     */
    private double getBiggestAbsY() {
        double result = 0;
        for (Point.Double point : pointsList)
            if (Math.abs(point.getY()) > result)
                result = Math.abs(point.getY());
        return result;
    }

    /**
     * Na podstawie maksymalnych wartości ustala jaką skalę przyjmie oś X i Y
     */
    private void setScale() {
        xScale = (width / ((float) biggestX * (2 + DRAWING_SIDE_MARGIN)));
        yScale = (height / ((float) biggestY * (2 + DRAWING_SIDE_MARGIN)));
    }

    /**
     * Rysuje linie układu współrzędnych
     */
    private void drawCoordinateLines() {
        graphic.setPaint(COORDINATE_COLOR);
        graphic.draw(new Line2D.Double(0, 0, biggestX * xScale, 0));
        graphic.draw(new Line2D.Double(0, 0, -biggestX * xScale, 0));
        graphic.draw(new Line2D.Double(0, 0, 0, biggestY * yScale));
        graphic.draw(new Line2D.Double(0, 0, 0, -biggestY * yScale));
    }

    /**
     * Rysuje oznaczenia układu współrzędnych w odpowiednich odległościach i wyliczonych wartościach
     */
    private void drawMarkers() {
        draw0Marker();

        int xStep = getDivider(biggestX);
        for (int i = 0; i <= (biggestX); i += xStep)
            drawXMarker(i, i * xScale);

        int yStep = getDivider(biggestY);
        for (int i = 0; i <= (biggestY); i += yStep)
            drawYMarker(i, i * yScale);
    }

    /**
     * Rysuje zero wykresu
     */
    private void draw0Marker() {
        graphic.drawString("0", 3, -3);
    }

    /**
     * Zwraca dzielnik określonej wartości gdy jest większa niż 10
     * @param var - wartość dla której ma zostać zwrócny dzielnik
     */
    private int getDivider(int var) {
        if (var / 10 > 0)
            return (int) Math.ceil(((double) var) / 10);
        else
            return 1;
    }

    /**
     * Rysuje znacznik X
     * @param value - wartość markera
     * @param xPosition - lokalizacji markera na osi X
     */
    private void drawXMarker(int value, float xPosition) {
        if (value != 0) {
            String valueString = String.valueOf(value);
            int textWidth = graphic.getFontMetrics().stringWidth(valueString);
            int markerPosition = (MARKER_HEIGHT + MARKER_NUMBER_MARGIN) * 2;
            graphic.draw(new Line2D.Double(xPosition, -MARKER_HEIGHT, xPosition, MARKER_HEIGHT));
            graphic.drawString(valueString, xPosition - textWidth / 2, markerPosition);
            graphic.draw(new Line2D.Double(-xPosition, -MARKER_HEIGHT, -xPosition, MARKER_HEIGHT));
            graphic.drawString("-" + valueString, -xPosition - textWidth / 2, markerPosition);
        }
    }

    /**
     * Rysuje znacznik Y
     * @param value - wartość jaka zostanie wpisana na markerze
     * @param yPosition - pozycja na osi Y
     */
    private void drawYMarker(int value, float yPosition) {
        if (value != 0) {
            String valueString = String.valueOf(value);
            int textHeight = graphic.getFontMetrics().getAscent() - graphic.getFontMetrics().getDescent();
            int markerPosition = (MARKER_HEIGHT + MARKER_NUMBER_MARGIN);
            graphic.draw(new Line2D.Double(-MARKER_HEIGHT, yPosition, MARKER_HEIGHT, yPosition));
            graphic.drawString("-" + valueString, markerPosition, yPosition + textHeight / 2);
            graphic.draw(new Line2D.Double(-MARKER_HEIGHT, -yPosition, MARKER_HEIGHT, -yPosition));
            graphic.drawString(valueString, markerPosition, -yPosition + textHeight / 2);
        }
    }

    /**
     * łączy linią podane punkty, tworząc jednocześnie wykres funkcji
     */
    private void drawFunction() {
        graphic.setPaint(FUNCTION_COLOR);
        Point.Double previousPoint = null;
        for (Point.Double point : pointsList) {
            if (previousPoint != null)
                graphic.draw(new Line2D.Double(previousPoint.getX() * xScale, -previousPoint.getY() * yScale,
                        point.getX() * xScale, -point.getY() * yScale));

            previousPoint = point;
        }
    }
}
