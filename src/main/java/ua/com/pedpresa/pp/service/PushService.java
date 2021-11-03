package ua.com.pedpresa.pp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.pedpresa.pp.domain.KeyWord;
import ua.com.pedpresa.pp.repos.KeyWordRepo;

import java.io.File;
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
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    properties.getProperty("spring.datasource.url"),
                    properties.getProperty("spring.datasource.username"),
                    properties.getProperty("spring.datasource.password"));
            PreparedStatement ps = con.prepareStatement("SELECT ID FROM wp_posts WHERE guid = ?");
            for (Map.Entry<Long, String> entry : pictMap.entrySet()) {
                System.out.println(entry.getKey() + "  " + entry.getValue());
                ps.setString(1, entry.getValue());
                ResultSet rs = ps.executeQuery();
                Long idPict = null;
                while (rs.next()) {
                    idPict = rs.getLong(1);
                }
                System.out.println(idPict);
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


}
