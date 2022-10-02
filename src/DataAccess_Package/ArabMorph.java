/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataAccess_Package;

import gpl.pierrick.brihaye.aramorph.AraMorph;
import gpl.pierrick.brihaye.aramorph.Solution;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class ArabMorph {

    AraMorph aMorph;

    public ArabMorph() {
        aMorph = new AraMorph();
    }

    private HashSet getWordSolutions(String word) {
        return aMorph.getWordSolutions(word);
    }

    public ArrayList<String> getWordMorphs(String input) {
        ArrayList<String> morphs = new ArrayList();
        aMorph.analyzeToken(input);
        HashSet hset = aMorph.getWordSolutions(input);
        Iterator itr = hset.iterator();
        while (itr.hasNext()) {
            Solution sol = (Solution) itr.next();
            String lemma = sol.getLemma();
            String pos = sol.getStemPOS();
            if (pos.startsWith("VERB")) {
                lemma = lemma + "a";
            }
            morphs.add(lemma);
        }
        return morphs;
    }

    public ArrayList<String> getNounsStems(String input) {
        ArrayList<String> morphs = new ArrayList();
        aMorph.analyzeToken(input);
        if (input.trim().isEmpty()) {
            return morphs;
        }
        HashSet hset = aMorph.getWordSolutions(input);
        Iterator itr = hset.iterator();
        while (itr.hasNext()) {
            Solution sol = (Solution) itr.next();
            String lemma = sol.getLemma();
            String pos = sol.getStemPOS();
            if (!pos.startsWith("VERB")) {
                morphs.add(lemma);
            }
        }
        return morphs;
    }

    public String getStem(String word) {
        if (word.trim().isEmpty()) {
            return word;
        }
        try {
            aMorph.analyzeToken(word);
            HashSet hset = aMorph.getWordSolutions(word);
            if (!hset.isEmpty()) {
                return ((Solution) hset.iterator().next()).getLemma();
            }

        } catch (Exception e) {

        }
        return word;
    }

    void test() {
        String stem=this.getStem("ٱِسْتِهْداف");
        System.out.println(stem);
        System.out.println(this.getNounsStems("أحمد"));

    }

    public static void main(String[] args) {
        ArabMorph cls = new ArabMorph();
        cls.test();
    }

}
