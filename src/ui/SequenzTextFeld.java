package ui;

import generell.CodeGenerator;
import javafx.scene.control.TextField;

public class SequenzTextFeld extends TextField {
  @Override
  public void replaceText(int start, int end, String text)
  {
    if (validate(text))
    {
      super.replaceText(start, end, text);
    }
  }

  @Override
  public void replaceSelection(String text)
  {
    if (validate(text))
    {
      super.replaceSelection(text);
    }
  }

  private boolean validate(String text)
  {
    if(text.length() > 0 && text.charAt(0) == '-')
      return true;
    return text.matches("[0-9abcdefx]*");
  }
}
