package generell;

public class SetUp {
  private class SetUpElement {
    private String select;
    private String nachrichten;
    private String sollPause;
    private String sequenz;
    private boolean statistik;
    private boolean fehlerVerteilung;
    private boolean pausenlänge;
    private boolean pausenVerteilung;
    private boolean fehlerSummenVerteilung;
    private String maske;

    public SetUpElement(String _select, String _nachrichten, String _sollPause, String _sequenz, boolean _statistik, boolean _fehlerVerteilung, boolean _pausenlänge, boolean _pausenVerteilung, boolean _fehlerSummenVerteilung, String _maske) {
      select = _select;
      nachrichten = _nachrichten;
      sollPause = _sollPause;
      sequenz = _sequenz;
      statistik = _statistik;
      fehlerVerteilung = _fehlerVerteilung;
      pausenlänge = _pausenlänge;
      pausenVerteilung = _pausenVerteilung;
      fehlerSummenVerteilung = _fehlerSummenVerteilung;
      maske = _maske;
    }
  }

  private SetUpElement setUpElemente[] = {
          new SetUpElement(  // (0) standard
                  "",
                  "0", "0", "0-",
                  true, true, true, true, true,
                  ""
          ),
          new SetUpElement(  // (1) testSetUp 1
                  "/home/studi/usrp-Dateien/fakeProtokoll_fehlerTest.txt",
                  "16", "0", "0-15",
                  true, true, false, false, true,
                  "101010101010101011111111XXXX11110000111101010011"
          ),
          new SetUpElement(  // (2) testSetUp xml
                  "/home/studi/usrp-Dateien/1001Bits_001.xml",
                  "1000", "20000", "0",
                  true, true, true, true, true,
                  "1010101010101010111111110000111100001111"
          ),
          new SetUpElement(  // (3) testSetUp Einzelnachricht
                  "/home/studi/usrp-Dateien/fakeProtokoll_test_Einzelnachricht.txt",
                  "1", "0", "0",
                  true, true, false,false, true,
                  "1010101010101010111111110000111100001111"
          ),
          new SetUpElement(  // (4) 1.000.000 Nachrichten
                  "/home/studi/BIT-Fehler_Statistik_Rohdaten/2017-11-30_1000000_Nachrichten_S1M_F433920KH_B50_P20ms.txt",
                  "1000001", "0", "0-1000000",
                  true, false, false, false, true,
                  "101010101010101011111111XXXXXXXXXXXXXXXXXXXX11110000111100001100110010101010111100001001100100001111001100110000000011111111010101010000111100110011"
          ),
          new SetUpElement(  // (5) 1.000.000 Nachrichten
                  "/home/studi/BIT-Fehler_Statistik_Rohdaten/2017-11-30_80000_Narichten_S1M_F433920KH_B100_P100ms.txt",
                  "80001", "0", "0-80000",
                  true, false, false, false, true,
                  "101010101010101011111111XXXXXXXXXXXXXXXXXXXX111100001100110011100001"
          )
  };

  public String getSelect(int _index) {
    return setUpElemente[_index].select;
  }

  public String getNachrichten(int _index) {
    return setUpElemente[_index].nachrichten;
  }

  public String getSollPause(int _index) {
    return setUpElemente[_index].sollPause;
  }

  public String getSequenz(int _index) {
    return setUpElemente[_index].sequenz;
  }

  public boolean isStatistik(int _index) {
    return setUpElemente[_index].statistik;
  }

  public boolean isFehlerVerteilung(int _index) {
    return setUpElemente[_index].fehlerVerteilung;
  }

  public boolean isPausenlänge(int _index) {
    return setUpElemente[_index].pausenlänge;
  }

  public boolean isPausenVerteilung(int _index) {
    return setUpElemente[_index].pausenVerteilung;
  }

  public boolean isFehlerSummenVerteilung(int _index) {
    return setUpElemente[_index].fehlerSummenVerteilung;
  }

  public String getMaske(int _index) {
    return setUpElemente[_index].maske;
  }
}
