package fehler;

import generell.Message;

import java.util.Arrays;

public class FehlerStatistik {
  private int fehlerCountArray[] = new int[Fehler.Typ.size];
  private Fehler.Typ fehlerAbfrage[] = {
          Fehler.Typ.KEIN_FEHLER,
          Fehler.Typ.KEINE_ÜBEREINSTIMMUNG,
          Fehler.Typ.ENTHÄLT_VOLLE_NACHRICHT,
          Fehler.Typ.ZWISCHEN_BIT_ZU_VIEL,
          Fehler.Typ.ZWISCHEN_BIT_FEHLT,
          Fehler.Typ.ZWISCHEN_BIT_WECHSEL,
          Fehler.Typ.EINBIT_FEHLER,
          Fehler.Typ.ZWEIBIT_FEHLER,
          Fehler.Typ.DREIBIT_FEHLER,
          Fehler.Typ.ZWISCHEN_BIT_1_FEHLT,
          Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_1,
          Fehler.Typ.ZWISCHEN_BIT_1_ZU_VIEL,
          Fehler.Typ.ZWISCHEN_BIT_0_FEHLT,
          Fehler.Typ.ZWISCHEN_BIT_WECHSEL_ZU_0,
          Fehler.Typ.ZWISCHEN_BIT_0_ZU_VIEL,
          Fehler.Typ.SHIFT,
          Fehler.Typ.SEQUENZ_DUPLIKAT,
          Fehler.Typ.NICHT_IM_SEQUENZRAUM,
          Fehler.Typ.NACHBITS,
          Fehler.Typ.ZWISCHEN_BITFEHLER
  };

  public FehlerStatistik() {
    Arrays.fill(fehlerCountArray, 0);
  }

  public void checkMessage(Message _message) {
    for (Fehler.Typ typ : fehlerAbfrage) {
      if (_message.countFehler(typ) > 0) {
        fehlerCountArray[typ.id]++;
      }
    }
  }

  public int get(Fehler.Typ... _typ) {
    int fehlerCount = 0;
    for (int i = 0; i < _typ.length; i++) {
      fehlerCount += fehlerCountArray[_typ[i].id];
    }
    return fehlerCount;
  }
}
