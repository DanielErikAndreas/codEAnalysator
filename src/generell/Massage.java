package generell;

import fehler.Fehler;
import fehler.FehlerListe;

import java.util.ArrayList;

public class Massage {
  private String bits;
  private String decoding_index;
  private String message_type_id;
  private String modulator_index;
  private String pause;
  private String tmpBits;

  private FehlerListe fehlerList = new FehlerListe();
  private static char manipulativBitZuViel;
  private static char manipulativBitZuWenig;
  private static char manipulativBitWechsel;

  private int sequenzNummer;
  private int shift;

  private static String sollNachricht;
  private static final double abweichungsVerhältnis = 0.2;
  private static ArrayList<Integer> startDynFeld;
  private static ArrayList<Integer> endDynFeld;

  private static final boolean checkKomments = true;


  public Massage(String _bits) {
    setMassage(_bits);
    pause = "0";
  }

  public Massage(String _bits, String _decoding_index, String _message_type_id, String _modulator_index, String _pause) {
    setMassage(_bits);
    //bits = _bits;
    decoding_index = _decoding_index;
    message_type_id = _message_type_id;
    modulator_index = _modulator_index;
    pause = _pause == null ? "0" : _pause;
  }

  /**
   * >>>>>>>>>>>>>>>>>>>>>>>
   * >>>>>>>>>>>>>>>>>>>>>>>
   * untersucht den Code und detektiert FehlerTyp
   */
  public Massage checkCode() {
    tmpBits = bits;
    int fehler = bitFehler(tmpBits);

    int ersterFehler, fehlerSub, fehlerAdd, fehlerWec, bestPass, zwischenBitFehler = 0;
    int kritischFehlerCount = (int) (sollNachricht.length() * abweichungsVerhältnis);
    while (fehler > 0) {
      if (zwischenBitFehler > kritischFehlerCount) {
        fehlerList.getFehlerList().clear();
        addFehler(Fehler.Typ.KEINE_ÜBEREINSTIMMUNG, 0);
        return this;
      }


      ersterFehler = ersterFehlerBei();
      String lösungSub = entfernen(ersterFehler);
      String lösungAdd = einfügen(ersterFehler);
      String lösungWec = wechsel(ersterFehler);
      fehlerSub = bitFehler(lösungSub);
      fehlerAdd = bitFehler(lösungAdd);
      fehlerWec = bitFehler(lösungWec);

      bestPass = bestPassIndex(tmpBits);

      // den SHIFT als Fehler hinzufügen, sofer er größer als 0 ist
      if (bestPass > 0 && !fehlerList.hatShift()) {
        shift = bestPass;
        addFehler(Fehler.Typ.SHIFT, bestPass);
      }

      if (ersterFehler < sollNachricht.length()) {
        if (fehlerSub <= fehlerAdd && fehlerSub <= fehlerWec || bestPass > 0) {
          tmpBits = lösungSub;
          if (manipulativBitZuViel == '0') {
            if (ersterFehler > 0) {
              addFehler(Fehler.Typ.ZWISCHEN_BIT_0_ZU_VIEL, ersterFehler);
              zwischenBitFehler++;
            } else {
              addFehler(Fehler.Typ.VOR_BIT_0_ZU_VIEL, ersterFehler);
            }
          } else if (manipulativBitZuViel == '1') {
            if (ersterFehler > 0) {
              addFehler(Fehler.Typ.ZWISCHEN_BIT_1_ZU_VIEL, ersterFehler);
              zwischenBitFehler++;
            } else {
              addFehler(Fehler.Typ.VOR_BIT_1_ZU_VIEL, ersterFehler);
            }
          }
        } else if (fehlerAdd <= fehlerSub && fehlerAdd <= fehlerWec) {
          tmpBits = lösungAdd;
          if (manipulativBitZuWenig == '0') {
            addFehler(Fehler.Typ.ZWISCHEN_BIT_0_FEHLT, ersterFehler);
            zwischenBitFehler++;
          } else if (manipulativBitZuWenig == '1') {
            addFehler(Fehler.Typ.ZWISCHEN_BIT_1_FEHLT, ersterFehler);
            zwischenBitFehler++;
          }
        } else if (fehlerWec <= fehlerSub && fehlerWec <= fehlerAdd) {
          tmpBits = lösungWec;
          if (manipulativBitWechsel == '0') {
            addFehler(Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_0, ersterFehler);
            zwischenBitFehler++;
          } else if (manipulativBitWechsel == '1') {
            addFehler(Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_1, ersterFehler);
            zwischenBitFehler++;
          }
        }
      } else {
        tmpBits = lösungSub;
        if (manipulativBitZuViel == '0') {
          addFehler(Fehler.Typ.NACH_BIT_0_ZU_VIEL, ersterFehler);
        } else if (manipulativBitZuViel == '1') {
          addFehler(Fehler.Typ.NACH_BIT_1_ZU_VIEL, ersterFehler);
        }
      }

      fehler = bitFehler(tmpBits);
    }

    // dynamische Felder in einen String packen
    StringBuilder sequenz = new StringBuilder();
    for (int i = 0; i < startDynFeld.size(); i++) {
      if (bits.length() > endDynFeld.get(i))
        sequenz.append(tmpBits.substring(startDynFeld.get(i), endDynFeld.get(i)));
    }

    String sequenzString = sequenz.toString();
    if (!sequenzString.equals("")) {
      sequenzNummer = Integer.parseInt(sequenz.toString(), 2);
    } else {
      sequenzNummer = 0;
    }

    return this;
  }

  private int ersterFehlerBei() {
    int länge;
    int retVal = -1;
    if (tmpBits.length() >= sollNachricht.length()) {
      länge = tmpBits.length();
      retVal = länge;
    } else {
      länge = sollNachricht.length();
    }

    for (int i = 0; i < länge; i++) {
      if (i >= tmpBits.length() || i >= sollNachricht.length())
        return i;
      if (xor(tmpBits.charAt(i), sollNachricht.charAt(i)))
        return i;
    }
    return retVal;
  }

  private String wechsel(int _index) {
    char[] bits = tmpBits.toCharArray();
    if (_index < bits.length) {
      char[] autBits = new char[bits.length];
      for (int i = 0; i < bits.length; i++) {
        if (i == _index) {
          if (bits[i] == '0')
            autBits[i] = '1';
          else
            autBits[i] = '0';
          manipulativBitWechsel = autBits[i];
        } else {
          autBits[i] = bits[i];
        }
      }
      return new String(autBits);
    } else {
      return null;
    }
  }

  private String entfernen(int _index) {
    char[] bits = tmpBits.toCharArray();
    if (_index < bits.length) {
      char[] autBits = new char[bits.length - 1];
      int autBitsCount = 0;
      int bitsCount = 0;
      while (autBitsCount < autBits.length) {
        if (bitsCount == _index) {
          bitsCount++;
        } else {
          autBits[autBitsCount++] = bits[bitsCount++];
        }
      }
      manipulativBitZuViel = bits[_index];
      return new String(autBits);
    } else {
      return null;
    }
  }

  private String einfügen(int _index) {
    char[] bits = tmpBits.toCharArray();
    if (_index <= bits.length) {
      char[] autBits = new char[bits.length + 1];
      int autBitsCount = 0;
      int bitsCount = 0;
      while (autBitsCount < autBits.length) {
        if (autBitsCount == _index) {
          if (bitsCount < sollNachricht.length())
            manipulativBitZuWenig = sollNachricht.toCharArray()[bitsCount];
          else
            manipulativBitZuWenig = '0';
          autBits[autBitsCount++] = manipulativBitZuWenig;
        } else {
          autBits[autBitsCount++] = bits[bitsCount++];
        }
      }
      return new String(autBits);
    } else {
      return null;
    }
  }

  private int bitFehler(String _bits) {
    return bitFehler(sollNachricht, _bits);
  }

  private int bitFehler(String _sollNachricht, String _bits) {
    if (_bits != null) {
      int bitFehler;
      int länge;

      if (_bits.length() > _sollNachricht.length()) {
        bitFehler = _bits.length() - _sollNachricht.length();
        länge = _sollNachricht.length();
      } else if (_bits.length() < _sollNachricht.length()) {
        bitFehler = _sollNachricht.length() - _bits.length();
        länge = _bits.length();
      } else {
        bitFehler = 0;
        länge = _sollNachricht.length();
      }

      for (int i = 0; i < länge; i++) {
        if (xor(_bits.charAt(i), _sollNachricht.charAt(i)))
          bitFehler++;
      }
      return bitFehler;
    } else {
      return 0x7fffffff;
    }
  }

  private static boolean xor(char _c1, char _c2) {
    if ((_c1 == '0' && _c2 == '1') || (_c1 == '1' && _c2 == '0'))
      return true;
    else
      return false;
  }

  /**
   * sucht den AnfangsIndex eines nibble
   *
   * @param _start
   * @param _nibble
   * @return
   */
  public int erstÜbereinstimmungIndex(int _start, String _nibble) {
    int retVal = -1;
    int lableLänge = _nibble.length();
    if (bits.length() >= _start + lableLänge) {
      int diff = bits.length() - lableLänge;
      for (int i = _start; i <= diff; i++) {
        if (bits.substring(i, i + lableLänge).equals(_nibble)) {
          retVal = i;
          break;
        }
      }
    }
    return retVal;
  }


  /**
   * prüft, ob ein String in einem anderen enthalten ist
   *
   * @param _s1
   * @param _s2
   * @return
   */
  public static boolean isTeilmenge(String _s1, String _s2) {
    boolean retVal = false;
    String hauptString = längsterString(_s1, _s2);
    String unterString = kürzesterString(_s1, _s2);

    int distanz = hauptString.length() - unterString.length();
    for (int i = 0; i <= distanz; i++) {
      if (hauptString.substring(i, hauptString.length()).equals(unterString)) {
        retVal = true;
        break;
      }
    }
    return retVal;
  }

  /**
   * gibt den Index zurück, an der Stelle wo die Sollnachricht am besten in die Massage passt
   *
   * @param _massage
   * @return
   */
  public int bestPassIndex(String _massage) {
    int bestIndex = 0;
    if (_massage.length() > sollNachricht.length()) {
      int dist = _massage.length() - sollNachricht.length();
      int fehler = 0x7fffffff;
      int tmpFehler;
      String tmpSollNachricht = sollNachricht;
      for (int i = 0; i <= dist; i++) {
        tmpFehler = bitFehler(tmpSollNachricht, _massage);
        if (tmpFehler < fehler) {
          fehler = tmpFehler;
          bestIndex = i;
        }
        tmpSollNachricht = "X" + tmpSollNachricht;
      }
    }
    return bestIndex;
  }

  public static String längsterString(String _s1, String _s2) {
    if (_s1.length() > _s2.length())
      return _s1;
    else if (_s1.length() < _s2.length())
      return _s2;
    else
      return _s1;
  }

  public static String kürzesterString(String _s1, String _s2) {
    if (_s1.length() > _s2.length())
      return _s2;
    else if (_s1.length() < _s2.length())
      return _s1;
    else
      return _s2;
  }

  public String getPause() {
    return pause;
  }

  public String toString() {
    return "[sq(" + sequenzNummer + "), " + fehlerList + ", " + bits + "]";
  }

  public static void setSollNachricht(String _sollNachricht) {
    sollNachricht = _sollNachricht;
  }

  public int countFehler(Fehler.Typ... _fehlerTyp) {
    return fehlerList.countFehler(_fehlerTyp);
  }

  public static void setStartEndDynFeld(ArrayList<Integer> _startDynFeld, ArrayList<Integer> _endDynFeld) {
    startDynFeld = _startDynFeld;
    endDynFeld = _endDynFeld;
  }

  public FehlerListe getFehlerList() {
    return fehlerList;
  }

  public void setMassage(String _massage) {
    if (checkKomments) {
      int bitsEnd = 0;
      for (int i = 0; i < _massage.length(); i++) {
        if (_massage.charAt(i) == '#') {
          bitsEnd = i;
          break;
        }
      }
      if (bitsEnd > 0) {
        bits = _massage.substring(0, bitsEnd).trim();
      } else {
        bits = _massage;
      }
    } else {
      bits = _massage;
    }
  }

  public int getSequenzNummer() {
    return sequenzNummer;
  }

  public void setSequenzNummer(int _sequenzNummer) {
    sequenzNummer = _sequenzNummer;
  }

  public void addFehler(Fehler.Typ _fehlerTyp, int _index) {
    fehlerList.addFehler(_fehlerTyp, _index);
  }

  public int getShift() {
    return shift;
  }
}
