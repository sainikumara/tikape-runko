package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.Database;
import tikape.runko.database.KeskustelualueDao;
import tikape.runko.database.ViestiDao;
import tikape.runko.database.KeskustelunavausDao;
import tikape.runko.domain.Keskustelualue;
import tikape.runko.domain.Viesti;
import java.util.ArrayList;
import java.util.List;
import spark.Spark;
import tikape.runko.domain.Keskustelunavaus;

public class Main {

    public static void main(String[] args) throws Exception {
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }
        String dbosoite = "jdbc:sqlite:foorumi.db";

        if (System.getenv("DATABASE_URL") != null) {
            dbosoite = System.getenv("DATABASE_URL");
        }

        Database database = new Database(dbosoite);
        database.init();

        Spark.staticFileLocation("/templates");

        KeskustelualueDao kaDao = new KeskustelualueDao(database);
        KeskustelunavausDao avausDao = new KeskustelunavausDao(database);
        ViestiDao vd = new ViestiDao(database);

        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            List<List> ka = kaDao.lukumaaratPerKA();

            map.put("keskustelualueet", ka);

            if (req.queryParams("longmsg") == null) {
                map.put("longmsg", false);
            } else {
                map.put("longmsg", true);
            }
            
            if (req.queryParams("warn") == null
                    || req.queryParams("warn").equals("false")) {
                map.put("warn", false);
            } else if (req.queryParams("warn").equals("true")) {
                map.put("warn", true);
            }

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        get("/topic/:id", (req, res) -> {
            HashMap map = new HashMap<>();

            
            if (req.queryParams("longmsg") == null) {
                map.put("longmsg", false);
            } else {
                map.put("longmsg", true);
            }
            
            Keskustelualue alue = kaDao.findOne(Integer.parseInt(req.params(":id")));
            String aihe = alue.getAihe();

            List<List> avaukset
                    = avausDao.lukumaaraPerKeskustelunavaus(Integer.parseInt(req.params(":id")), 1);

            map.put("alueId", alue.getId());
            map.put("aihe", aihe);
            map.put("threads", avaukset);

            return new ModelAndView(map, "topic");
        }, new ThymeleafTemplateEngine());

        get("/thread/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            int avauksenId = Integer.parseInt(req.params(":id"));
            Keskustelunavaus avaus = avausDao.findOne(avauksenId);
            int alueenId = avaus.getAlue();

            Keskustelualue aktiivinenAlue = kaDao.findOne(alueenId);
            String aihe = aktiivinenAlue.getAihe();
            String otsikko = avaus.getOtsikko();

            int sivunumero = 1;
            try {
                sivunumero = Integer.parseInt(req.queryParams("sivu"));
            } catch (Throwable t) {
                sivunumero = 1;
            }
            int edellinenSivu = sivunumero - 1;
            if (edellinenSivu < 1) {
                edellinenSivu = 1;
            }
            int seuraavaSivu = sivunumero + 1;

            map.put("sivu", sivunumero);
            map.put("edellinen", edellinenSivu);
            map.put("seuraava", seuraavaSivu);
            map.put("alueenId", alueenId);
            map.put("aihe", aihe);
            map.put("avauksenId", avauksenId);
            map.put("otsikko", otsikko);
            
            if (req.queryParams("longmsg") == null) {
                map.put("longmsg", false);
            } else {
                map.put("longmsg", true);
            }

            List<List> viestit = vd.findAllInThread(avauksenId, sivunumero);

            if (viestit.isEmpty()) {
                res.redirect("/thread/" + avauksenId + "?sivu=" + edellinenSivu);
            }

            map.put("viestit", viestit);
            return new ModelAndView(map, "thread");
        }, new ThymeleafTemplateEngine());

        post("/msg", (req, res) -> {
            if (req.queryParams("message").length() > 1000) {
                res.redirect("/thread/" + req.queryParams("avaus") + "?sivu=" 
                        + req.queryParams("sivu") + "&longmsg=true");
                return "";
            }
            
            vd.addOne(Integer.parseInt(req.queryParams("alue")),
                    Integer.parseInt(req.queryParams("avaus")),
                    req.queryParams("name"),
                    req.queryParams("message"));
            res.redirect("/thread/" + req.queryParams("avaus") + "?sivu=" + req.queryParams("sivu"));
            return "";
        });

        post("/uusialue", (req, res) -> {
            if (req.queryParams("aloitus").length() > 1000) {
                res.redirect("/?longmsg=true");
                return "";
            }
            
            try {
                int alueenId = kaDao.addOne(req.queryParams("topic"));
                int avauksenId = avausDao.addOne(alueenId, "Alueen kuvaus");
                vd.addOne(alueenId, avauksenId,
                        req.queryParams("name"), req.queryParams("aloitus"));
            } catch (Throwable t) {
                t.printStackTrace();
                res.redirect("/?warn=true");
                return "";
            }

            res.redirect("/?warn=false");
            return "";
        });

        post("topic/uusiavaus", (req, res) -> {
            int alueid = Integer.parseInt(req.queryParams("alueId"));
            if (req.queryParams("msg").length() > 1000) {
                res.redirect("/topic/" + Integer.toString(alueid) + "?longmsg=true");
                return "";
            }
            int avausid = avausDao.addOne(alueid, req.queryParams("title"));
            vd.addOne(alueid, avausid,
                    req.queryParams("name"), req.queryParams("msg"));
            res.redirect("/thread/" + Integer.toString(avausid) + "?sivu=1");
            return "";
        });

    }
}
