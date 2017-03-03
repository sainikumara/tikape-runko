package tikape.runko;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import tikape.runko.database.Database;
import tikape.runko.database.KeskustelualueDao;
import tikape.runko.database.KeskustelunavausDao;
import tikape.runko.database.ViestiDao;
import tikape.runko.domain.Keskustelualue;
import tikape.runko.domain.Keskustelunavaus;
import tikape.runko.domain.Viesti;

public class TestausUI {
    
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        

        System.out.println("Hello. Please give a name for the database file. " +

                "Include .db");
        String filename = sc.nextLine();
        
        Database database = new Database("jdbc:sqlite:" + filename);
        database.init();

        KeskustelualueDao alueDao = new KeskustelualueDao(database);
        ViestiDao viestiDao = new ViestiDao(database);
        KeskustelunavausDao avausDao = new KeskustelunavausDao(database);
        
        
        while(true) {
            System.out.println("Do you want to modify the database or test the forum?");
            System.out.println("mod\tmodify\ntest\ttest forum\nexit\tquit application");
            String modOrTest = sc.nextLine();
        
            if (modOrTest.equals("exit")) {
                break;
            } else if (!(modOrTest.equals("mod") || modOrTest.equals("test"))) {
                System.out.println("Unknown command.");
            } else if (modOrTest.equals("mod")) {
                enterModifyState(database, sc);
            } else if (modOrTest.equals("test")) {
                enterTestState(database, sc, alueDao, avausDao, viestiDao);
            }
        }
        System.out.println("Bye.");
    }
    
    private static void enterModifyState(Database db, Scanner sc) throws SQLException {
        System.out.println("Entering database modification.\n");
        System.out.println("q\tquery\nu\tupdate\ns\tlist schemas\nl\tlist all tables\n" +
                "t\tlist contents of a single table\nexit\texit mod state\n" +
                "help\tshow this list\n");
        while(true) {
            System.out.println("Enter desired command.");
            String mode = sc.nextLine();
            
            if (mode.equals("exit")) {
                System.out.println("Quitting database modification.");
                break;
            } else if (mode.equals("help")) {
                System.out.println("q\tquery\nu\tupdate\ns\tlist schemas\nl\tlist all tables\n" +
                    "t\tlist contents of a single table\nexit\texit mod state\n" +
                    "help\tshow this list\n");
            } else if (mode.equals("l")) {
                Connection con = db.getConnection();
                
                ResultSet rs = con.createStatement().executeQuery(
                        "SELECT name FROM sqlite_master WHERE type='table'");
       
                while(rs.next()) {
                    System.out.println(rs.getString("name"));
                }
                System.out.println("");
                con.close();
                
            } else if (mode.equals("t")) {
                System.out.println("Enter name of table:");
                String table = sc.nextLine();
                Connection con = db.getConnection();
                
                
                try {
                    ResultSet rs = con.createStatement().executeQuery(
                            "SELECT * FROM " + table);
                    
                    while(rs.next()) {
                        for (int i = 1;;i++) {
                            try {
                                String s = rs.getString(i);
                                System.out.print(s + "\t");
                            } catch (Throwable t){
                                System.out.println("");
                                break;
                            }
                        }
                    }                
                } catch (Throwable t) {
                    System.out.println("Couldn't list table contents.");
                }
                
                con.close();
            } else if (mode.equals("q")) {
                System.out.println("Enter statement:");
                String statement = sc.nextLine();
                
                Connection con = db.getConnection();

                ResultSet rs = con.createStatement().executeQuery(statement);
        
                while(rs.next()) {
                    for (int i = 1;;i++) {
                        try {
                            String s = rs.getString(i);
                            System.out.print(s + "\t");
                        } catch (Throwable t){
                            System.out.println("");
                            break;
                        }
                    }
                }
                con.close();
            } else if (mode.equals("u")) {
                System.out.println("Enter statement:");
                String statement = sc.nextLine();
                try {
                    db.update(statement);
                } catch (Throwable t) {}
            } else if (mode.equals("s")) {
                Connection con = db.getConnection();
                ResultSet rs = con.createStatement().executeQuery(
                        "SELECT name FROM sqlite_master WHERE type='table'");
       
                while(rs.next()) {
                    String table = rs.getString("name");
                    ResultSet rs2 = con.createStatement().executeQuery(
                            "PRAGMA TABLE_INFO(" + table + ")");
                    System.out.println(table);
                    for (int j = 0; j < table.length(); j++) {
                        System.out.print("-");
                    }
                    System.out.println("");
                    while(rs2.next()) {
                        for (int i = 2; i <= 3; i++) {
                            System.out.print(rs2.getString(i) + " ");
                        }
                        System.out.println("");
                    }
                    System.out.println("");
                }
                System.out.println("");
                con.close();
                
            } else {
                System.out.println("Unknown command.");
                System.out.println("q\tquery\nu\tupdate\ns\tlist schemas\nl\tlist all tables\n" +
                    "t\tlist contents of a single table\nexit\texit program\n" +
                    "help\tshow this list\n");
            }
        }
    }
    
    private static void enterTestState(Database db, Scanner sc,
            KeskustelualueDao ad, KeskustelunavausDao kd, ViestiDao vd) throws SQLException {
        System.out.println("This is a testing UI for the forum\nAll topics:");
        List<String> topics = printAndGetTopics(ad);
        while(true) {
            System.out.println("Submit topic to enter or 'exit' to leave.");
            String command = sc.nextLine();
            
            if (command.equals("exit")) {
                break;
            } else if (topics.contains(command)) {
                enterTopic(sc, ad.getIdByTopic(command), kd, vd);
            } else {
                System.out.println("Unknown command.");
            }
        }
        return;
    }
    
    private static List<String> printAndGetTopics(KeskustelualueDao ad) throws SQLException {
        List<String> names = new ArrayList<>();
        for (String[] info : ad.lukumaaratPerKA()) {
            System.out.format("%s\tthreads: %s\tmessages: %s\tlast: %s\n",
                    info[0], info[1], info[2], info[3]);
            names.add(info[0]);
        }
        return names;
    }
    
    private static void enterTopic(Scanner sc, Integer topic, KeskustelunavausDao kd,
            ViestiDao vd) throws SQLException{
        System.out.println("\nAll threads for this topic.");
        List<Integer> threadids = printAndGetThreads(kd);
        int id = -1;
        while(true) {
            System.out.println("Enter thread ID to view or 'list' to list threads");
            String command = sc.nextLine();
            if (command.equals("list")) {
                printAndGetThreads(kd);
                continue;
            }
            try {
                id = Integer.parseInt(command);
            } catch (Throwable t) {
                System.out.println("Not a valid number.");
                continue;
            }
            
            if (threadids.contains(id)) {
                enterThread(sc, vd, id, topic);
            } else {
                System.out.println("Not a valid thread id.");
                continue;
            }
        }
    }
    
    private static List<Integer> printAndGetThreads(KeskustelunavausDao kd) throws SQLException{
        List<Keskustelunavaus> threads = kd.findAll();
        List<Integer> threadids = new ArrayList<>();
        for (Keskustelunavaus ka : threads) {
            System.out.format("id: %s\totsikko: %s\t%s\n",
                    ka.getId(), ka.getOtsikko(), ka.getAika());
            threadids.add(ka.getId());
        }
        return threadids;
    }
    
    private static void enterThread(Scanner sc, ViestiDao vd, int threadid,
            int topicid) throws SQLException {
        System.out.println("All messages in this thread\n");
        List<Viesti> messages = vd.findAllThread(threadid);
        for (Viesti m : messages) {
            System.out.format("%s\t%s\n%s\n\n",
                    m.getNimimerkki(), m.getAika(), m.getSisalto());
        }
        while(true) {
            System.out.println("Enter command\nnm\tnew message\nlist\tlist messages\n" +
                    "back\tback to threads");
            String command = sc.nextLine();
            if (command.equals("nm")) {
                createMessage(sc, threadid, topicid, vd);
            } else if (command.equals("back")) {
                return;
            } else if (command.equals("list")) {
                messages = vd.findAllThread(threadid);
                for (Viesti m : messages) {
                    System.out.format("%s\t%s\n%s\n\n",
                    m.getNimimerkki(), m.getAika(), m.getSisalto());
                }
            } else {
                System.out.println("Unknown command.");
            }
        }
        
    }
    
    private static void createMessage(Scanner sc, int threadid,
            int topicid, ViestiDao vd) throws SQLException {
        System.out.println("Enter name:");
        String name = sc.nextLine();
        System.out.println("Enter message (max 1000 characters):");
        String message = sc.nextLine();
        vd.addOne(topicid, threadid, name, message);
        System.out.println("Message sent.\n");
    }
    
}
