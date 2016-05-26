package com.thehoick.audiopila;

import com.thehoick.audiopila.model.Archive;
import com.thehoick.audiopila.model.ArchiveDAO;
import com.thehoick.audiopila.model.SimpleArchiveDAO;
import spark.ModelAndView;
import spark.template.jade.JadeTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        staticFileLocation("/public");
        ArchiveDAO dao = new SimpleArchiveDAO();

        before((req, res) -> {
            if (req.cookie("device") != null) {
                // TODO:as add get the device from the req UserAgent somehow...
                req.attribute("device", req.cookie("device"));
            }
        });

        Map<String, String> map = new HashMap<>();
        map.put("title", "Audio Pila!");

        get("/", (req, res) -> new ModelAndView(map, "index"), new JadeTemplateEngine());

        post("/archives", (req, res) -> {
            Archive archive = new Archive(req.queryParams("path"));
            dao.add(archive);

            res.redirect("/archives");
            return null;
        });

        get("/archives", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("archives", dao.findAll());
            return new ModelAndView(model, "archives");
        }, new JadeTemplateEngine());

    }

}
