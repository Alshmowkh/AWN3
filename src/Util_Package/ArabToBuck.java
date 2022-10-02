/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util_Package;

import java.util.HashMap;

public class ArabToBuck extends HashMap<String, String> {

    public ArabToBuck() {
        this.put("\u0621", "'");
        this.put("\u0622", "|");
        this.put("\u0623", ">");
        this.put("\u0624", "&");
        this.put("\u0625", "<");
        this.put("\u0626", "}");
        this.put("\u0627", "A");
        this.put("\u0628", "b");
        this.put("\u0629", "p");
        this.put("\u062A", "t");
        this.put("\u062B", "v");
        this.put("\u062C", "j");
        this.put("\u062D", "H");
        this.put("\u062E", "x");
        this.put("\u062F", "d");
        this.put("\u0630", "*");
        this.put("\u0631", "r");
        this.put("\u0632", "z");
        this.put("\u0633", "s");
        this.put("\u0634", "$");
        this.put("\u0635", "S");
        this.put("\u0636", "D");
        this.put("\u0637", "T");
        this.put("\u0638", "Z");
        this.put("\u0639", "E");
        this.put("\u063A", "g");
        this.put("\u0640", "_");
        this.put("\u0641", "f");
        this.put("\u0642", "q");
        this.put("\u0643", "k");
        this.put("\u0644", "l");
        this.put("\u0645", "m");
        this.put("\u0646", "n");
        this.put("\u0647", "h");
        this.put("\u0648", "w");
        this.put("\u0649", "Y");
        this.put("\u064A", "y");
        this.put("\u064B", "F");
        this.put("\u064C", "N");
        this.put("\u064D", "K");
        this.put("\u064E", "a");
        this.put("\u064F", "u");
        this.put("\u0650", "i");
        this.put("\u0651", "~");
        this.put("\u0652", "o");
        this.put("\u0670", "`");
        this.put("\u0652", "{");
    }

    public String transliterate(String s) {
        String ret = "";
        for (int i = 0; i < s.length(); i++) {
            ret += this.get("" + s.charAt(i));
        }
        return ret;
    }
}
