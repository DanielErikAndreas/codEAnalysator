package graphen;

import generell.NachrichtenManager;
import generell.Pause;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.ArrayList;

public class Graph {
  protected int paddingTop = 40;
  protected int paddingButom = 40;
  protected int paddingLeft = 100;
  protected int paddingRight = 20;
  protected int ausdehnungX;
  protected int ausdehnungY = 250;
  protected int höhe, breite;
  protected static int akzeptierteSapelSchritte[] = {1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000, 100000, 200000, 500000, 1000000, 2000000, 5000000};
  protected int maxYwert;
  protected String werteNameX;
  protected String werteNameY;
  protected double tolleranz = 1.05;

  protected NachrichtenManager nachrichtenManager;
  protected BufferedImage grafik;

  protected static final int MITTIG = 0;
  protected static final int RECHTSBÜNDIG = 1;
  protected static final int LINKSBÜNDIG = 2;

  protected static final int X = 0;
  protected static final int Y = 1;

  /**||||||||||||||||||||||||||||
   * ||||||||||||||||||||||||||||
   * @param _nachrichtenManager
   */
  public Graph(NachrichtenManager _nachrichtenManager) {
    nachrichtenManager = _nachrichtenManager;
  }

  // wird überschrieben
  public BufferedImage getBufferedImage(){return null;}

  /**
   * baut den Graphen
   */
  protected void makeBufferedImage() {
    grafik = new BufferedImage(breite, höhe, BufferedImage.TYPE_3BYTE_BGR);

    // alles weiß machen
    for (int x = 0; x < breite; x++) {
      for (int y = 0; y < höhe; y++) {
        grafik.setRGB(x, y, Color.WHITE.getRGB());
      }
    }

    // Skale Y (Graf)
    block(paddingLeft - 1, paddingTop, paddingLeft - 1, paddingTop + ausdehnungY, Color.BLACK.getRGB());

    // Skale X (Graf)
    block(paddingLeft - 1, paddingTop + ausdehnungY, paddingLeft + ausdehnungX, paddingTop + ausdehnungY, Color.BLACK.getRGB());

    // Skalenbeschreibung
    schreibeInGrafik(werteNameX, MITTIG, Font.BOLD, 11, paddingLeft + ausdehnungX / 2, paddingTop + ausdehnungY + 35);
    schreibeInGrafik(werteNameY, MITTIG, Font.BOLD, 11, paddingLeft, paddingTop - 5);
  }

  /**
   * zeichnet die Werte an den graphen
   *
   * @param _dimension
   * @param _wert
   * @param _x
   * @param _y
   */
  protected void graphWertZeichnen(int _dimension, int _wert, int _x, int _y) {
    graphWertZeichnen(_dimension, NumberFormat.getInstance().format(_wert), _x, _y);
  }
  protected void graphWertZeichnen(int _dimension, String _wert, int _x, int _y) {
    int strichLänge = 5;
    String wert = _wert;

    if (_dimension == MITTIG) {
      for (int y = _y; y < _y + strichLänge; y++) {
        grafik.setRGB(_x, y, Color.BLACK.getRGB());
      }
      schreibeInGrafik(wert, MITTIG, Font.PLAIN, 10, _x, _y + 15);
    } else if (_dimension == RECHTSBÜNDIG) {
      for (int x = _x; x > _x - strichLänge; x--) {
        grafik.setRGB(x, _y, Color.BLACK.getRGB());
      }
      schreibeInGrafik(wert, RECHTSBÜNDIG, Font.PLAIN, 10, _x - 8, _y + 4);
    }
  }

  /**
   * schreibt in die Grafik
   *
   * @param _text
   * @param _ausrichtung
   * @param _schriftform
   * @param _schriftgröße
   * @param _x
   * @param _y
   */
  protected void schreibeInGrafik(String _text, int _ausrichtung, int _schriftform, int _schriftgröße, int _x, int _y) {
    schreibeInGrafik(_text, _ausrichtung, _schriftform, _schriftgröße, _x, _y, 0x000000);
  }
  protected int schreibeInGrafik(String _text, int _ausrichtung, int _schriftform, int _schriftgröße, int _x, int _y, int _farbe) {
    Graphics2D g2d = grafik.createGraphics();
    g2d.setPaint(new Color(_farbe));
    g2d.setFont(new Font("Dialog", _schriftform, _schriftgröße));
    FontMetrics fm = g2d.getFontMetrics();
    int textBreite = fm.stringWidth(_text);
    if (_ausrichtung == 0) {
      g2d.drawString(_text, _x - textBreite / 2, _y);
    } else if (_ausrichtung == 1) {
      g2d.drawString(_text, _x - textBreite, _y);
    } else if (_ausrichtung == 2) {
      g2d.drawString(_text, _x, _y);
    }
    g2d.dispose();
    return textBreite;
  }

  /**
   * passt den Wert in die vorgegebene Höhe des Graphen an
   * @param _anpassung Wert, an dem angepasst werdfen soll
   * @param _orientierung maximal Wert
   * @param _wert Wert, der angepasst werden soll
   * @return
   */
  protected int yAnpassung(int _anpassung, int _orientierung, int _wert) {

    //double wert1 = _wert * ausdehnungGrafY;
    double wert1 = _wert * _anpassung;
    //double wert2 = (double) nachrichtenManager.getMaxPause() * tolleranz;
    double wert2 = (double) _orientierung * tolleranz;
    double wert1d2 = wert1 / wert2;
    int intWert1d2 = (int) wert1d2;

    // besser runden
    if (wert1d2 - intWert1d2 >= 0.5)
      intWert1d2 += 1;

    return intWert1d2;
  }

  /**
   * zeichnet einen Block zwischen den Koordinaten x1, y1, x2, y2
   *
   * @param _x1
   * @param _y1
   * @param _x2
   * @param _y2
   * @param _color
   */
  protected void block(int _x1, int _y1, int _x2, int _y2, int _color) {
    int xVon = (_x1 < _x2) ? _x1 : _x2;
    int xBis = (_x1 > _x2) ? _x1 : _x2;
    int yVon = (_y1 < _y2) ? _y1 : _y2;
    int yBis = (_y1 > _y2) ? _y1 : _y2;

    for (int y = yVon; y <= yBis; y++) {
      for (int x = xVon; x <= xBis; x++) {
        grafik.setRGB(x, y, _color);
      }
    }
  }

  /**
   * sucht den besten Abstandswert
   *
   * @param _akzeptierteSchritte
   * @param _maxWert
   * @return
   */
  protected int berechneSchritte(int _akzeptierteSchritte[], int _maxWert) {
    int retVal = 0;
    int spanne = 0x7fffffff;
    int tmpSpanne;

    for (int i = 0; i < _akzeptierteSchritte.length; i++) {
      tmpSpanne = distanz(_maxWert, _akzeptierteSchritte[i]);
      if (tmpSpanne < spanne) {
        spanne = tmpSpanne;
        retVal = _akzeptierteSchritte[i];
      }
    }

    return retVal;
  }

  /**
   * berechnet die Distanz zwischen zweiu Werten
   *
   * @param _wert1
   * @param _wert2
   * @return
   */
  public static int distanz(int _wert1, int _wert2) {
    int retVal;
    if (_wert1 > _wert2)
      retVal = _wert1 - _wert2;
    else if (_wert1 < _wert2)
      retVal = _wert2 - _wert1;
    else
      retVal = 0;
    return retVal;
  }

  /**
   * gibt die Pause mit der größten Anzahl zurück
   *
   * @param _pausen
   * @return
   */
  protected int häufigstePause(ArrayList<Pause> _pausen) {
    int retVal = 0;
    for (Pause pause : _pausen) {
      if (pause.getAnzahl() > retVal)
        retVal = pause.getAnzahl();
    }
    return retVal;
  }

  /**
   * gibt den Index der Pause mit der größten Anzahl zurück
   *
   * @param _pausen
   * @return
   */
  protected int häufigstePauseIndex(ArrayList<Pause> _pausen) {
    int retVal = 0;
    int maxPausenAnzahl = 0;
    for (int i = 0; i < _pausen.size(); i++) {
      if (_pausen.get(i).getAnzahl() > maxPausenAnzahl) {
        maxPausenAnzahl = _pausen.get(i).getAnzahl();
        retVal = i;
      }
    }
    return retVal;
  }

  public int getHight() {
    return paddingTop + ausdehnungY + paddingButom;
  }

  public int getWhite() {
    return paddingLeft + ausdehnungX + paddingRight;
  }
}
