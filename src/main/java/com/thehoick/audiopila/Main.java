package com.thehoick.audiopila;

import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import com.thehoick.audiopila.model.Archive;
import com.thehoick.audiopila.model.ArchiveDAO;
import com.thehoick.audiopila.model.SimpleArchiveDAO;
import spark.ModelAndView;
import spark.Request;
import spark.template.jade.JadeTemplateEngine;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

public class Main {
    private static final java.lang.String FLASH_MESSAGE_KEY = "flash_message";
    private static final java.lang.String FLASH_TYPE_KEY = "flash_type";

    public static void main(String[] args) {
        staticFiles.location("/public");
        ArchiveDAO dao = new SimpleArchiveDAO();
        Map<String, Object> model = new HashMap<>();


        before((req, res) -> {
            if (req.cookie("device") != null) {
                // TODO:as add get the device from the req UserAgent somehow...
                req.attribute("device", req.cookie("device"));
            }

            model.put("title", "Audio Pila!");
            HashMap<String, String> flash = captureFlashMessage(req);
            model.put("flash", flash);
        });

        get("/", (req, res) -> new ModelAndView(model, "index"), new JadeTemplateEngine());

        post("/archives", (req, res) -> {
            Archive archive = new Archive(req.queryParams("path"));
            dao.add(archive);

            setFlashMessage(req, "Archive added.", "success");
            res.redirect("/archives");
            return null;
        });

        get("/archives", (req, res) -> {
            model.put("archives", dao.findAll());
            return new ModelAndView(model, "archives");
        }, new JadeTemplateEngine());

        enableDebugScreen();
    }


    private static void setFlashMessage(Request req, String message, String type) {
        req.session().attribute(FLASH_MESSAGE_KEY, message);
        req.session().attribute(FLASH_TYPE_KEY, type);
    }

    private static HashMap<String, String> getFlashMessage(Request req) {
        if (req.session(false) == null) {
            return null;
        }
        if (!req.session().attributes().contains(FLASH_MESSAGE_KEY)) {
            return null;
        }
        String txt = (String) req.session().attribute(FLASH_MESSAGE_KEY);
        String type = (String) req.session().attribute(FLASH_TYPE_KEY);
        HashMap<String, String> flash = new HashMap<>();
        flash.put("message", txt);
        flash.put("type", type);
        return flash;
    }

    private static HashMap<String, String> captureFlashMessage(Request req) {
        HashMap<String, String> flash = getFlashMessage(req);
        if (flash != null) {
            req.session().removeAttribute(FLASH_MESSAGE_KEY);
            req.session().removeAttribute(FLASH_TYPE_KEY);
        }
        return flash;
    }
}
