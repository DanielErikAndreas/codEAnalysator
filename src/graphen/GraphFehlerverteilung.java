package graphen;

import generell.Massage;
import generell.NachrichtenManager;
import fehler.Fehler;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GraphFehlerverteilung extends Graph {
  private int sektoren[][];
  private String[] sektorenBezeichnung = {"shift", "fehlt", "wechsel", "zu viel", "nach", "Nachricht"};
  private String[] farbBedeutung = {"kein Fehler", "Nachricht fehlt", "EinBitFehler", "ZweiBitFehler", "DreiBitFehler"};
  private int[] farbArray = {KEINFEHLER, NACHRICHT_FEHLT, EINBITFEHLER, ZWEIBITFEHLER, DREIBITFEHLER};

  private static final int KEINFEHLER = 0x0080ff; // blau
  private static final int SHIFT = 0xff8000; // orange
  private static final int NACHRICHT_FEHLT = 0xff0000; // rot
  private static final int EINBITFEHLER = 0x00994c; // dunkelgrün
  private static final int ZWEIBITFEHLER = 0xff8000; // orange
  private static final int DREIBITFEHLER = 0xff007f; // magenta
  private static final int WEIß = 0xffffff; // weiß


  public GraphFehlerverteilung(NachrichtenManager _nachrichtenManager) {
    super(_nachrichtenManager);
    ArrayList<Massage> massages = nachrichtenManager.getNachrichtenParser().getMassages();

    sektoren = new int[massages.size()][sektorenBezeichnung.length];
    ausdehnungX = sektoren.length;

    boolean fehlerInMasage;
    boolean fehlerVorNachMasage;
    int fehler;
    for (int i = 0; i < sektoren.length; i++) {
      fehlerInMasage = false;
      fehler = massages.get(i).countFehler(Fehler.Typ.NICHT_EXISTENT);
      if (fehler == 0) {
        fehler = massages.get(i).countFehler(Fehler.Typ.VOR_BIT_0_ZU_VIEL, Fehler.Typ.VOR_BIT_1_ZU_VIEL);
        if (fehler == 0) {
          sektoren[i][0] = WEIß;
        } else {
          if (fehler == 1) {
            sektoren[i][0] = EINBITFEHLER;
          } else if (fehler == 2) {
            sektoren[i][0] = ZWEIBITFEHLER;
          } else if (fehler >= 3) {
            sektoren[i][0] = DREIBITFEHLER;
          }
        }

        fehler = massages.get(i).countFehler(Fehler.Typ.ZWISCHEN_BIT_0_FEHLT, Fehler.Typ.ZWISCHEN_BIT_1_FEHLT);
        if (fehler == 0) {
          sektoren[i][1] = WEIß;
        } else {
          fehlerInMasage = true;
          if (fehler == 1) {
            sektoren[i][1] = EINBITFEHLER;
          } else if (fehler == 2) {
            sektoren[i][1] = ZWEIBITFEHLER;
          } else if (fehler >= 3) {
            sektoren[i][1] = DREIBITFEHLER;
          }
        }

        fehler = massages.get(i).countFehler(Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_0, Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_1);
        if (fehler == 0) {
          sektoren[i][2] = WEIß;
        } else {
          fehlerInMasage = true;
          if (fehler == 1) {
            sektoren[i][2] = EINBITFEHLER;
          } else if (fehler == 2) {
            sektoren[i][2] = ZWEIBITFEHLER;
          } else if (fehler >= 3) {
            sektoren[i][2] = DREIBITFEHLER;
          }
        }

        fehler = massages.get(i).countFehler(Fehler.Typ.ZWISCHEN_BIT_0_ZU_VIEL, Fehler.Typ.ZWISCHEN_BIT_1_ZU_VIEL);
        if (fehler == 0) {
          sektoren[i][3] = WEIß;
        } else {
          fehlerInMasage = true;
          if (fehler == 1) {
            sektoren[i][3] = EINBITFEHLER;
          } else if (fehler == 2) {
            sektoren[i][3] = ZWEIBITFEHLER;
          } else if (fehler >= 3) {
            sektoren[i][3] = DREIBITFEHLER;
          }
        }

        fehler = massages.get(i).countFehler(Fehler.Typ.NACH_BIT_0_ZU_VIEL, Fehler.Typ.NACH_BIT_1_ZU_VIEL);
        if (fehler == 0) {
          sektoren[i][4] = WEIß;
        } else {
          if (fehler == 1) {
            sektoren[i][4] = EINBITFEHLER;
          } else if (fehler == 2) {
            sektoren[i][4] = ZWEIBITFEHLER;
          } else if (fehler >= 3) {
            sektoren[i][4] = DREIBITFEHLER;
          }
        }

        fehler = massages.get(i).countFehler(Fehler.Typ.KEINE_ÜBEREINSTIMMUNG);
        if (!fehlerInMasage && fehler == 0) {
          sektoren[i][5] = KEINFEHLER;
        } else {
          sektoren[i][5] = WEIß;
        }
      } else {
        sektoren[i][0] = WEIß;
        sektoren[i][1] = WEIß;
        sektoren[i][2] = WEIß;
        sektoren[i][3] = WEIß;
        sektoren[i][4] = WEIß;
        sektoren[i][5] = NACHRICHT_FEHLT;
      }

    }

    werteNameX = "Pakete";
    werteNameY = "";

    breite = paddingLeft + ausdehnungX + paddingRight;
    // fals die breite einen gewissen Wert unterschreitet, wird sie auf ein Minimum gesetzt, da sonst die Legende nicht passt
    if (breite < paddingLeft + 670 + paddingRight)
      breite = paddingLeft + 670 + paddingRight;
    höhe = paddingTop + ausdehnungY + paddingButom;
  }

  @Override
  public BufferedImage getBufferedImage() {
    super.makeBufferedImage();

    int stepY = (ausdehnungY / sektoren[0].length) + 1;

    // den Graphen (Grafik) zeichnen
    int schwelle = paddingTop + stepY;
    int countX = 0;
    int countY = 0;
    for (int x = paddingLeft; x < paddingLeft + ausdehnungX; x++) {
      for (int y = paddingTop; y < paddingTop + ausdehnungY; y++) {
        grafik.setRGB(x, y, sektoren[countX][countY]);
        if (y >= schwelle) {
          schwelle += stepY;
          countY++;
        }
      }
      countX++;
      schwelle = paddingTop + stepY;
      countY = 0;
    }

    int werteAbstand = 100;
    int tmpWert = 0;

    // Werte der Skale X (Graf)
    graphWertZeichnen(X, tmpWert, paddingLeft - 1, paddingTop + ausdehnungY);
    tmpWert += werteAbstand;
    for (int x = paddingLeft + werteAbstand; x < paddingLeft + ausdehnungX; x += werteAbstand) {
      graphWertZeichnen(X, tmpWert, x, paddingTop + ausdehnungY);
      tmpWert += werteAbstand;
    }

    // Werte der Skale Y
    int stepYcount = 0;
    for (int y = paddingTop + stepY / 2; y < paddingTop + ausdehnungY; y += stepY) {
      graphWertZeichnen(Y, sektorenBezeichnung[stepYcount], paddingLeft - 1, y);
      stepYcount++;
    }

    // Beschreibung
    int posX = paddingLeft;
    int step;
    for (int i = 0; i < farbBedeutung.length; i++) {
      step = schreibeInGrafik(farbBedeutung[i], LINKSBÜNDIG, Font.PLAIN, 11, posX, paddingTop - 5, farbArray[i]);
      posX += step + 20;
    }

    return grafik;
  }
}
