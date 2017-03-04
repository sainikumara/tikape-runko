
package tikape.runko.database;

import tikape.runko.domain.Keskustelunavaus;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class KeskustelunavausDao implements Dao<Keskustelunavaus, Integer> {

    private Database database;

    public KeskustelunavausDao(Database database) {
        this.database = database;
    }

    @Override
    public Keskustelunavaus findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelunavaus WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        Integer alue = rs.getInt("alue");
        Integer aika = rs.getInt("aika");
        String otsikko = rs.getString("otsikko");

        Keskustelunavaus k = new Keskustelunavaus(id, alue, aika, otsikko);

        rs.close();
        stmt.close();
        connection.close();

        return k;
    }

    @Override
    public List<Keskustelunavaus> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelunavaus");

        ResultSet rs = stmt.executeQuery();
        List<Keskustelunavaus> keskustelunavaukset = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            Integer alue = rs.getInt("alue");
            Integer aika = rs.getInt("aika");
            String otsikko = rs.getString("otsikko");

            keskustelunavaukset.add(new Keskustelunavaus(id, alue, aika, otsikko));
        }

        rs.close();
        stmt.close();
        connection.close();

        return keskustelunavaukset;
    }

    public List<List> lukumaaraPerKeskustelunavaus(Integer alue) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT "
                + "Keskustelunavaus.id AS id, "
                + "Keskustelunavaus.otsikko AS avaus, "
                + "COUNT (*) AS viesteja, "
                + "MAX (Viesti.aika) AS uusin "
                + "FROM Keskustelunavaus JOIN Viesti "
                + "ON Viesti.avaus=Keskustelunavaus.id "
                + "AND Keskustelunavaus.alue = ? "
                + "GROUP BY Viesti.avaus");

        stmt.setObject(1, alue);

        ResultSet rs = stmt.executeQuery();

        List<List> keskustelunavaukset = new ArrayList<>();

        while (rs.next()) {
            String id = rs.getString("id");
            String avaus = rs.getString("avaus");
            String viesteja = rs.getString("viesteja");
            long uusin = rs.getLong("uusin");

            // timestampin luomisessa saattaa joutua kertomaan 1000:lla tai ei, riippuu, talletetaanko ms vai s
            Date timestamp = new Date(uusin * 1000);
            String uusinStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);

            List<String> tiedot = new ArrayList<>();
            
            tiedot.add(id);
            tiedot.add(avaus);
            tiedot.add(viesteja);
            tiedot.add(uusinStr);

            keskustelunavaukset.add(tiedot);

        }
        rs.close();

        stmt.close();

        connection.close();
        return keskustelunavaukset;
    }

    public void addOne(Integer alue, String otsikko) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO Keskustelunavaus (alue, otsikko, aika) VALUES (?, ?, ?)");
        
        Timestamp currentTime = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()/1000);

        stmt.setObject(1, alue);
        stmt.setObject(2, otsikko);
        stmt.setTimestamp(3, currentTime);
        stmt.execute();

        stmt.close();
        connection.close();
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM Viesti WHERE avaus = ?");
        PreparedStatement stmt2 = connection.prepareStatement("DELETE FROM Keskustelunavaus WHERE id = ?");
        stmt.setInt(1, key);
        stmt2.setInt(1, key);
        stmt.execute();
        stmt.execute();
    }   
    
    public List<Keskustelunavaus> findAllInTopic(String topicid) throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM Keskustelunavaus WHERE alue = ?");
        stmt.setObject(1, topicid);

        ResultSet rs = stmt.executeQuery();
        List<Keskustelunavaus> keskustelunavaukset = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            Integer alue = rs.getInt("alue");
            Integer aika = rs.getInt("aika");
            String otsikko = rs.getString("otsikko");

            keskustelunavaukset.add(new Keskustelunavaus(id, alue, aika, otsikko));
        }

        rs.close();
        stmt.close();
        connection.close();

        return keskustelunavaukset;
    }
    
    public Integer getIdByTitle(String title) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM Keskustelunavaus WHERE otsikko = ?");
        stmt.setString(1, title);
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
