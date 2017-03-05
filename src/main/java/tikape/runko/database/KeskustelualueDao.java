package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import tikape.runko.domain.Keskustelualue;

public class KeskustelualueDao implements Dao<Keskustelualue, Integer> {
    
    private Database database;

    public KeskustelualueDao(Database uusiDatabase) {
        this.database = uusiDatabase;
    }

    @Override
    public Keskustelualue findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelualue WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        String aihe = rs.getString("aihe");

        Keskustelualue ka = new Keskustelualue(id, aihe);

        rs.close();
        stmt.close();
        connection.close();

        return ka;
    }

    @Override
    public List<Keskustelualue> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelualue");

        ResultSet rs = stmt.executeQuery();
        List<Keskustelualue> keskustelualueet = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String aihe = rs.getString("aihe");

            keskustelualueet.add(new Keskustelualue(id, aihe));
        }

        rs.close();
        stmt.close();
        connection.close();

        return keskustelualueet;
    }
    
    public Integer addOne(String aihe) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Keskustelualue (aihe) VALUES (?)");
        
        stmt.setString(1, aihe);
        
        stmt.execute();
        
        ResultSet rs = stmt.getGeneratedKeys();
        int alueenId = rs.getInt(1);
        
        stmt.close();
        connection.close();
        
        return alueenId;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt1 = connection.prepareStatement("DELETE FROM Viesti WHERE alue = ?");
        PreparedStatement stmt2 = connection.prepareStatement("DELETE FROM Keskustelunavaus WHERE alue = ?");
        PreparedStatement stmt3 = connection.prepareStatement("DELETE FROM Keskustelualue WHERE alue = ?");
        
        stmt1.setObject(1, key);
        stmt2.setObject(1, key);
        stmt3.setObject(1, key);
        
        stmt1.execute();
        stmt2.execute();
        stmt3.execute();
        
        stmt1.close();
        stmt2.close();
        stmt3.close();
        connection.close();
    }
    
    // Tämän metodin avulla saadaan etusivulle keskustelualueittain avauksien ja
    // viestien lukumäärät ja uusimpien viestien lähetysajankohdat
    public List<List> lukumaaratPerKA() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT DISTINCT ON (Keskustelualue.id) "
                + "Keskustelualue.id AS id, "
                + "Keskustelualue.aihe AS aihe, "
                + "COUNT (DISTINCT Viesti.avaus) AS avauksia, "
                + "COUNT (*) AS viesteja, "
                + "MAX (Viesti.aika) AS uusin "
                + "FROM Keskustelualue JOIN Viesti "
                + "ON Keskustelualue.id = Viesti.alue "
                + "ORDER BY uusin");
        
        ResultSet rs = stmt.executeQuery();
        List<List> keskustelualueet = new ArrayList<>();
        
        while (rs.next()) {
            String id = rs.getString("id");
            String aihe = rs.getString("aihe");
            String avauksia = rs.getString("avauksia");
            String viesteja = rs.getString("viesteja");
            long uusin = rs.getLong("uusin");
            
            // timestampin luomisessa saattaa joutua kertomaan 1000:lla tai ei, riippuu, talletetaanko ms vai s
            Date timestamp = new Date(uusin * 1000);
            String uusinStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
            
            List<String> kaTiedot = new ArrayList<>();
            kaTiedot.add(id);
            kaTiedot.add(aihe);
            kaTiedot.add(avauksia);
            kaTiedot.add(viesteja);
            kaTiedot.add(uusinStr);
            
            keskustelualueet.add(kaTiedot);
        }

        rs.close();
        stmt.close();
        connection.close();
        
        return keskustelualueet;
    }
    
    public Integer getIdByTopic(String topic) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT id FROM Keskustelualue WHERE aihe = ?");
        stmt.setObject(1, topic);
        ResultSet rs = stmt.executeQuery();
        
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }
        
        int id = rs.getInt("id");
        
        rs.close();
        stmt.close();
        connection.close();
        
        return id;
    }
}
