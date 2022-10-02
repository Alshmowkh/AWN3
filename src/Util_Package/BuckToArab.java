
package Util_Package;

import java.util.HashMap;

public class BuckToArab extends HashMap<String, String> {
    public BuckToArab () {
	this.put("'","\u0621");
	this.put("|","\u0622");
	this.put(">","\u0623");
	this.put("&","\u0624");
	this.put("<","\u0625");
	this.put("}","\u0626");
	this.put("A","\u0627");
	this.put("b","\u0628");
	this.put("p","\u0629");
	this.put("t","\u062A");
	this.put("v","\u062B");
	this.put("j","\u062C");
	this.put("H","\u062D");
	this.put("x","\u062E");
	this.put("d","\u062F");
	this.put("*","\u0630");
	this.put("r","\u0631");
	this.put("z","\u0632");
	this.put("s","\u0633");
	this.put("$","\u0634");
	this.put("S","\u0635");
	this.put("D","\u0636");
	this.put("T","\u0637");
	this.put("Z","\u0638");
	this.put("E","\u0639");
	this.put("g","\u063A");
	this.put("_","\u0640");
	this.put("f","\u0641");
	this.put("q","\u0642");
	this.put("k","\u0643");
	this.put("l","\u0644");
	this.put("m","\u0645");
	this.put("n","\u0646");
	this.put("h","\u0647");
	this.put("w","\u0648");
	this.put("Y","\u0649");
	this.put("y","\u064A");
	this.put("F","\u064B");
	this.put("N","\u064C");
	this.put("K","\u064D");
	this.put("a","\u064E");
	this.put("u","\u064F");
	this.put("i","\u0650");
	this.put("~","\u0651");
	this.put("o","\u0652");
	this.put("`","\u0670");
	this.put("{","\u0652");
    }

  public String transliterate(String s) {
    String ret = "";
    for (int i=0;i<s.length();i++) {
      ret +=this.get(""+s.charAt(i));
    }
    return ret;
  }
}
