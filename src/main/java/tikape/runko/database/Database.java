package tikape.runko.database;

import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;

public class Database {

    private String databaseAddress;

    public Database(String databaseAddress) throws ClassNotFoundException {
        this.databaseAddress = databaseAddress;
    }

    public Connection getConnection() throws SQLException {
        if (this.databaseAddress.contains("postgres")) {
            try {
                URI dbUri = new URI(databaseAddress);

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

                return DriverManager.getConnection(dbUrl, username, password);
            } catch (Throwable t) {
                System.out.println("Error: " + t.getMessage());
                t.printStackTrace();
            }
        }
        return DriverManager.getConnection(databaseAddress);
    }

    public void init() {
        List<String> lauseet = null;
        
        if (this.databaseAddress.contains("postgres")) {
            lauseet = postgreLauseet();
        } else {
            lauseet = sqliteLauseet();
        }

        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();
            System.out.println("Running command >> " + lauseet.get(0));
            st.executeUpdate(lauseet.get(0));
        } catch (Throwable t) {
            System.out.println("Error >> " + t.getMessage());
        }
        
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();
            System.out.println("Running command >> " + lauseet.get(1));
            st.executeUpdate(lauseet.get(1));
        } catch (Throwable t) {
            System.out.println("Error >> " + t.getMessage());
        }
        
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();
            System.out.println("Running command >> " + lauseet.get(2));
            st.executeUpdate(lauseet.get(2));
        } catch (Throwable t) {
            System.out.println("Error >> " + t.getMessage());
        }
    }
    
    private List<String> postgreLauseet() {
        ArrayList<String> lista = new ArrayList<>();
        
        lista.add("CREATE TABLE Keskustelualue (" 
                + "id SERIAL PRIMARY KEY NOT NULL, "
                + "aihe varchar(50) NOT NULL UNIQUE);");
        lista.add("CREATE TABLE Keskustelunavaus ("
                + "id SERIAL PRIMARY KEY NOT NULL, "
                + "alue integer NOT NULL REFERENCES Keskustelualue(id), "
                + "aika timestamp WITHOUT time zone DEFAULT (now() at time zone 'sst'), "
                + "otsikko varchar(200) NOT NULL);");
        lista.add("CREATE TABLE Viesti ("
                + "id SERIAL PRIMARY KEY, "
                + "alue integer NOT NULL REFERENCES Keskustelualue(id), "
                + "avaus integer NOT NULL REFERENCES Keskustelunavaus(id), "
                + "aika timestamp WITHOUT time zone DEFAULT (now() at time zone 'sst'), "
                + "nimimerkki varchar(20) NOT NULL, "
                + "sisalto varchar(1000) NOT NULL);");
        return lista;
    }

    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();
        // Lauseet tietokantataulujen luomiseen
        lista.add("CREATE TABLE IF NOT EXISTS Keskustelualue (" +
                    "id integer PRIMARY KEY, " +
                    "aihe varchar(50) NOT NULL UNIQUE);");
        lista.add("CREATE TABLE IF NOT EXISTS  Keskustelunavaus (" +
                    "id integer PRIMARY KEY, " + 
                    "alue integer NOT NULL, " +
                    "aika timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "otsikko varchar(200) NOT NULL, " +
                    "FOREIGN KEY(alue) REFERENCES Keskustelualue(id));");
        lista.add("CREATE TABLE IF NOT EXISTS  Viesti (" +
                    "id integer PRIMARY KEY, " +
                    "alue integer NOT NULL, " +
                    "avaus integer NOT NULL, " + 
                    "aika timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "nimimerkki varchar(20) NOT NULL, " + 
                    "sisalto varchar(1000) NOT NULL, " +
                    "FOREIGN KEY(avaus) REFERENCES Keskustelunavaus(id), " +
                    "FOREIGN KEY(alue) REFERENCES Keskustelualue(id));");
        return lista;
    }
    
    public void update(String query, Object... parameters) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement st = conn.prepareStatement(query);
            
        for (int i = 0; i < parameters.length; i++) {
            st.setObject(i + 1, parameters[i]);
        }
        
        st.execute();
        conn.close();
    }

}

