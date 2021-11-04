package ua.com.pedpresa.pp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.pedpresa.pp.domain.KeyWord;
import ua.com.pedpresa.pp.domain.PostMod;
import ua.com.pedpresa.pp.repos.KeyWordRepo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@Service
public class PushService {

    @Autowired
    private KeyWordRepo keyWordRepo;

    public void updatePictIdFromDB() {
        Map<Long, String> pictMap = new HashMap<>();
        Iterable<KeyWord> kwAll = keyWordRepo.findAll();
        for (KeyWord keyWord : kwAll) {
            if (keyWord.getId_pict() == null || keyWord.getId_pict() == 0L) {
                pictMap.put(keyWord.getId(), keyWord.getPict());
            }
        }

        File file = new File("src/main/resources/application.properties");
        java.util.Properties properties = new java.util.Properties();
        try {
            properties.load(new FileReader(file));
            Class.forName(properties.getProperty("pp.db.driver"));
            Connection con = DriverManager.getConnection(
                    properties.getProperty("pp.db.url"),
                    properties.getProperty("pp.db.username"),
                    properties.getProperty("pp.db.password"));
            PreparedStatement ps = con.prepareStatement("SELECT ID FROM wp_posts WHERE guid = ?");
            for (Map.Entry<Long, String> entry : pictMap.entrySet()) {
//                System.out.println(entry.getKey() + "  " + entry.getValue());
                ps.setString(1, entry.getValue());
                ResultSet rs = ps.executeQuery();
                Long idPict = null;
                while (rs.next()) {
                    idPict = rs.getLong(1);
                }
//                System.out.println(idPict);
                Optional<KeyWord> ikw = keyWordRepo.findById(entry.getKey());
                List<KeyWord> res = new ArrayList<>();
                ikw.ifPresent(res::add);
                if (res.size() == 1) {
                    KeyWord keyWord = res.get(0);
                    keyWord.setId_pict(idPict);
                    keyWordRepo.save(keyWord);
                }
            }
            con.close();
        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public void pushPost(PostMod pm) {
        File file = new File("src/main/resources/application.properties");
        java.util.Properties properties = new java.util.Properties();
        try {
            properties.load(new FileReader(file));
            Class.forName(properties.getProperty("pp.db.driver"));
            Connection con = DriverManager.getConnection(
                    properties.getProperty("pp.db.url"),
                    properties.getProperty("pp.db.username"),
                    properties.getProperty("pp.db.password"));
            PreparedStatement ps = con.prepareStatement("INSERT INTO wp_posts VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement psLastId = con.prepareStatement("SELECT MAX(ID) FROM wp_posts");
            ResultSet rs = psLastId.executeQuery();
            Long idPost = null;
            while (rs.next()) {
                idPost = rs.getLong(1);
            }
            idPost = idPost + 1L;
            Calendar cal = Calendar.getInstance();

            Timestamp date21 = new Timestamp(cal.getTime().getTime());       // DATE21
            cal.add(Calendar.HOUR, -3);
            Timestamp date22 = new Timestamp(cal.getTime().getTime());       // DATE22
            cal.add(Calendar.MINUTE,2);
            Timestamp date2 = new Timestamp(cal.getTime().getTime());       // DATE2
            cal.add(Calendar.HOUR, 3);
            Timestamp date1 = new Timestamp(cal.getTime().getTime());       // DATE1
            ps.setLong(1,idPost);                                   // ID
            ps.setInt(2,8);                                      // post_author
            ps.setTimestamp(3,date1);                               // post_date
            ps.setTimestamp(4,date2);                               // post_date_gmt
            ps.setString(5,pm.getText_mod_tag());                   // post_content
            ps.setString(6, pm.getTitle_mod());                     // post_title
            ps.setString(7,"");                                  // post_excerpt
            ps.setString(8,"future");                           // post_status
            ps.setString(9,"closed");                            // comment_status
            ps.setString(10,"closed");                           // ping_status
            ps.setString(11,"");                                 // post_password
            ps.setString(12,idPost.toString());                     // post_name
            ps.setString(13,"");                                 // to_ping
            ps.setString(14,"");                                 // pinged
            ps.setTimestamp(15,date21);                              // post_modified
            ps.setTimestamp(16,date22);                              // post_modified_gmt
            ps.setString(17,pm.getText_mod_tag());                  // post_content_filtered
            ps.setLong(18,0L);                                   // post_parent
            ps.setString(19,"https://pedpresa.com.ua/?p="+idPost); // guid
            ps.setInt(20,0);                                     // menu_order
            ps.setString(21,"post");                             // post_type
            ps.setString(22,"");                                 // post_mime_type
            ps.setInt(23,0);                                     // comment_count

            ps.execute();
            ps.close();
            psLastId.close();

            PreparedStatement psm = con.prepareStatement("INSERT INTO wp_postmeta VALUES (?,?,?,?)");
            PreparedStatement psLastIdm = con.prepareStatement("SELECT MAX(meta_id) FROM wp_postmeta");

            ResultSet rsm = psLastIdm.executeQuery();
            Long idPostM = null;
            while (rsm.next()) {
                idPostM = rsm.getLong(1);
            }
            psm.setLong(1,idPostM+1L);
            psm.setLong(2,idPost);
            psm.setString(3,"_wpcom_is_markdown");
            psm.setString(4,"1");
            psm.execute();

            psm.setLong(1,idPostM+2L);
            psm.setLong(2,idPost);
            psm.setString(3,"_last_editor_used_jetpack");
            psm.setString(4,"classic-editor");
            psm.execute();

            psm.setLong(1,idPostM+3L);
            psm.setLong(2,idPost);
            psm.setString(3,"_edit_last");
            psm.setString(4,"8");
            psm.execute();

            psm.setLong(1,idPostM+4L);
            psm.setLong(2,idPost);
            psm.setString(3,"_publicize_twitter_user");
            psm.setString(4,"@PEDPRESA");
            psm.execute();

            psm.setLong(1,idPostM+5L);
            psm.setLong(2,idPost);
            psm.setString(3,"rank_math_seo_score");
            psm.setString(4,"50");
            psm.execute();

            psm.setLong(1,idPostM+6L);
            psm.setLong(2,idPost);
            psm.setString(3,"rank_math_focus_keyword");
            psm.setString(4,pm.getMain_keyword());
            psm.execute();

            psm.setLong(1,idPostM+7L);
            psm.setLong(2,idPost);
            psm.setString(3,"_thumbnail_id");
            psm.setString(4,pm.getId_pict().toString());
            psm.execute();

            psm.close();
            psLastIdm.close();

            /**
             *  Second parameter for inserting is:
             * 2	Освіта
             * 3	Вища освіта
             * 4	Дошкiльна освіта
             * 5	ЗНО
             * 6	Позашкiльна освіта
             * 7	ПТО
             * 8	Середня освіта
             * 9	Офiцiйно
             * 10	Суспільство
             * 11	Вiдео
             * 12	Здоров`я
             * 13	Культура
             * 14	Наука
             * 15	Посмiхнiться
             */

            List<Long> catList = new ArrayList<>();
            catList.add(2L);
            catList.add(10L);
            catList.add(67300L);
            if(pm.getMain_keyword().equals("COVID-19")) catList.add(12L);
            if(pm.getMain_keyword().equals("МОН") || pm.getMain_keyword().equals("МОЗ") ) catList.add(9L);

            PreparedStatement pst = con.prepareStatement("INSERT INTO wp_term_relationships VALUES (?,?,?)");

            for (Long aLong : catList) {
                pst.setLong(1,idPost);
                pst.setLong(2,aLong);
                pst.setInt(3,0);
                pst.execute();
            }

            pst.close();


            System.out.println("ALL DONE");

            con.close();
        } catch (SQLException | IOException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

}

