package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.OpiskelijaDao;
import tikape.runko.database.KeskustelualueDao;
import tikape.runko.database.ViestiDao;
import java.util.ArrayList;
import java.util.List;


public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:foorumi.db");
        database.init();

        OpiskelijaDao opiskelijaDao = new OpiskelijaDao(database);
        KeskustelualueDao kaDao = new KeskustelualueDao(database);

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            
            List<String[]> ka = kaDao.lukumaaratPerKA();
            
            List<String> kaStr = new ArrayList<>();
            for (String[] alue : ka) {
                kaStr.add(alue[0] + "\t" + alue[1] + "\t" + alue[2] + "\t" + alue[3] + "\n");
            }
            
            map.put("keskustelualueet", kaStr);

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/opiskelijat", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("opiskelijat", opiskelijaDao.findAll());

            return new ModelAndView(map, "opiskelijat");
        }, new ThymeleafTemplateEngine());

        get("/opiskelijat/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("opiskelija", opiskelijaDao.findOne(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "opiskelija");
        }, new ThymeleafTemplateEngine());
    }
}
