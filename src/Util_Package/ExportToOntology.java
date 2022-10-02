package Util_Package;

import awn3.AWN3;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JOptionPane;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;

public class ExportToOntology extends AWN3 {

    ArrayList<String> store;
    OntModel model;
    OntClass root;
    String NS;
    int i;

    public ExportToOntology() {
        i = 0;
    }

    void writeOnto(OntModel m) throws IOException {
        String path = "./Shom_lib2/awnOnt.owl";
        FileWriter fw = new FileWriter(path);
        m.write(fw, "RDF/XML");
        fw.close();
        System.out.println("---------------have done----------------");
    }

    void buildStart() throws IOException {
        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        NS = "http://alshmowkh.org/2017/0/awnOnto#";
        model.setNsPrefix("rhe", NS);
        root = model.createClass(NS + "كلمة");

    }

    void buildLevel(ArrayList<String> level, OntClass parent) throws SQLException {
        i++;
//                        JOptionPane.showMessageDialog(null,parent);

//        if (i < 50) {
        for (String n : level) {
            String namenode = aws.getArabicTerm(n).trim().replaceAll(" ", "_");
            OntClass cls = model.createClass(NS + namenode);
            parent.addSubClass(cls);
        }
        for (String sub : level) {
            OntClass subParent = model.getOntClass(NS + aws.getArabicTerm(sub).trim().replaceAll(" ", "_"));

            ArrayList<String> hypo = adb.getArabicHyponyms(sub);
            if (hypo.size() > 0) {
                buildLevel(hypo, subParent);
            }
        }
//        }
    }

    void buildLevel2(ArrayList<String> level, OntClass parent) throws SQLException {
//        i++;
//        if (i < 50) {
        for (String n : level) {
            String namenode = aws.getArabicTerm(n).trim().replaceAll(" ", "_");
            OntClass cls = model.createClass(NS + namenode);
            parent.addSubClass(cls);
            ArrayList<String> hypos = adb.getArabicHyponyms(n);
            if (hypos.size() > 0) {
                buildLevel2(hypos, cls);
            }
//            }
        }
    }

    void buildPath(String synsetid, OntClass parent) throws SQLException {

        String namenode = aws.getArabicTerm(synsetid).replaceAll(" ", "_");
        OntClass cls = model.createClass(NS + namenode);
        parent.addSubClass(cls);
        ArrayList<String> hypo = adb.getArabicHyponyms(synsetid);
        if (hypo.size() > 0) {
            buildPath(hypo.get(0), cls);
        }
    }

    ArrayList<String> recursionTree(ArrayList<String> synsetlist) throws SQLException {

        ArrayList<String> local;
        for (String syn : synsetlist) {
//            System.out.println(syn);
            local = adb.getArabicHyponyms(syn);
//            store.addAll(local);
            if (local.size() > 0) {
                store.addAll(recursionTree(local));
            }
        }

        return store;
    }

    void test() throws SQLException, IOException {
        this.buildStart();
//        this.buildPath(aws.getFirstLevelNouns().get(0), root);

        this.buildLevel2(aws.getFirstLevelNouns(), root);
        this.writeOnto(model);
    }

    public static void main(String[] args) throws SQLException, IOException {
        ExportToOntology cls = new ExportToOntology();
        cls.test();
    }

    void p(Object o) {
        System.out.println(o);
    }

    void p(ArrayList list) throws SQLException {
        for (Object l : list) {
            System.out.println(l);
        }
    }
}
