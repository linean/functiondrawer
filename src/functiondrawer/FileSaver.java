package functiondrawer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Klasa obs\u0142uguje zapisywanie narysowanego wykresu do pliku. Umo\u017cliwia wybranie dw\u00f3ch podstawowych format\u00f3w
 * i lokalizacji, gdzie plik ma zosta\u0107 zapisany. Wy\u0142apuje ewentualne b\u0142\u0119dy zapisu.
 */
class FileSaver {
    private JFileChooser fileChooser;
    private BufferedImage image;

    /**
     * Zapisuje obraz
     * @param image - obraz kt\u00f3ry ma zosta\u0107 zapisany
     * @return - powodzenie operacji
     */
    boolean saveGraphic(BufferedImage image) {
        this.image = image;
        initFileChooser();
        setFileFilters();
        return chooseAndSaveFile();
    }

    private void initFileChooser() {
        fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setSelectedFile(new File(Strings.defaultSaveFileName));
    }

    /**
     * Ustawia formaty w jakim mo\u017cna zapisa\u0107 obraz
     */
    private void setFileFilters() {
        FileNameExtensionFilter
                jpgFilter = new FileNameExtensionFilter("JPG", "jpg"),
                pngFilter = new FileNameExtensionFilter("PNG", "png");

        fileChooser.addChoosableFileFilter(jpgFilter);
        fileChooser.addChoosableFileFilter(pngFilter);
        fileChooser.setAcceptAllFileFilterUsed(false);
    }

    /**
     * Ustala lokalizacj\u0119 zapisania pliku i wykonuje operacj\u0119 zapisania
     * @return - powodzenie operacjis
     */
    private boolean chooseAndSaveFile() {
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String extension = fileChooser.getFileFilter().getDescription();
            String path = fileChooser.getSelectedFile().getAbsolutePath();

            try {
                return saveFile(extension, path);

            } catch (IOException e) {
                return false;
            }

        } else
            return false;
    }

    /**
     * Zapisuje plik
     * @return - powodzenie operacji
     */
    private boolean saveFile(String extension, String path) throws IOException {
        path = removeDotFromPath(path);
        switch (extension) {
            case "JPG":
                path = path + ".jpg";
                ImageIO.write(image, "JPG", new File(path));
                return true;

            case "PNG":
                path = path + ".png";
                ImageIO.write(image, "PNG", new File(path));
                return true;

            default:
                return false;
        }
    }

    /**
     * Usuwa typ pliku z wybranej lokalizacji
     * @param path - wybrana lokalziacja
     * @return - wybrana lokalizacja bez ko\u0144c\u00f3wki pliku
     */
    private String removeDotFromPath(String path) {
        if (path.indexOf('.') > 0)
            return path.substring(0, path.indexOf('.'));
        return path;
    }
}
