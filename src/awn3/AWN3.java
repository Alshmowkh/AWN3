package awn3;

import DataAccess_Package.ArabMorph;
import DataAccess_Package.ArabicDBDataAccess3;
import Util_Package.ArabToBuck;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AWN3 {

    public ArabicWordSenseList3 aws;
    public ArabicDBDataAccess3 adb;
    private ArrayList<String> list;
    ArrayList<String> entities;
    ArabToBuck a2b;
    public String rheDoc;
    public ArrayList<Node> ESFsList;
    public String ESFdoc;
    DocumentBuilderFactory dbf;
    DocumentBuilder db;
    Document doc;

    Boolean onceListFill;

    public AWN3() {
        aws = new ArabicWordSenseList3();
        adb = new ArabicDBDataAccess3();
        a2b = new ArabToBuck();
//        rheDoc = "F:\\Master\\Thesis\\Implementations\\Rhe_Processes\\Rhe_lib\\out_puts\\Alpha_Beta\\Rhe_2.xml";

        ESFsList = new ArrayList();
        setEntities();
        onceListFill = false;

    }

    private void setEntities() {
        entities = new ArrayList();
        entities.add("PERSON");
        entities.add("INSTRUMENT");
        entities.add("ORGANIZATION");
        entities.add("ANIMAL");
        entities.add("PLANT");
        entities.add("MEASURE");
        entities.add("PROCESS");
        entities.add("LOCATION");
        entities.add("GROUP");
        entities.add("NATURAL_OBJECT");
        entities.add("EVENT");
        entities.add("PSYCHOLOGICAL_FEATURE");
        entities.add("ARTIFACT");
    }

    void printWordSenses(String word) throws SQLException {
        ArabMorph aMorph = new ArabMorph();
        ArrayList<String> hset = aMorph.getWordMorphs(word);
        for (String m : hset) {
            ArrayList<String> syns = aws.getWordSenses(aws.b2a.transliterate(m));
            for (String s : syns) {
                ArrayList<String> membrs = adb.readSynsetMembers(s);
                for (String mb : membrs) {
                    System.out.println(mb);
                }
                System.out.println();
            }
            System.out.println("-----------------");
        }
    }

    public Map getAlpahBetaMap(String path) throws ParserConfigurationException, IOException, SAXException {
        dbf = DocumentBuilderFactory.newInstance();
        db = dbf.newDocumentBuilder();
        doc = db.parse(new File(path));

        NodeList phrases = doc.getElementsByTagName("phrase");
        Map<Integer, ArrayList<String>> attrs = new HashMap();
        if (phrases.getLength() > 0) {

            for (int i = 0; i < phrases.getLength(); i++) {
                int id = Integer.parseInt(phrases.item(i).getAttributes().getNamedItem("id").getNodeValue());
                String likTo = phrases.item(i).getAttributes().getNamedItem("A_likenedTo").getNodeValue();
                String pred = phrases.item(i).getAttributes().getNamedItem("B_predicate").getNodeValue();
                String lik = phrases.item(i).getAttributes().getNamedItem("C_likened").getNodeValue();
                attrs.put(id, new ArrayList<>(Arrays.asList(likTo, pred, lik)));
            }
        } else {
            p("The document \"" + rheDoc + "\"  has no candidate ESF");
        }

        return attrs;
    }

    public Map getESFcandidates(String path) throws ParserConfigurationException, IOException, SAXException {
        dbf = DocumentBuilderFactory.newInstance();
        db = dbf.newDocumentBuilder();
        doc = db.parse(new File(path));

        NodeList phrases = doc.getElementsByTagName("ESF_Phrase");
        Map<Integer, ArrayList<String>> attrs = new HashMap();
        if (phrases.getLength() > 0) {

            for (int i = 0; i < phrases.getLength(); i++) {
                String pred = phrases.item(i).getAttributes().getNamedItem("predicate").getNodeValue();
                int id = Integer.parseInt(phrases.item(i).getAttributes().getNamedItem("id").getNodeValue());

                String likTo = phrases.item(i).getChildNodes().item(1).getAttributes().getNamedItem("stem").getNodeValue();
                String lik = phrases.item(i).getChildNodes().item(3).getAttributes().getNamedItem("stem").getNodeValue();
                attrs.put(id, new ArrayList<>(Arrays.asList(likTo, pred, lik)));
            }
        } else {
            p("The document \"" + rheDoc + "\"  has no candidate ESF");
        }

        return attrs;
    }

    public NodeList getAlpahBetaNodes(String path) throws ParserConfigurationException, IOException, SAXException {
        dbf = DocumentBuilderFactory.newInstance();
        db = dbf.newDocumentBuilder();
        doc = db.parse(new File(path));

        NodeList phrases = doc.getElementsByTagName("ESF_Phrase");
        if (phrases.getLength() == 0) {
            p("The document \"" + rheDoc + "\"  has no candidate ESF");
        }
        return phrases;
    }

    HashMap<String, ArrayList<String>> getArabicWordSenses(String word) throws SQLException {
        ArabMorph aMorph = new ArabMorph();
        ArrayList<String> hset = aMorph.getWordMorphs(word);

        HashMap<String, ArrayList<String>> senses = new HashMap();
        for (String m : hset) {

            ArrayList<String> syns = aws.getWordSenses(aws.b2a.transliterate(m));
            for (String s : syns) {
                ArrayList<String> membrs = adb.readSynsetMembers(s);
                senses.put(m, membrs);
            }
        }
        return senses;
    }

    public ArrayList<String> getEndPath(String word) throws SQLException {

        list = new ArrayList();
        ArrayList<String> allSenses, synsets;
        ArabMorph aMorph = new ArabMorph();
        String stemOrg = aws.deDiacritic(aws.b2a.transliterate(aMorph.getStem(word)));
        System.out.println("stem " + stemOrg);
        if (!adb.isdiacrited(word)) {

            allSenses = aMorph.getNounsStems(word);

            for (String sense : allSenses) {
                synsets = adb.getSynsets_diac(aws.convertToScript(sense));
                System.out.println(synsets);
                for (String sy : synsets) {
                    ArrayList<String> members = aws.getSynsetMembers(sy, stemOrg);

                    if (!members.isEmpty()) {

                        list = aws.getHyperPath(sy);
                        return list;
                    }
                }

            }
        } else {
            synsets = adb.getSynsets_diac(word);
//            System.out.println(synsets);
            if (!synsets.isEmpty()) {
                list = aws.getHyperPath(synsets.get(0));
                return list;
            }
        }
        synsets = adb.getSynsets_noDiac(stemOrg);
//        System.out.println(synsets);
        for (String sy : synsets) {
            ArrayList<String> members = aws.getSynsetMembers(sy, stemOrg);
            if (!members.isEmpty()) {
                list = aws.getHyperPath(sy);
                return list;
            }
        }
//       

        System.out.println("------------The \"" + word + " \"is not included in Knowledge Base ");
        return null;
    }

    String getNED(String entity) throws SQLException {

        ArrayList<String> path = getEndPath(entity);
        System.out.println("path :" + path);
        if (path != null) {
            String named;
            for (String n : path) {
                String normal = aws.removeWNSuffix(n).toUpperCase();
                if (entities.contains(normal)) {
                    named = entities.get(entities.indexOf(normal));
                    return named;
                }
            }
        }
        return null;
    }

    Boolean isESF(String alpha, String predicate, String beta) throws SQLException {

        String entityX = getNED(alpha);
        String entityY = getNED(beta);
        if (entityX == null) {
            System.out.println("The entity of " + alpha + "  is not recognized name");
            return null;
        }
        if (entityY == null) {
            System.out.println("The entity of " + beta + "  is not recognized name");
            return null;
        }
        return !entityX.equals(entityY);
    }

    void printESFstate(Object state) {

        p("\n\t\t\t");
        p("--------------------------------");
        p("\n\t\t\t|\t\t\t\t|");
        p("\n\t\t\t|\t\t\t\t|");
        p("\n\t\t\t|\t\t" + state + "\t\t|");
        p("\n\t\t\t|\t\t\t\t|");
        p("\n\t\t\t|\t\t\t\t|");
        p("\n\t\t\t");
        p("--------------------------------");
        p("\n");

    }

    void p(Object o) {
        System.out.print(o);
    }

    public void semanicQuery() throws SQLException, ParserConfigurationException, IOException, SAXException {
        Map attrs = this.getESFcandidates(rheDoc);
        for (Object i : attrs.keySet()) {
            ArrayList ary = (ArrayList) attrs.get(i);

            String alpha = ary.get(0).toString();
            String beta = ary.get(2).toString();
            p("\n" + alpha);
            p("  " + beta + "\n");

            printESFstate(isESF(alpha, "", beta));
        }
    }

    public void ESFdetector() throws SQLException, ParserConfigurationException, IOException, SAXException, TransformerException {
        if (!rheDoc.isEmpty()) {
            NodeList ends = getAlpahBetaNodes(rheDoc);
            ESFsList = new ArrayList();
            if (ends.getLength() > 0) {
                for (int i = 0; i < ends.getLength(); i++) {

                    String alpha = ends.item(i).getChildNodes().item(1).getAttributes().getNamedItem("stem").getNodeValue();
                    String predicate = ends.item(i).getAttributes().getNamedItem("predicate").getNodeValue();
                    String beta = ends.item(i).getChildNodes().item(3).getAttributes().getNamedItem("stem").getNodeValue();

//                    p("\n" + alpha);
//                    p("  "+ predicate);
//                    p("  " + beta + "\n");
                    try {
                        String state = isESF(alpha, "", beta).toString().trim().toUpperCase();
                        if (state.equals("TRUE")) {
                            ESFsList.add(ends.item(i));
                            p("\n---" + alpha + " " + beta + "---\n");
                        }
                    } catch (Exception e) {

                    }

                }
            }
//            ESFannotator();
        }
    }

    public void ESFannotator() throws IOException, SAXException, ParserConfigurationException, SQLException, TransformerException, XPathExpressionException {
        if (ESFsList.isEmpty() && !onceListFill) {
            this.ESFdetector();
            onceListFill = true;
        }
        if (!ESFsList.isEmpty()) {
            for (Node n : ESFsList) {
                Node sentNode = n.getParentNode().getParentNode();

                Node ESFs = doc.createElement("Eloquent_Simile");
                ESFs.appendChild(n);
                NodeList sentChild = sentNode.getChildNodes();
                for (int i = 1; i < sentChild.getLength(); i += 2) {
                    if (sentChild.item(i).getNodeName().equals("Eloquent_Simile")) {
                        sentChild.item(i).appendChild(n);
                    } else {
                        sentNode.appendChild(ESFs);
                    }
                }
            }
            String targetdoc = new File(rheDoc).getParent() + "\\ESFdoc.xml";
            doc.normalize();
            writeDom(doc, targetdoc);
            removeChildAll("ESF_Phrases", targetdoc);

            System.out.println("Annotated ESF document is created at " + ESFdoc);
        }
    }

    private void writeDom(Document dc, String path) throws TransformerConfigurationException, TransformerException {

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();

        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        t.setOutputProperty(OutputKeys.VERSION, "1.0");
        t.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource dom = new DOMSource(dc);

        StreamResult sr = new StreamResult(new File(path));
        t.transform(dom, sr);
    }

    private void removeChildAll(String nameNode, String filePath) throws SAXException, IOException, TransformerException {
        doc = db.parse(new File(filePath));
        NodeList remNodes = doc.getElementsByTagName(nameNode);
        for (int i = 0; i < remNodes.getLength(); i++) {
            remNodes.item(i).getParentNode().removeChild(remNodes.item(i));
        }
        doc.normalize();
        writeDom(doc, filePath);
        doc = db.parse(new File(filePath));
        NodeList nestedRem = doc.getElementsByTagName(nameNode);
        if (nestedRem.getLength() > 0) {
            removeChildAll(nameNode, filePath);
        }
    }

    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException v7f8
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws SQLException, SAXException, ParserConfigurationException, IOException {
        AWN3 awn = new AWN3();
//        String Ab = "F:\\Master\\Thesis\\Implementations\\Rhe_Processes\\Rhe_lib\\out_puts\\Alpha_Beta\\Rhe_2.xml";
//        Map attrs = awn.getAlpahBetaMap(Ab);
//        for (Object i : attrs.keySet()) {
//            
//            ArrayList ary = (ArrayList) attrs.get(i);
//
        String alpha, beta;
//        alpha= ary.get(0).toString();
//          beta = ary.get(2).toString();
        alpha = "أبوك";
        beta = "غزال";
//            awn.p("\n" + alpha);
//            awn.p("  " + beta + "\n");
//
        awn.printESFstate(awn.isESF(alpha, "", beta));
//        }

////البحث في الصفات  Adjective
    }

}
/* ملاحظات
 1- الكينونات المقارنة التي ستبنى عليها الانطلوجي هي 
 1-person
 2- instrument
 3- organization
 4- plant
 5- measure
 6- location
 7- animal
 8- group
 9- process
 10- natural_object

 */
