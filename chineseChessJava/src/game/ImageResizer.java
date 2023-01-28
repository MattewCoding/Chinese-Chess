package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Redimentionner une image
 * @author Ryan, Mattew
 */
public class ImageResizer {
	
    /**
     * Redimensionne une image à une largeur et une hauteur absolues
     * L'image peut ne pas être proportionnelle.
     * @param inputImagePath Chemin de l'image originale
     * @param outputImagePath Chemin pour sauvegarder l'image redimensionnée
     * @param scaledWidth Largeur absolue en pixels
     * @param scaledHeight Hauteur absolue en pixels
     * @throws IOException Lévée si le fichier d'entrée est introuvable
     */
    public static void resize(String inputImagePath, String outputImagePath, int scaledWidth, int scaledHeight) throws IOException {
        // reads input image
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);
 
        // creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());
 
        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
 
        // extracts extension of output file
        String formatName = outputImagePath.substring(outputImagePath
                .lastIndexOf(".") + 1);
 
        // writes to output file
        ImageIO.write(outputImage, formatName, new File(outputImagePath));
    }
 
    /**
     * Redimensionne une image selon un pourcentage de la taille d'origine (proportionnel).
     * @param inputImagePath Chemin de l'image originale
     * @param outputImagePath Chemin pour sauvegarder l'image redimensionnée
     * @param percent un nombre double spécifie le pourcentage de l'image de sortie par rapport à l'image d'entrée.
     * @throws IOException Lévée si le fichier d'entrée est introuvable
     */
    public static void resize(String inputImagePath, String outputImagePath, double percent) throws IOException {
        File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);
        int scaledWidth = (int) (inputImage.getWidth() * percent);
        int scaledHeight = (int) (inputImage.getHeight() * percent);
        resize(inputImagePath, outputImagePath, scaledWidth, scaledHeight);
    }
}

