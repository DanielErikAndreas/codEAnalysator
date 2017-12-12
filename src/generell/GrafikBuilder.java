package generell;

import graphen.Graph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GrafikBuilder {
  private NachrichtenManager nachrichtenManager;
  private int höhe = 0;
  private int breite;
  private int paddingTop = 30;
  private int linksAusrichtung = 300;
  private int statistikBreite = 300;

  private boolean isStatistik = true;
  private BufferedImage grafik;
  private Font font = new Font("Monospaced", Font.PLAIN, 12); // Dialog
  private Color statistikSchriftFarbe = Color.BLACK;

  private static final int MITTIG = 0;
  private static final int RECHTSBÜNDIG = 1;
  private static final int LINKSBÜNDIG = 2;

  private ArrayList<Graph> graphen = new ArrayList<Graph>();


  /**
   * ||||||||||||||||||||||||||||||
   * ||||||||||||||||||||||||||||||
   *
   * @param _nachrichtenManager
   */
  public GrafikBuilder(NachrichtenManager _nachrichtenManager) {
    nachrichtenManager = _nachrichtenManager;
  }

  /**
   * fügt eine Grafik dem Bild hinzu
   *
   * @param _graph
   */
  public void addGraph(Graph _graph) {
    graphen.add(_graph);
  }

  /**
   * generiert eine Grafik in png-Format
   * Diagramm, Statistik etc.
   *
   * @param _file
   */
  public String makeGrafik(File _file) {
    int maxBreite = 0;
    String outString = null;

    // ermittelt die Höhe der Grafik mit der Summierung der Höhern der Graphen
    höhe = paddingTop;
    for (Graph g : graphen) {
      if (g.getWhite() > maxBreite) {
        maxBreite = g.getWhite();
      }
      höhe += g.getHight();
    }

    if (isStatistik) {
      linksAusrichtung = statistikBreite;
      breite = linksAusrichtung + maxBreite;

      // wenn die Höhe der Graphen kleiner ist als die Höhe des Textblockes, wird die Höhe des TextBlockes gewählt
      if (höhe < (nachrichtenManager.getTextBlock().length * 14 + 30)) {
        höhe = nachrichtenManager.getTextBlock().length * 14 + 30;
      }
    } else {
      linksAusrichtung = 0;
      breite = maxBreite;
    }

    // wenn die Statistik breiter ist als die berechnete Breite
    int statistikBreite = 10 + getStatistikMaxBreite() + 10;
    if (statistikBreite > breite) {
      breite = statistikBreite;
    }

    grafik = new BufferedImage(breite, höhe, BufferedImage.TYPE_3BYTE_BGR);

    // alles weiß machen
    for (int x = 0; x < breite; x++) {
      for (int y = 0; y < höhe; y++) {
        grafik.setRGB(x, y, Color.WHITE.getRGB());
      }
    }

    Graphics graphics = grafik.getGraphics();
    int step = paddingTop;
    for (Graph g : graphen) {
      graphics.drawImage(g.getBufferedImage(), linksAusrichtung, step, null);
      step += g.getHight();
    }

    // Statistik
    if (isStatistik) {
      schreibeStatistikBlock(10, 15);
    }

    // schreibe Graph in eine png-Datei
    try {
      ImageIO.write(grafik, "png", _file);
      outString = "Grafik: " + _file.getAbsolutePath();
      System.out.println("Grafik: " + _file.getAbsolutePath());
    } catch (IOException e) {
      e.printStackTrace();
      outString = "Grafik: fehlgeschlagen";
    }

    return outString;
  }

  private void schreibeStatistikBlock(int _x, int _y) {
    int zeilenAbstand = 14;
    String textBlock[] = nachrichtenManager.getTextBlock();
    for (int i = 0; i < textBlock.length; i++) {
      schreibeInGrafik(textBlock[i], LINKSBÜNDIG, _x, _y + zeilenAbstand * i);
    }
  }

  /**
   * schreibt in die Grafik
   *
   * @param _text
   * @param _ausrichtung
   * @param _x
   * @param _y
   */
  private void schreibeInGrafik(String _text, int _ausrichtung, int _x, int _y) {
    Graphics2D g2d = grafik.createGraphics();
    g2d.setPaint(statistikSchriftFarbe);
    g2d.setFont(font);
    FontMetrics fm = g2d.getFontMetrics();
    if (_ausrichtung == 0) {
      g2d.drawString(_text, _x - fm.stringWidth(_text) / 2, _y);
    } else if (_ausrichtung == 1) {
      g2d.drawString(_text, _x - fm.stringWidth(_text), _y);
    } else if (_ausrichtung == 2) {
      g2d.drawString(_text, _x, _y);
    }
    g2d.dispose();
  }

  /**
   * gibt die maximale Breite des Textes der Statistik zurück
   * @return
   */
  private int getStatistikMaxBreite() {
    String statistik[] = nachrichtenManager.getTextBlock();
    Graphics2D g2d = (new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR)).createGraphics();
    g2d.setPaint(statistikSchriftFarbe);
    g2d.setFont(font);
    FontMetrics fm = g2d.getFontMetrics();
    int maxStatistikBreite = 0, zeilenbreite;
    for (int i = 0; i < statistik.length; i++) {
     zeilenbreite = fm.stringWidth(statistik[i]);
     if (zeilenbreite > maxStatistikBreite) {
       maxStatistikBreite = zeilenbreite;
     }
    }
    return maxStatistikBreite;
  }

  public void setStatistik(boolean _statistik) {
    isStatistik = _statistik;
    if (isStatistik) {
      linksAusrichtung = statistikBreite;
    } else {
      linksAusrichtung = 0;
    }
  }
}
