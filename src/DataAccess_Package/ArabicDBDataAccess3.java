package DataAccess_Package;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class ArabicDBDataAccess3 {

    ArrayList<String> results;
    PreparedStatement ps;
    Connection conn;
    ResultSet rs;

    public ArabicDBDataAccess3() {

        try {
            connectDB();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(ArabicDBDataAccess3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void connectDB() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        conn = DriverManager.getConnection("jdbc:derby:F:\\Master\\Thesis\\Tools\\Ontology\\AWN\\wordnet");
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
        Properties pr = System.getProperties();
        pr.put("derby.storage.pageSize", "32000");
        pr.put("derby.storage.pageCacheSize", "5000");

    }

    void closeDB() throws SQLException {
        conn.close();
    }

    public ArrayList<Character> diacriticList() {
        ArrayList<Character> marks = new ArrayList();
        marks.add('َ');//فتحة
        marks.add('ِ');//كسرة
        marks.add('ُ');//ضمة
        marks.add('ْ');//سكون
        marks.add('ّ');//شدة
        marks.add('ٌ');//تنوين مضموم
        marks.add('ً');//تنوين مفتوح
        marks.add('ٍ');//تنوين مكسور

        return marks;

//        public ArabicDiacritics() {
//		this.add("\u064E");
//		this.add("\u064F");
//		this.add("\u0650");
//		this.add("\u0651");
//		this.add("\u0652");
//		this.add("\u0653");
//		this.add("\u0654");
//		this.add("\u0655");
//		this.add("\u064B");
//		this.add("\u064C");
//		this.add("\u064D");
//	}
    }

    public Boolean isdiacrited(String word) throws SQLException {

        return word.contains("َ") | word.contains("ِ") | word.contains("ُ") | word.contains("ْ") | word.contains("ّ");
    }

    public ArrayList<String> readWordSenses(String word) throws SQLException {
        String searchString = "";
        if (this.isdiacrited(word)) {
            ps = conn.prepareStatement("SELECT synsetid FROM word WHERE value =?");
            ps.setString(1, word);
            rs = ps.executeQuery();

        } else {
            char[] wordChars = word.toCharArray();
            for (int i = 0; i < wordChars.length; i++) {
                searchString += wordChars[i] + "%";
            }

            ps = conn.prepareStatement("SELECT synsetid FROM word WHERE value LIKE ?");
            ps.setString(1, searchString);
            rs = ps.executeQuery();
        }
        if (!rs.next()) {
            char[] wordChars = word.toCharArray();
            for (int i = 0; i < wordChars.length; i++) {
                searchString += wordChars[i] + "%";
            }
            ps = conn.prepareStatement("SELECT synsetid FROM word WHERE value LIKE ?");
            ps.setString(1, searchString);
            rs = ps.executeQuery();
        }

        results = new ArrayList();

        while (rs.next()) {
            String ssid = rs.getString("synsetid");
            results.add(ssid);
        }
        return results;
    }

    public ArrayList<String> getSynsets_diac(String word) throws SQLException {

        results = new ArrayList();
        if (this.isdiacrited(word)) {
            ps = conn.prepareStatement("SELECT synsetid FROM word WHERE value = ?");
            ps.setString(1, word);
            rs = ps.executeQuery();
        } else {
            System.out.println("The word " + word + " is not diacritic");
            return results;
        }

        while (rs.next()) {
            String ssid = rs.getString("synsetid");
            results.add(ssid);
        }
        return results;
    }

    public ArrayList<String> getSynsets_noDiac(String word) throws SQLException {
        String searchString = "";

        char[] wordChars = word.toCharArray();
        for (int i = 0; i < wordChars.length; i++) {
            searchString += wordChars[i] + "%";
        }
//        System.out.println(searchString);
        ps = conn.prepareStatement("SELECT synsetid FROM word WHERE value LIKE ?");
        ps.setString(1, searchString);
        rs = ps.executeQuery();

        results = new ArrayList();

        while (rs.next()) {
            String ssid = rs.getString("synsetid");
            results.add(ssid);
        }

        return results;
    }

    public ArrayList<String> getSynsets0(String word) throws SQLException {
        String searchString = "";

        char[] wordChars = word.toCharArray();
        for (int i = 0; i < wordChars.length; i++) {
            searchString += wordChars[i] + "%";
        }

        ps = conn.prepareStatement("SELECT synsetid FROM word WHERE value LIKE ?");
        ps.setString(1, searchString);
        rs = ps.executeQuery();

        results = new ArrayList();

        while (rs.next()) {
            String ssid = rs.getString("synsetid");
            results.add(ssid);
        }

        return results;
    }

    public ArrayList<String> getSynsetsRandom(String word) throws SQLException {
        String searchString = "";
        char[] wordChars = word.toCharArray();
        for (int i = 0; i < wordChars.length; i++) {
            char ch = wordChars[i];
            if (!diacriticList().contains(ch)) {
                searchString += ch + "%";
            }
        }
        System.out.println(searchString);
        ps = conn.prepareStatement("SELECT synsetid FROM word WHERE value LIKE ?");
        ps.setString(1, searchString);
        rs = ps.executeQuery();

        results = new ArrayList();

        while (rs.next()) {
            String ssid = rs.getString("synsetid");
            results.add(ssid);
        }
        return results;
    }

    public ArrayList<String> readSynsetMembers(String synsetid) throws SQLException {

        results = new ArrayList<>();

        ps = conn.prepareStatement("SELECT value FROM word WHERE synsetid = ?");
        ps.setString(1, synsetid);
        rs = ps.executeQuery();
        while (rs.next()) {
            results.add(rs.getString("value"));
        }

        return results;
    }

    public String vector2csv(ArrayList words) {
        if (words == null || words.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder(((String) words.get(0))
                .replaceAll("_", " "));
        for (int i = 1; i < words.size(); i++) {
            result.append(',');
            result.append(new StringBuffer(((String) words.get(i)).replaceAll(
                    "_", " ")));
        }
        if (result.length() > 140) {
            return result.substring(0, 140) + "..........more";
        }
        return result.toString();
    }

    public HashMap<String, String> getHyponyms(String synsetid) throws SQLException {

        HashMap<String, String> resultsMap = new HashMap<>();

        ps = conn.prepareStatement("SELECT link1,authorship.author FROM link,item,authorship WHERE  link.type='hyponym' AND link2= ? AND itemid=link1 AND item.authorshipid=authorship.authorshipid");
        ps.setString(1, synsetid);
        rs = ps.executeQuery();
        while (rs.next()) {
            String link1 = rs.getString("link1");
            String author = rs.getString("author");
            resultsMap.put(link1, author);
        }
        return resultsMap;
    }

    public ArrayList<String> getHyponymsList(String synsetid) throws SQLException {

        ps = conn.prepareStatement("SELECT link1,authorship.author FROM link,item,authorship WHERE  link.type='hyponym' AND link2= ? AND itemid=link1 AND item.authorshipid=authorship.authorshipid");
        ps.setString(1, synsetid);
        rs = ps.executeQuery();
        results = new ArrayList();
        while (rs.next()) {
            String link1 = rs.getString("link1");
            results.add(link1);
        }
        return results;
    }

    public ArrayList<String> getArabicHyponyms(String synsetid) throws SQLException {

        ps = conn.prepareStatement("SELECT link1,authorship.author FROM link,item,authorship WHERE  link.type='hyponym' AND link2= ? AND itemid=link1 AND item.authorshipid=authorship.authorshipid");
        ps.setString(1, synsetid);
        rs = ps.executeQuery();
        ArrayList<String> res = new ArrayList();
        while (rs.next()) {
            String link1 = rs.getString("link1");
            res.add(link1);
        }
        results = new ArrayList();
        for (String arb : res) {
            if (hasTranslation(arb)) {
                results.add(arb);
            }
        }
        return results;
    }

    public String getHypernym(String synsetid) throws SQLException {

        String result = "";

        ps = conn.prepareStatement("SELECT link2 FROM link where link1 = ?");
        ps.setString(1, synsetid);
        rs = ps.executeQuery();
        while (rs.next()) {
            result = rs.getString("link2");
        }

        return result;
    }

    public ArrayList<String> getFirstLevelNodes() throws SQLException {
        ArrayList<String> firstLevel = new ArrayList();
        results = new ArrayList();
        ps = conn.prepareStatement("SELECT * FROM starters");
        rs = ps.executeQuery();
        while (rs.next()) {
            String synsetid = rs.getString("synsetid");
            results.add(synsetid);
        }
        for (String id : results) {
            if (hasTranslation(id)) {
                firstLevel.add(id);
            }
        }
        return firstLevel;
    }

    public Boolean hasTranslation(String synsetid) throws SQLException {

        boolean isTrans = false;
        ps = conn.prepareStatement("SELECT * FROM has_translation WHERE synsetid=?");
        ps.setString(1, synsetid);
        rs = ps.executeQuery();
        while (rs.next()) {
            isTrans = true;
        }
        return isTrans;
    }

    public ArrayList<String> getSynsetData(String synsetid) throws SQLException {
        results = new ArrayList<>();

        ps = conn.prepareStatement("SELECT gloss,pos,pwnid FROM item WHERE itemid = ?");
        ps.setString(1, synsetid);
        rs = ps.executeQuery();
        while (rs.next()) {
            String gloss = rs.getString("gloss");
            results.add(gloss);
            String pos = rs.getString("pos");
            results.add(pos);
            String pwnid = rs.getString("pwnid");
            results.add(pwnid);

        }
        return results;
    }

    public String getTopLevelSynsetsForTree(String synsetid) throws SQLException {

        String result = "";
        ps = conn.prepareStatement("SELECT arabicid FROM arabicstarters WHERE synsetid= ? ");
        ps.setString(1, synsetid);
        rs = ps.executeQuery();
        while (rs.next()) {
            result = rs.getString("arabicid");
        }
        return result;
    }

    public String getTranslation(String synsetid, String lang) throws SQLException {

        String result = "";

        if (lang.equals("Arabic")) {
            ps = conn.prepareStatement("SELECT link1 FROM link WHERE link2= ? AND type='equivalent'");
            ps.setString(1, synsetid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result = rs.getString("link1");
            }
        } else if (lang.equals("English")) {
            ps = conn.prepareStatement("SELECT link2 FROM link WHERE link1= ? AND type='equivalent'");
            ps.setString(1, synsetid);
            rs = ps.executeQuery();
            while (rs.next()) {
                result = rs.getString("link2");
            }
        }

        return result;
    }

    ArrayList<String> getAllSynsets() throws SQLException {
        //all tables in Wordnet
        //    item=126693;  form=16998;  word=226628; link=161705;  
//    starters=340;  has_translation=10414; mappings=97410;
//        conversion1=115424;  authorship=168876;  config=1; 
        //        ps = conn.prepareStatement("SELECT * FROM has_translation WHERE synsetid=?");

        results = new ArrayList();
        ps = conn.prepareStatement("select synsetid from word");

        rs = ps.executeQuery();
        while (rs.next()) {
            results.add(rs.getString("synsetid"));
        }
        return results;
    }

    ArrayList<String> getAllNouns() throws SQLException {
        results = new ArrayList();
        ps = conn.prepareStatement("select value,synsetid from word");

        rs = ps.executeQuery();
        while (rs.next()) {
            String synsetid = rs.getString("synsetid");
            String pos = String.valueOf(synsetid.charAt(synsetid.lastIndexOf("_") + 1));
            if ("n".equals(pos)) {
                results.add(rs.getString("value"));
            }
        }
        return results;
    }

    ArrayList<String> getAllhasTrans() throws SQLException {
        results = new ArrayList();
        ps = conn.prepareStatement("select * from has_translation");

        rs = ps.executeQuery();
        while (rs.next()) {

            results.add(rs.getString(1));
        }

        return results;
    }

    ArrayList<String> getArabicNouns() throws SQLException {
        results = new ArrayList();
        ps = conn.prepareStatement("select value,synsetid from word");

        rs = ps.executeQuery();
        while (rs.next()) {
            String synsetid = rs.getString("synsetid");
            String pos = String.valueOf(synsetid.charAt(synsetid.lastIndexOf("_") + 1));
            String lang = synsetid.substring(synsetid.length() - 2, synsetid.length());
            String value = rs.getString("value").trim();
            if ("n".equals(pos) && "AR".equals(lang) && !results.contains(value)) {
                results.add(value);
            }
        }
        return results;
    }
//    public String getSynsetID(String buck) {
//          ps = conn.prepareStatement("SELECT * FROM word WHERE value =?");
//            ps.setString(1, buck);
//            rs = ps.executeQuery();
//    }

    ArrayList<String> getAllSynsetsAdj() throws SQLException {
        //all tables in Wordnet
        //    item=126693;  form=16998;  word=226628; link=161705;  
//    starters=340;  has_translation=10414; mappings=97410;
//        conversion1=115424;  authorship=168876;  config=1; 
        //        ps = conn.prepareStatement("SELECT * FROM has_translation WHERE synsetid=?");
        String temp;
        String[] ary;
        results = new ArrayList();
        ps = conn.prepareStatement("select synsetid from word");

        rs = ps.executeQuery();
        while (rs.next()) {
            temp = rs.getString("synsetid");
            ary = temp.split("_");
            if (ary.length > 0) {
                if (ary[ary.length - 1].startsWith("a") && ary[ary.length - 1].endsWith("AR")) {
                    if (!results.contains(temp)) {
                        results.add(temp);
                    }
                }
            }
        }
        return results;
    }

    void test() throws SQLException {
//        int i = 1;
        ArrayList<String> set;
//        set= this.getSynsets_noDiac("كتب");
//        set = this.getFirstLevelNodes();

        set = this.getAllSynsetsAdj();
        for (String syn : set) {

            System.out.println(this.readSynsetMembers(syn));
//            System.out.println(syn);
        }
//        System.out.println(this.getHypernym("majal~ap_n2AR"));
    }

    void test2() throws SQLException {
//        for (String s : getSynsets_noDiac("حياة")) {
////            i++;
//            System.out.println(readSynsetMembers(s));
//        }
        results=this.getFirstLevelNodes();
        
    }

    public static void main(String[] args) throws SQLException {
        ArabicDBDataAccess3 cls = new ArabicDBDataAccess3();
        cls.test2();
//        int i = 0;
//        for (String s : cls.getSynsets_noDiac("حياة")) {
////            i++;
//            System.out.println(s);
//
//        }
//        System.out.println(i);
    }

}
