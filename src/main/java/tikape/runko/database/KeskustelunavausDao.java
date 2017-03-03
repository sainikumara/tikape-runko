
package tikape.runko.database;

import tikape.runko.domain.Keskustelunavaus;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lvikstro
 */
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

    public List<String[]> lukumaaraPerKeskustelunavaus(Integer alue) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT "
                + "Keskustelunavaus.otsikko AS avaus, "
                + "COUNT (*) AS viesteja, "
                + "MAX (Viesti.aika) AS uusin "
                + "FROM Keskustelunavaus JOIN Viesti "
                + "ON Viesti.avaus=Keskustelunavaus.id "
                + "Keskustelunavaus.alue = ?"
                + "GROUP BY Viesti.avaus ");

        stmt.setObject(1, alue);

        ResultSet rs = stmt.executeQuery();
        List<String[]> keskustelunavaukset = new ArrayList<>();

        while (rs.next()) {
            String avaus = rs.getString("avaus");
            String viesteja = rs.getString("viesteja");
            long uusin = rs.getLong("uusin");

            // timestampin luomisessa saattaa joutua kertomaan 1000:lla tai ei, riippuu, talletetaanko ms vai s
            Date timestamp = new Date(uusin * 1000);
            String uusinStr = timestamp.toString();

            String[] alueenTiedot = new String[3];
            alueenTiedot[1] = avaus;
            alueenTiedot[2] = viesteja;
            alueenTiedot[3] = uusinStr;

            keskustelunavaukset.add(alueenTiedot);

        }
        rs.close();
        stmt.close();
        connection.close();
        return keskustelunavaukset;
    }

    public void addOne(Integer key, Integer alue, Long aika, String otsikko) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Keskustelunavaus VALUES (id = ?, alue = ?, aika= ?, otsikko= ?)");

        stmt.setObject(1, key);
        stmt.setObject(2, alue);
        stmt.setObject(3, aika);
        stmt.setObject(4, otsikko);
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
    
    public List<Keskustelunavaus> findAllInTopic(Integer topicid) throws SQLException {

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

}
