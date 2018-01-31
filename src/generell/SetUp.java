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
    private boolean sequenzen;
    private String maske;

    public SetUpElement(String _select, String _nachrichten, String _sollPause, String _sequenz, boolean _statistik, boolean _fehlerVerteilung, boolean _pausenlänge, boolean _pausenVerteilung, boolean _fehlerSummenVerteilung,boolean _sequenzen , String _maske) {
      select = _select;
      nachrichten = _nachrichten;
      sollPause = _sollPause;
      sequenz = _sequenz;
      statistik = _statistik;
      fehlerVerteilung = _fehlerVerteilung;
      pausenlänge = _pausenlänge;
      pausenVerteilung = _pausenVerteilung;
      fehlerSummenVerteilung = _fehlerSummenVerteilung;
      sequenzen = _sequenzen;
      maske = _maske;
    }
  }

  private SetUpElement setUpElemente[] = {
          new SetUpElement(  // (0) standard
                  "",
                  "0", "0", "0-",
                  true, true, false, false, true, false,
                  ""
          ),
          new SetUpElement(  // (1) testSetUp 1
                  "/home/studi/usrp-Dateien/fakeProtokoll_fehlerTest.txt",
                  "16", "0", "0-15",
                  true, true, false, false, true, false,
                  "101010101010101011111111XXXX11110000111101010011"
          ),
          new SetUpElement(  // (2) testSetUp xml
                  "/home/studi/usrp-Dateien/1001Bits_001.xml",
                  "1000", "20000", "0",
                  true, true, true, true, true, false,
                  "1010101010101010111111110000111100001111"
          ),
          new SetUpElement(  // (3) testSetUp Einzelnachricht
                  "/home/studi/usrp-Dateien/fakeProtokoll_test_Einzelnachricht.txt",
                  "1", "0", "0",
                  true, true, false,false, true, false,
                  "1010101010101010111111110000111100001111"
          ),
          new SetUpElement(  // (4) 1.000.000 Nachrichten
                  "/home/studi/BIT-Fehler_Statistik_Rohdaten/2017-11-30_1000000_Nachrichten_S1M_F433920KH_B50_P20ms.txt",
                  "1000001", "0", "0-1000000",
                  true, false, false, false, true, false,
                  "101010101010101011111111XXXXXXXXXXXXXXXXXXXX11110000111100001100110010101010111100001001100100001111001100110000000011111111010101010000111100110011"
          ),
          new SetUpElement(  // (5) 1.000.000 Nachrichten
                  "/home/studi/BIT-Fehler_Statistik_Rohdaten/2017-11-30_80000_Narichten_S1M_F433920KH_B100_P100ms.txt",
                  "80001", "0", "0-80000",
                  true, false, false, false, true, false,
                  "101010101010101011111111XXXXXXXXXXXXXXXXXXXX111100001100110011100001"
          ),
          new SetUpElement(  // (6) 20.000 Nachrichten
                  "/home/studi/BIT-Fehler_Statistik_Rohdaten/RTL-SDR/2017-12-04_20000_Narichten_RTL_S1M_F433920KH_B100_P100ms_001.txt",
                  "20001", "0", "0b10000100001000010-0b10101011001100010",
                  true, false, false, false, true, false,
                  "10101010101010101111111101000XXXXXXXXXXXXXXXXX11111111000000001111111111111111000000001100110011100001"
          ),
          new SetUpElement(  // (7) 1.000 Nachrichten
                  "/home/studi/BIT-Fehler_Statistik_Rohdaten/nebenbei/USRP_RTL-SDR_ASK_F433920K_SR1M_BL20_P20ms.txt",
                  "1000", "0", "0",
                  true, true, false, false, true, false,
                  "101010101010101011111111001100110000111100001111011001100010100001111000000001111111110101111111111111111"
          ),
          new SetUpElement(  // (8) 4.096 Nachrichten
                  "/home/studi/BIT-Fehler_Statistik_Rohdaten/nebenbei2/USRP-USRP_0N0129_0C0907_BL50_ET3_ASK_F433M92_SR1M_P10m_G30-G30.txt",
                  "4096", "0", "0-4095",
                  true, true, false, false, true, true,
                  "101010101010101011111111XXXX1XXXX1XXXX11110000111100001010110011001100111100001111000010101010111101101001000011110101000011001100111101010101000011110011001100001111"
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

  public boolean isBitfehlerVerteilung(int _index) {
    return setUpElemente[_index].fehlerSummenVerteilung;
  }

  public boolean isSequenzen(int _index) {
    return setUpElemente[_index].sequenzen;
  }

  public String getMaske(int _index) {
    return setUpElemente[_index].maske;
  }
}
