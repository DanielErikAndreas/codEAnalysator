package generell;

import fehler.Fehler;
import fehler.FehlerStatistik;

import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

public class NachrichtenManager {
  private ArrayList<Pause> pausenSumme = new ArrayList<Pause>();
  private NachrichtenParser nachrichtenParser;
  private File file;
  private int sollPaketAnzahl;
  private int sollPause;

  private int kommarStellen = 3;
  private int zahlLänge1 = 25;
  private int zahlLänge2 = 10;
  private int zahlLänge3 = 1 + 3 + 1 + kommarStellen + 1;
  private String format = "%." + kommarStellen + "f";

  protected static final int RECHTSBÜNDIG = 1;
  protected static final int LINKSBÜNDIG = 2;

  private String sollNachricht;

  public NachrichtenManager(File _file) {
    file = _file;
    nachrichtenParser = new NachrichtenParser(file);
  }

  /**
   * liest aus einer Datei und generiert die Liste der Pausen
   */
  public void generate(String _sollNachricht) {
    sollNachricht = _sollNachricht;
    int tmpIntPause;
    Pause tmpPausePause;

    if (sollPause > 0)
      for (Massage massage : nachrichtenParser.getMassages()) {

        // Paket der Pausen hinzufügen
        tmpIntPause = Integer.parseInt(massage.getPause());
        tmpPausePause = inPause(tmpIntPause);
        if (tmpPausePause != null)
          tmpPausePause.addAnzahl();
        else
          pausenSumme.add(new Pause(tmpIntPause));
      }

    nachrichtenParser.checkCode(sollNachricht);

  }

  /**
   * gibt das passende Pausenobjekt zurück
   *
   * @param _pause
   * @return
   */
  private Pause inPause(int _pause) {
    for (Pause pause : pausenSumme) {
      if (pause.getPause() == _pause)
        return pause;
    }
    return null;
  }

  /**
   * erstellt die Statistik und schreibt sie in eine txt-Datei
   *
   * @param _file
   */
  public void makeStatista(File _file) {
    Collections.sort(pausenSumme);
    String[] textBlock = getTextBlock();
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < textBlock.length; i++) {
      sb.append(textBlock[i]).append(System.lineSeparator());
    }

    sb.append(System.lineSeparator()).append(System.lineSeparator()).append(System.lineSeparator());
    int nummerStringLänge = (nachrichtenParser.getMassages().size()+"").length();
    for (int i = 0; i < nachrichtenParser.getMassages().size(); i++) {
      sb.append(passString(RECHTSBÜNDIG, i+"", nummerStringLänge));
      sb.append(": ");
      sb.append(nachrichtenParser.getMassages().get(i).toString()).append(System.lineSeparator());
    }

    //System.out.println(sb.toString());

    FileWriter fileWriter = null;
    BufferedWriter bufferedWriter = null;
    try {
      fileWriter = new FileWriter(_file);
      bufferedWriter = new BufferedWriter(fileWriter);
      bufferedWriter.write(sb.toString());
      System.out.println("geschrieben in: " + _file.getAbsolutePath());
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        bufferedWriter.close();
        fileWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  public String[] getTextBlock() {
    FehlerStatistik fs = nachrichtenParser.getFehlerStatistik();
    int verluste = sollPaketAnzahl - nachrichtenParser.getEmpfangeneNachrichten();
    String[] retVal = {
            getFile().getName(),
            "Maske: " + sollNachricht,
            "",
            "",
            bezeichnung("Anzahl der Pakete:") + printWert(sollPaketAnzahl) + passString(RECHTSBÜNDIG, "100,000%", zahlLänge3),
            bezeichnung(".. erfolgreich:") + printWert(nachrichtenParser.getEmpfangeneNachrichten()) + anteil(nachrichtenParser.getEmpfangeneNachrichten()),
            bezeichnung(".. Verluste:") + printWert(verluste) + anteil(verluste),
            "",
            "Sequenzen:",
            ".. Raum: " + (nachrichtenParser.getSequenzStart() == 0 && nachrichtenParser.getSequenzEnd() == 0 ?
                    "keiner" : nachrichtenParser.getSequenzStart() + " - " + nachrichtenParser.getSequenzEnd()),
            bezeichnung(".. fehlende") + printWert(nachrichtenParser.getFehlendeSequenzen()) + anteil(nachrichtenParser.getFehlendeSequenzen()),
            bezeichnung(".. Duplikate") + printWert(fs.get(Fehler.Typ.SEQUENZ_DUPLIKAT)) + anteil(fs.get(Fehler.Typ.SEQUENZ_DUPLIKAT)),
            bezeichnung(".. nicht im Raum") + printWert(fs.get(Fehler.Typ.NICHT_IM_SEQUENZRAUM)) + anteil(fs.get(Fehler.Typ.NICHT_IM_SEQUENZRAUM)),
            "",
            "Pausen:",
            bezeichnung(".. unterschiedliche:") + printWert(pausenSumme.size()),
            bezeichnung(".. soll:") + printWert(sollPause),
            bezeichnung(".. max:") + printWert(nachrichtenParser.getMaxPause()),
            bezeichnung(".. min:") + printWert(nachrichtenParser.getMinPause()),
            bezeichnung(".. Ø:") + durchschnitt(nachrichtenParser.getPausenSumme(), nachrichtenParser.getEmpfangeneNachrichten()),
            bezeichnung(".. Spanne:") + printWert(nachrichtenParser.getMaxPause() - nachrichtenParser.getMinPause()),
            "",
            bezeichnung("Übereinstimmungen:") + printWert(fs.get(Fehler.Typ.KEIN_FEHLER, Fehler.Typ.ENTHÄLT_VOLLE_NACHRICHT)) + anteil(fs.get(Fehler.Typ.KEIN_FEHLER, Fehler.Typ.ENTHÄLT_VOLLE_NACHRICHT)),
            bezeichnung("kein Fehler:") + printWert(fs.get(Fehler.Typ.KEIN_FEHLER)) + anteil(fs.get(Fehler.Typ.KEIN_FEHLER)),
            bezeichnung("enthält komp. Nachricht: ") + printWert(fs.get(Fehler.Typ.ENTHÄLT_VOLLE_NACHRICHT)) + anteil(fs.get(Fehler.Typ.ENTHÄLT_VOLLE_NACHRICHT)),
            "",
            "Fehler:",
            bezeichnung("Bit fehlt:") + printWert(fs.get(Fehler.Typ.ZWISCHEN_BIT_FEHLT)) + anteil(fs.get(Fehler.Typ.ZWISCHEN_BIT_FEHLT)),
            bezeichnung("Bit zu viel:") + printWert(fs.get(Fehler.Typ.ZWISCHEN_BIT_ZU_VIEL)) + anteil(fs.get(Fehler.Typ.ZWISCHEN_BIT_ZU_VIEL)),
            bezeichnung("Bit gewechselt:") + printWert(fs.get(Fehler.Typ.ZWISCHEN_BIT_WECHSEL)) + anteil(fs.get(Fehler.Typ.ZWISCHEN_BIT_WECHSEL)),
            "",
            bezeichnung("EinBitFehler:") + printWert(fs.get(Fehler.Typ.EINBIT_FEHLER)) + anteil(fs.get(Fehler.Typ.EINBIT_FEHLER)),
            bezeichnung("ZweiBitFehler:") + printWert(fs.get(Fehler.Typ.ZWEIBIT_FEHLER)) + anteil(fs.get(Fehler.Typ.ZWEIBIT_FEHLER)),
            bezeichnung("DreiBitFehler:") + printWert(fs.get(Fehler.Typ.DREIBIT_FEHLER)) + anteil(fs.get(Fehler.Typ.DREIBIT_FEHLER)),
            "",
            bezeichnung("Bit 1 fehlt:") + printWert(fs.get(Fehler.Typ.ZWISCHEN_BIT_1_FEHLT)) + anteil(fs.get(Fehler.Typ.ZWISCHEN_BIT_1_FEHLT)),
            bezeichnung("Bit 1 gekipt:") + printWert(fs.get(Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_1)) + anteil(fs.get(Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_1)),
            bezeichnung("Bit 1 zu viel:") + printWert(fs.get(Fehler.Typ.ZWISCHEN_BIT_1_ZU_VIEL)) + anteil(fs.get(Fehler.Typ.ZWISCHEN_BIT_1_ZU_VIEL)),
            "",
            bezeichnung("Bit 0 fehlt:") + printWert(fs.get(Fehler.Typ.ZWISCHEN_BIT_0_FEHLT)) + anteil(fs.get(Fehler.Typ.ZWISCHEN_BIT_0_FEHLT)),
            bezeichnung("Bit 0 gekipt:") + printWert(fs.get(Fehler.Typ.ZWISCHEN_BIT_0_ZU_VIEL)) + anteil(fs.get(Fehler.Typ.ZWISCHEN_BIT_0_ZU_VIEL)),
            bezeichnung("Bit 0 zu viel:") + printWert(fs.get(Fehler.Typ.ZWISCHEN_BIT_0_ZU_VIEL)) + anteil(fs.get(Fehler.Typ.ZWISCHEN_BIT_0_ZU_VIEL)),
            "",
            bezeichnung("shift:") + printWert(fs.get(Fehler.Typ.SHIFT)) + anteil(fs.get(Fehler.Typ.SHIFT)),
            bezeichnung(".. max:") + printWert(nachrichtenParser.getMaxShift()),
            bezeichnung(".. Ø:") + durchschnitt(nachrichtenParser.getShiftSumme(), fs.get(Fehler.Typ.SHIFT)),
            "",
            bezeichnung("Nachbits:") + printWert(fs.get(Fehler.Typ.NACHBITS)) + anteil(fs.get(Fehler.Typ.NACHBITS)),
            bezeichnung(".. max:") + printWert(nachrichtenParser.getMaxNachbit()),
            bezeichnung(".. Ø:") + durchschnitt(nachrichtenParser.getNachbitsSumme(), fs.get(Fehler.Typ.NACHBITS))
    };
    return retVal;
  }

  private String anteil(int _wert) {
    double wert = (double) _wert * 100.0 / (double) sollPaketAnzahl;
    if (wert == 0.0) {
      return "";
    }
    return passString(RECHTSBÜNDIG,String.format(format, wert) + "%", zahlLänge3);
  }

  private String printWert(int _wert) {
    return passString(RECHTSBÜNDIG, NumberFormat.getInstance().format(_wert), zahlLänge2);
  }
  private String printWert(double _wert) {
    return passString(RECHTSBÜNDIG ,NumberFormat.getInstance().format(_wert), zahlLänge2);
  }

  private String bezeichnung(String _s) {
    return passString(LINKSBÜNDIG,_s, zahlLänge1);
  }

  private String durchschnitt(long _summe, int _anzahl) {
    double durchschnitt;
    if (_anzahl == 0.0 || _summe == 0.0) {
      durchschnitt = 0;
    } else {
      durchschnitt = (double) _summe / (double) _anzahl;
    }
    String s = NumberFormat.getInstance().format(durchschnitt);
    String splitString[] = s.split(",");
    int addSpace =  zahlLänge2 - splitString[0].length();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < addSpace; i++) {
      sb.append(" ");
    }
    sb.append(s);
    return sb.toString();
  }

  /**
   * füllt einen String vorne auf die angegebene Länge mit Spaces aus
   *
   * @param _s
   * @param _länge
   * @return
   */
  private String passString(int _ausrichtung, String _s, int _länge) {
    if (_s.length() >= _länge) {
      return _s;
    }

    int zusatzSpace = _länge - _s.length();
    StringBuffer sb = new StringBuffer();
    if(_ausrichtung == LINKSBÜNDIG) {
      sb.append(_s);
    }
    for (int i = 0; i < zusatzSpace; i++) {
      sb.append(" ");
    }
    if(_ausrichtung == RECHTSBÜNDIG) {
      sb.append(_s);
    }

    return sb.toString();
  }

  /**
   * setzt Start und End des Sequentraumes
   *
   * @param _sequenzRaum
   */
  public boolean setSequenz(String _sequenzRaum) {
    return nachrichtenParser.setSequenzRaum(_sequenzRaum);
  }

  public void setSollPaketAnzahl(int sollPaketAnzahl) {
    this.sollPaketAnzahl = sollPaketAnzahl;
  }

  public ArrayList<Pause> getPausenSumme() {
    return pausenSumme;
  }

  public String getSollNachricht() {
    return sollNachricht;
  }

  public void setSollPause(int sollPause) {
    this.sollPause = sollPause;
  }

  public File getFile() {
    return file;
  }

  public NachrichtenParser getNachrichtenParser() {
    return nachrichtenParser;
  }

}
