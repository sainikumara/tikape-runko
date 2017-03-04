package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.OpiskelijaDao;
import tikape.runko.database.KeskustelualueDao;
import tikape.runko.database.ViestiDao;
import tikape.runko.database.KeskustelunavausDao;
import tikape.runko.domain.Keskustelualue;
import tikape.runko.domain.Viesti;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Keskustelunavaus;

public class Main {

    public static void main(String[] args) throws Exception {
        Database database = new Database("jdbc:sqlite:foorumi.db");
        database.init();

        OpiskelijaDao opiskelijaDao = new OpiskelijaDao(database);
        KeskustelualueDao kaDao = new KeskustelualueDao(database);
        KeskustelunavausDao avausDao = new KeskustelunavausDao(database);
        ViestiDao vd = new ViestiDao(database);

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            List<List> ka = kaDao.lukumaaratPerKA();

            map.put("keskustelualueet", ka);

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/topic/:id", (req, res) -> {
            HashMap map = new HashMap<>();

            Keskustelualue alue = kaDao.findOne(Integer.parseInt(req.params(":id")));
            String aihe = alue.getAihe();
            map.put("aihe", aihe);

            List<List> avaukset
                    = avausDao.lukumaaraPerKeskustelunavaus(Integer.parseInt(req.params(":id")));
            map.put("threads", avaukset);

            return new ModelAndView(map, "topic");
        }, new ThymeleafTemplateEngine());

        get("/thread/:id", (req, res) -> {
            HashMap map = new HashMap<>();

            List<List> viestit = vd.findAllInThread(Integer.parseInt(req.params(":id")));
            List<String> viestinTiedot = viestit.get(0);
            int alueenId = Integer.parseInt(viestinTiedot.get(1));
            int avauksenId = Integer.parseInt(viestinTiedot.get(2));
            
            Keskustelualue aktiivinenAlue = kaDao.findOne(alueenId);
            String aihe = aktiivinenAlue.getAihe();
            String aiheid = Integer.toString(aktiivinenAlue.getId());
            Keskustelunavaus alue = avausDao.findOne(avauksenId);
            String otsikko = alue.getOtsikko();
            
            map.put("alueenId", alueenId);
            map.put("aihe", aihe);
            map.put("avauksenId", avauksenId);
            map.put("otsikko", otsikko);
            map.put("viestit", viestit);
            return new ModelAndView(map, "thread");
        }, new ThymeleafTemplateEngine());

        post("/msg", (req, res) -> {
            vd.addOne(Integer.parseInt(req.queryParams("alue")),
                    Integer.parseInt(req.queryParams("avaus")),
                    req.queryParams("name"),
                    req.queryParams("message"));
            res.redirect("/thread/" + req.queryParams("avaus"));
            return "";
        });
    }
}
