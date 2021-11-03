package ua.com.pedpresa.pp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.pedpresa.pp.domain.*;
import ua.com.pedpresa.pp.repos.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;


@Service
public class WordService {

    private static final String PUNCT = "“!\"#$&'()*+,-./:;<=>?@[\\]^_`{|}~«»";
    // excluded %

    @Autowired
    private Words1Repo words1Repo;
    @Autowired
    private Words2Repo words2Repo;
    @Autowired
    private Words3Repo words3Repo;
    @Autowired
    private Words4Repo words4Repo;
    @Autowired
    private Words5Repo words5Repo;
    @Autowired
    private KeyWordRepo keyWordRepo;

    public WordService() {
    }

    public void addSinonim(String text_old, String text_new) {

        if (!text_old.isEmpty() || !text_new.isEmpty()) {
            int titleLenth = text_old.trim().split(" ").length;

            switch (titleLenth) {
                case 1: {
                    Words1 w1 = new Words1(text_old.trim(), text_new.trim());
                    words1Repo.save(w1);
                    break;
                }
                case 2: {

                    Words2 w2 = new Words2(text_old.trim(), text_new.trim());
                    words2Repo.save(w2);
                    break;
                }
                case 3: {
                    Words3 w3 = new Words3(text_old.trim(), text_new.trim());
                    words3Repo.save(w3);
                    break;
                }
                case 4: {
                    Words4 w4 = new Words4(text_old.trim(), text_new.trim());
                    words4Repo.save(w4);
                    break;
                }
                case 5: {
                    Words5 w5 = new Words5(text_old.trim(), text_new.trim());
                    words5Repo.save(w5);
                    break;
                }
                default: {
                    System.out.println("ERROR");
                }
            }
        }
    }

    /**
     * @param title
     * @param text  !!! important (more keywords with two title & one text)
     * @return List of words with Freq >2 (minFreq)
     * Use 2title + 1text (for more weight of title)
     */
    public List<String> getWordsList(String title, String text) {
        List<String> freqList = new ArrayList<>();
        List<String> wordsList = List.of(stringNormalize(title + " " + title + " " + text).trim().split(" "));
        int minFreq = Math.max(wordsList.size() / 150, 2);

        LinkedHashMap<String, Integer> reverseSortedMap = getSortedMap(wordsList);

        for (Map.Entry<String, Integer> entry : reverseSortedMap.entrySet()) {
            if (entry.getKey().length() > 2 && entry.getValue() > minFreq) {
                freqList.add(entry.getKey() + " - " + entry.getValue());
            }
        }
        return freqList;
    }

    private LinkedHashMap<String, Integer> getSortedMap(List<String> wordsList) {
        Map<String, Integer> wordsMap = new HashMap<>();
        for (String s : wordsList) {
            String z = wordNormalize(s);
            if (wordsMap.containsKey(wordNormalize(z))) wordsMap.replace(wordNormalize(z), wordsMap.get(z) + 1);
            else wordsMap.put(z, 1);
        }
        LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();

        wordsMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));
        return reverseSortedMap;
    }

    /**
     * @param title
     * @param text
     * @return List of keywords from DB equals with text
     * May change
     */
    public List<String> getKeywords(String title, String text) {
        List<String> wordsFromRepo = new ArrayList<>();
        for (KeyWord key : keyWordRepo.findAll()) {
            wordsFromRepo.add(key.getKey_words());
        }
        List<String> freqListWithNum = getWordsList(stringNormalize(title), stringNormalize(text));
        List<String> freqList = new ArrayList<>();
        List<String> keywords = new ArrayList<>();
        for (String s : freqListWithNum) {
            freqList.add(s.substring(0, s.lastIndexOf("-") - 1));
//            System.out.println("|"+s.substring(0,s.lastIndexOf("-")-1)+"|");
        }
        int partOfword;
        for (String s : wordsFromRepo) {
            partOfword = 0;
            String[] w = s.split(" ");
            for (int i = 0; i < w.length; i++) {
//                System.out.println(w[i]);
                if (freqList.contains(wordNormalize(w[i]))) {
//                    keywords.add(s);
                    partOfword++;
//                    System.out.println(s);
                }
            }
//            System.out.println(w.length +" === "+partOfword);
            if (partOfword == w.length) {
                keywords.add(s);
            } else if (w.length > 1 && partOfword > 1 && w.length == partOfword - 1) keywords.add(s);
        }
        return keywords;
    }

    public void updateNewsSeq() {
        File file = new File("src/main/resources/application.properties");
        java.util.Properties properties = new java.util.Properties();
        try {
            properties.load(new FileReader(file));
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    properties.getProperty("spring.datasource.url"),
                    properties.getProperty("spring.datasource.username"),
                    properties.getProperty("spring.datasource.password"));
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            stmt.execute("ALTER TABLE news_mod DROP id");
            stmt.execute("ALTER TABLE news_mod AUTO_INCREMENT = 1");
            stmt.execute("ALTER TABLE news_mod ADD id int UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST");

            System.out.println("DELETED OK");

        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public static String wordNormalize(String str) {
        if (!str.isEmpty()) {
            char f = str.trim().charAt(0);
            if (PUNCT.indexOf(f) >= 0) str = str.substring(1);
            if (!str.isEmpty()) {
                char l = str.trim().charAt(str.length() - 1);
                if (PUNCT.indexOf(l) >= 0) {
                    str = str.substring(0, str.length() - 1);
                    str = wordNormalize(str);
                }
            }
        }
        return str;
    }

    public String stringNormalize(String text) {
        text
                .replace(",.", ",")
                .replace("  ", " ")
                .replace("COVID", "СOVID");
        return text;
    }

    public String getPict(String kw) {
        if (kw == null) return "";
        Iterable<KeyWord> kwAll = keyWordRepo.findAll();
        for (KeyWord keyWord : kwAll) {
            if (keyWord.getKey_words().equals(kw)) {
                return keyWord.getPict();
            }
        }
        return "";
    }
}
