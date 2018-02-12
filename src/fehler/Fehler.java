package fehler;

public class Fehler {
  private Typ typ;
  private int index = 1;

  public enum Typ {
    ZWISCHEN_BIT_0_FEHLT (0),
    ZWISCHEN_BIT_1_FEHLT (1),
    ZWISCHEN_BIT_0_ZU_VIEL (2),
    ZWISCHEN_BIT_1_ZU_VIEL (3),
    ZWISCHEN_BIT_WECHSEL_ZU_0 (4),
    ZWISCHEN_BIT_WECHSEL_ZU_1 (5),
    VOR_BIT_0_ZU_VIEL (6),
    VOR_BIT_1_ZU_VIEL (7),
    NACH_BIT_0_ZU_VIEL (8),
    NACH_BIT_1_ZU_VIEL (9),
    KEINE_ÜBEREINSTIMMUNG (10),
    NICHT_EXISTENT (11),
    KEIN_FEHLER (12),
    ENTHÄLT_VOLLE_NACHRICHT (13),
    ZWISCHEN_BIT_ZU_VIEL (14),
    ZWISCHEN_BIT_FEHLT (15),
    ZWISCHEN_BIT_WECHSEL (16),
    EINBIT_FEHLER (17),
    ZWEIBIT_FEHLER (18),
    DREIBIT_FEHLER (19),
    SHIFT (20),
    NICHT_IM_SEQUENZRAUM (21),
    SEQUENZ_DUPLIKAT (22),
    NACHBITS (23),
    ZWISCHEN_BITFEHLER (24);

    public final int id;
    public static final int size = Typ.values().length;

    Typ(int _id) {
      id = _id;
    }
  }

  public Fehler(Typ _typ, int _index) {
    typ = _typ;
    index = _index;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[").append(index).append(", ").append(typ).append("]");
    return  sb.toString();
  }

  public Typ getTyp() {
    return typ;
  }

  public int getIndex() {
    return index;
  }
}
