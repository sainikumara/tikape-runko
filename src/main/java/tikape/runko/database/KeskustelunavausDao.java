/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Opiskelija;

/**
 *
 * @author lvikstro
 */
public class KeskustelunavausDao implements Dao<Keskustelunavaus, Integer> {

    private Database database;

    public KeskustelunavausDao(Database database) {
        this.database = database;
    }v

    @Override
    public Keskustelunavaus findOne(Integer alue) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelunavaus WHERE id = ?");
        stmt.setObject(1, alue);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
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

    @Override
    public void delete(Integer key) throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM Viesti WHERE avaus= ?");
        PreparedStatement stmt2 = connection.prepareStatement("DELETE FROM Keskustelunavaus WHERE id= ?");
        stmt.setInt(1, key);
        stmt2.setInt(1, key);
        stmt.execute();
        stmt.execute();
    }   

}
