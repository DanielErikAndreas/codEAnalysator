package fehler;

import java.util.ArrayList;

public class FehlerListe {
  private ArrayList<Fehler> fehlerList = new ArrayList<Fehler>();
  private boolean hatShift = false;

  public FehlerListe() {
  }

  public void addFehler(Fehler.Typ _fehlerTyp, int _stelle) {
    // fügt den SHIFT als Fehler hinzu, sofern er noch nicht vorhanden ist
    if (_fehlerTyp == Fehler.Typ.SHIFT) {
      if (!hatShift) {
        fehlerList.add(new Fehler(Fehler.Typ.SHIFT, _stelle));
        hatShift = true;
      }
    } else {
      fehlerList.add(new Fehler(_fehlerTyp, _stelle));
    }

  }

  public String toString() {
    if (fehlerList.isEmpty())
      return "(" + Fehler.Typ.KEIN_FEHLER + ")";
    /*
    if (fehlerList.get(0).getTyp() == Fehler.Typ.KEINE_ÜBEREINSTIMMUNG)
      return "(" + Fehler.Typ.KEINE_ÜBEREINSTIMMUNG + ")";
      */
    StringBuilder sb = new StringBuilder();
    sb.append("(");
    for (int i = 0; i < fehlerList.size(); i++) {
      if (i < fehlerList.size() - 1)
        sb.append(fehlerList.get(i)).append(", ");
      else
        sb.append(fehlerList.get(i)).append(")");
    }
    return sb.toString();
  }

  /**
   * gibt eine Bestätigung, wenn  der gesuchte FehlerTyp vorhanden ist
   *
   * @param _typ
   * @return
   */
  public int countFehler(Fehler.Typ... _typ) {
    int fehlerCount = 0;
    int tmpFehlerCount = 0;
    for (int i = 0; i < _typ.length; i++) {

      // wenn keine Fehler vorliegen
      if (_typ[i] == Fehler.Typ.KEIN_FEHLER && fehlerList.isEmpty()) {
        tmpFehlerCount = 1;

        // enthält die komplette Nachricht
      } else if (_typ[i] == Fehler.Typ.ENTHÄLT_VOLLE_NACHRICHT) {
        boolean isZwischenfehler = false;
        boolean isVorNach = false;
        for (Fehler fehler : fehlerList) {
          if (fehler.getTyp() == Fehler.Typ.ZWISCHEN_BIT_0_FEHLT || fehler.getTyp() == Fehler.Typ.ZWISCHEN_BIT_1_FEHLT ||
                  fehler.getTyp() == Fehler.Typ.ZWISCHEN_BIT_0_ZU_VIEL || fehler.getTyp() == Fehler.Typ.ZWISCHEN_BIT_1_ZU_VIEL) {
            isZwischenfehler = true;
            break;
          }
          if (fehler.getTyp() == Fehler.Typ.VOR_BIT_0_ZU_VIEL || fehler.getTyp() == Fehler.Typ.VOR_BIT_1_ZU_VIEL ||
                  fehler.getTyp() == Fehler.Typ.NACH_BIT_0_ZU_VIEL || fehler.getTyp() == Fehler.Typ.NACH_BIT_1_ZU_VIEL) {
            isVorNach = true;
          }
        }
        if (!isZwischenfehler && isVorNach) {
          tmpFehlerCount = 1;
        }

        // wenn Bit in der Nachricht zu viel sind
      } else if (_typ[i] == Fehler.Typ.ZWISCHEN_BIT_ZU_VIEL) {

        for (Fehler fehler : fehlerList) {
          if (fehler.getTyp() == Fehler.Typ.ZWISCHEN_BIT_1_ZU_VIEL || fehler.getTyp() == Fehler.Typ.ZWISCHEN_BIT_0_ZU_VIEL)
            tmpFehlerCount++;
        }

        // wenn bits in der Nachricht fehlen
      } else if (_typ[i] == Fehler.Typ.ZWISCHEN_BIT_FEHLT) {

        for (Fehler fehler : fehlerList) {
          if (fehler.getTyp() == Fehler.Typ.ZWISCHEN_BIT_1_FEHLT || fehler.getTyp() == Fehler.Typ.ZWISCHEN_BIT_0_FEHLT)
            tmpFehlerCount++;
        }

        // wenn Bits in der Nachricht gekipt sind
      } else if (_typ[i] == Fehler.Typ.ZWISCHEN_BIT_WECHSEL) {

        for (Fehler fehler : fehlerList) {
          if (fehler.getTyp() == Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_1 || fehler.getTyp() == Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_0)
            tmpFehlerCount++;
        }

        // wenn in der Nachricht nur ein Fehler aufgetreten ist
      } else if (_typ[i] == Fehler.Typ.EINBIT_FEHLER) {
        int countEinBitFehler = 0;
        for (Fehler fehler : fehlerList) {
          if (isZwischenFehler(fehler.getTyp()))
            countEinBitFehler++;
        }
        if (countEinBitFehler == 1)
          tmpFehlerCount++;

        // wenn in der Nachricht nur zwei Fehler aufgetreten sind
      } else if (_typ[i] == Fehler.Typ.ZWEIBIT_FEHLER) {
        int countZweiBitFehler = 0;
        for (Fehler fehler : fehlerList) {
          if (isZwischenFehler(fehler.getTyp()))
            countZweiBitFehler++;
        }
        if (countZweiBitFehler == 2)
          tmpFehlerCount++;

        // wenn in der Nachricht drei oder mehr Fehler auftreten
      } else if (_typ[i] == Fehler.Typ.DREIBIT_FEHLER) {
        int countDreiBitFehler = 0;
        for (Fehler fehler : fehlerList) {
          if (isZwischenFehler(fehler.getTyp()))
            countDreiBitFehler++;
        }
        if (countDreiBitFehler >= 3)
          tmpFehlerCount++;

        // wenn nach der Nachricht noch Bits folgen
      } else if (_typ[i] == Fehler.Typ.NACHBITS) {
        for (Fehler fehler : fehlerList) {
          if (fehler.getTyp() == Fehler.Typ.NACH_BIT_0_ZU_VIEL || fehler.getTyp() == Fehler.Typ.NACH_BIT_1_ZU_VIEL) {
            tmpFehlerCount++;
            break;
          }
        }

        // wenn der eigentlich gesuchte Fehler auftritt
      } else {

        for (Fehler fehler : fehlerList) {
          if (fehler.getTyp() == _typ[i])
            tmpFehlerCount++;
        }
      }
      fehlerCount += tmpFehlerCount;
      tmpFehlerCount = 0;
    }
    return fehlerCount;
  }

  public boolean isZwischenFehler(Fehler.Typ _fehlerTyp) {
    return _fehlerTyp == Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_1 ||
            _fehlerTyp == Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_0 ||
            _fehlerTyp == Fehler.Typ.ZWISCHEN_BIT_1_FEHLT ||
            _fehlerTyp == Fehler.Typ.ZWISCHEN_BIT_0_FEHLT ||
            _fehlerTyp == Fehler.Typ.ZWISCHEN_BIT_1_ZU_VIEL ||
            _fehlerTyp == Fehler.Typ.ZWISCHEN_BIT_0_ZU_VIEL;
  }

  public ArrayList<Fehler> getFehlerList() {
    return fehlerList;
  }

  public boolean hatShift() {
    return hatShift;
  }
}
