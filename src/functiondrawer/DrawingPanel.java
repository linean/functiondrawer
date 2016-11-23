package functiondrawer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa obs\u0142uguj\u0119 JPanel umieszczony w aplikacji. Jej zadaniem jest nayrsowanie uk\u0142adu wsp\u00f3\u0142rz\u0119dnych i wykresu funkcji
 * na podstawie obliczonych przez parser punkt\u00f3w. Dodatkowo umo\u017cliwia ona wy\u015bwietlanie informacji tekstowych w przypadku
 * wyst \u0105pienia komunikat\u00f3w dla uzytkownika.
 * Na podstawie listy obliczonych punkt\u00f3w, zostaje automatycznie wyznaczony zakres i skala ryskunku kt\u00f3ry zostanie wykonany.
 * Kroki uk\u0142adu wsp\u00f3\u0142rz\u0119dnych dobierane s \u0105 tak by ich ilo\u015bc nie przekracza\u0142a dziesi\u0119ciu.
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
     * Ustawia wiadomo\u015b\u0107 b\u0142\u0119du - kolor czerwony
     * @param message - wiadomo\u015bc do wy\u015bwietlenia
     */
    void setErrorMessage(String message) {
        if (message != null) {
            this.errorMessage = message;
            repaint();
        }
    }

    /**
     * Ustawia wiadomo\u015b\u0107 - kolor zielony
     * @param message - wiadomo\u015bc do wy\u015bwietlenia
     */
    void setMessage(String message) {
        if (message != null) {
            this.message = message;
            repaint();
        }
    }

    /**
     * Ustawia punkty na podstawie kt\u00f3rych ma zosta\u0107 narysowany wykres
     * @param pointsList - lista puntk\u00f3w
     */
    void setFunctionPoints(List<Point.Double> pointsList) {
        this.pointsList = pointsList;
        repaint();
    }

    /**
     * @return aktualnie narysowan \u0105 grafik\u0119 w postaci zabuforowanego obrazu
     */
    BufferedImage getBufferedGraphic() {
        BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();
        paint(g);

        return bufferedImage;
    }

    /**
     * G\u0142\u00f3wna metoda obiektu, zostaje wywo\u0142ana ka\u017cdorazowo gdy JPanel ma zosta\u0107 narysowany,
     * steruje ona b\u0142\u0119dami i rysowaniem funkcji
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
     * Zwraca aktualn \u0105 wielko\u015b\u0107 ekranu
     */
    private void measureScreen() {
        width = getWidth();
        height = getHeight();
    }

    /**
     * Wy\u015bwietla b\u0142 \u0105d
     */
    private void showErrorMessage() {
        graphic.setPaint(ERROR_COLOR);
        graphic.setFont(new Font("TimesRoman", Font.PLAIN, MESSAGE_FONT_SIZE));
        graphic.drawString(errorMessage, 10, height - 10);
        errorMessage = "";
    }

    /**
     * Wy\u015bwietla informacj\u0119
     */
    private void showMessage() {
        graphic.setPaint(MESSAGE_COLOR);
        graphic.setFont(new Font("TimesRoman", Font.PLAIN, MESSAGE_FONT_SIZE));
        graphic.drawString(message, 10, height - 10);
        message = "";
    }

    /**
     * Ustala \u015brodek JPanel'u
     */
    private void setCoordinateCenter() {
        graphic.translate(width / 2, height / 2);
    }

    /**
     * Na podstawie listy przes\u0142anych punkt\u00f3w ustala maksymaln \u0105 istniej \u0105c \u0105 warto\u015b\u0107 X i Y
     */
    private void setLimits() {
        biggestX = (int) Math.ceil(getBiggestAbsX());
        biggestY = (int) Math.ceil(getBiggestAbsY());

        biggestX += getDivider(biggestX);
        biggestY += getDivider(biggestY);
    }

    /**
     * Zwraca najwi\u0119ksz \u0105 warto\u015b\u0107 bezwzgl\u0119dn \u0105 liczby X
     */
    private double getBiggestAbsX() {
        double result = 0;
        for (Point.Double point : pointsList)
            if (Math.abs(point.getX()) > result)
                result = Math.abs(point.getX());
        return result;
    }

    /**
     * Zwraca najwi\u0119ksz \u0105 warto\u015b\u0107 bezwzgl\u0119dn \u0105 liczby Y
     */
    private double getBiggestAbsY() {
        double result = 0;
        for (Point.Double point : pointsList)
            if (Math.abs(point.getY()) > result)
                result = Math.abs(point.getY());
        return result;
    }

    /**
     * Na podstawie maksymalnych warto\u015bci ustala jak \u0105 skal\u0119 przyjmie o\u015b X i Y
     */
    private void setScale() {
        xScale = (width / ((float) biggestX * (2 + DRAWING_SIDE_MARGIN)));
        yScale = (height / ((float) biggestY * (2 + DRAWING_SIDE_MARGIN)));
    }

    /**
     * Rysuje linie uk\u0142adu wsp\u00f3\u0142rz\u0119dnych
     */
    private void drawCoordinateLines() {
        graphic.setPaint(COORDINATE_COLOR);
        graphic.draw(new Line2D.Double(0, 0, biggestX * xScale, 0));
        graphic.draw(new Line2D.Double(0, 0, -biggestX * xScale, 0));
        graphic.draw(new Line2D.Double(0, 0, 0, biggestY * yScale));
        graphic.draw(new Line2D.Double(0, 0, 0, -biggestY * yScale));
    }

    /**
     * Rysuje oznaczenia uk\u0142adu wsp\u00f3\u0142rz\u0119dnych w odpowiednich odleg\u0142o\u015bciach i wyliczonych warto\u015bciach
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
     * Zwraca dzielnik okre\u015blonej warto\u015bci gdy jest wi\u0119ksza ni\u017c 10
     * @param var - warto\u015b\u0107 dla kt\u00f3rej ma zosta\u0107 zwr\u00f3cny dzielnik
     */
    private int getDivider(int var) {
        if (var / 10 > 0)
            return (int) Math.ceil(((double) var) / 10);
        else
            return 1;
    }

    /**
     * Rysuje znacznik X
     * @param value - warto\u015b\u0107 markera
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
     * @param value - warto\u015b\u0107 jaka zostanie wpisana na markerze
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
     * \u0142 \u0105czy lini \u0105 podane punkty, tworz \u0105c jednocze\u015bnie wykres funkcji
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
