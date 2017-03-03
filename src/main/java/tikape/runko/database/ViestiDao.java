package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Viesti;

public class ViestiDao implements Dao<Viesti, Integer> {

    private Database database;
    
    public ViestiDao(Database uusiDatabase) {
        this.database = uusiDatabase;
    }
    
    @Override
    public Viesti findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE id = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("id");
        int alue = rs.getInt("alue");
        int avaus = rs.getInt("avaus");
        int aika = rs.getInt("aika");
        String nimimerkki = rs.getString("nimimerkki");
        String sisalto = rs.getString("sisalto");
        
        Viesti v = new Viesti(id, alue, avaus, aika, nimimerkki, sisalto);

        rs.close();
        stmt.close();
        connection.close();

        return v;
    }

    @Override
    public List<Viesti> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti");

        ResultSet rs = stmt.executeQuery();
        List<Viesti> keskustelualueet = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            int alue = rs.getInt("alue");
            int avaus = rs.getInt("avaus");
            int aika = rs.getInt("aika");
            String nimimerkki = rs.getString("nimimerkki");
            String sisalto = rs.getString("sisalto");
        
            Viesti v = new Viesti(id, alue, avaus, aika, nimimerkki, sisalto);

            keskustelualueet.add(v);
        }

        rs.close();
        stmt.close();
        connection.close();

        return keskustelualueet;
    }
    
    public void addOne(Integer viestinAlue, Integer viestinAvaus, 
            String kirjoittajanNimimerkki, String viestinSisalto) throws SQLException {
        Connection connection = database.getConnection();
       PreparedStatement stmt = connection.prepareStatement(
                 "INSERT INTO Viesti (alue, avaus, aika, nimimerkki, sisalto) " +
                 "VALUES (?, ?, now, ?, ?)");
       

        stmt.setObject(1, viestinAlue);
        stmt.setObject(2, viestinAvaus);
        stmt.setObject(3, kirjoittajanNimimerkki);
        stmt.setObject(4, viestinSisalto);
        
        stmt.execute();
        
        stmt.close();
        connection.close();
    }

    @Override
    public void delete(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM Viesti WHERE id = ?");
        stmt.setObject(1, key);
        
        stmt.execute();
        
        stmt.close();
        connection.close();
    }
    
    public void delete(String nimimerkki) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM Viesti WHERE nimimerkki = ?");
        stmt.setObject(1, nimimerkki);
        
        stmt.execute();
        
        stmt.close();
        connection.close();
    }
    
    public List<Viesti> findAllTopic(String topicId) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE alue = ?");
        stmt.setObject(1, topicId);
        ResultSet rs = stmt.executeQuery();
       
        List<Viesti> keskustelualueet = new ArrayList<>();
        
        while (rs.next()) {
            Integer id = rs.getInt("id");
            int alue = rs.getInt("alue");
            int avaus = rs.getInt("avaus");
            int aika = rs.getInt("aika");
            String nimimerkki = rs.getString("nimimerkki");
            String sisalto = rs.getString("sisalto");
        
            Viesti v = new Viesti(id, alue, avaus, aika, nimimerkki, sisalto);

            keskustelualueet.add(v);
        }

        rs.close();
        stmt.close();
        connection.close();

        return keskustelualueet;
    }
    
    public List<Viesti> findAllThread(int threadid) throws SQLException {
        Connection connection = database.getConnection();
        
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti WHERE avaus = ?");
        stmt.setObject(1, threadid);
        ResultSet rs = stmt.executeQuery();
         
        List<Viesti> viestit = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            int alue = rs.getInt("alue");
            int avaus = rs.getInt("avaus");
            int aika = rs.getInt("aika");
            String nimimerkki = rs.getString("nimimerkki");
            String sisalto = rs.getString("sisalto");
        
            Viesti v = new Viesti(id, alue, avaus, aika, nimimerkki, sisalto);
 
            viestit.add(v);
        }

        rs.close();
        connection.close();

        return viestit;
    }
}
