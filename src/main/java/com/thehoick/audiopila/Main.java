package com.thehoick.audiopila;

import com.thehoick.audiopila.model.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.ModelAndView;
import spark.Request;
import spark.template.jade.JadeTemplateEngine;

import java.nio.file.NotDirectoryException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

public class Main {
    private static final java.lang.String FLASH_MESSAGE_KEY = "flash_message";
    private static final java.lang.String FLASH_TYPE_KEY = "flash_type";
    private Device device;

    public static void main(String[] args) {
        String database = "audiopila.db";
        if (args.length > 0) {
            if (args.length != 2) {
                System.out.println("java Main <port> <database>");
                System.exit(0);
            }
            port(Integer.parseInt(args[0]));
            database = args[1];
            System.out.println("port: " + args[0] + ",  database: " + database);
        }

        staticFiles.location("/public");

        String dbPath = Main.class.getClass().getResource("/db/").toString();
        String connectionString = "jdbc:sqlite:" + dbPath + database;
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        Sql2oSqliteDAO dao = new Sql2oSqliteDAO(sql2o);
        Connection con = sql2o.open();

        Map<String, Object> model = new HashMap<>();

        // TODO:as addArchive Device to database and to Archive entries as well.


        // TODO:as maybe create a JSON API as well...

        before((req, res) -> {
            if (req.cookie("device") != null) {
                // TODO:as addArchive get the device from the req UserAgent somehow...
                req.attribute("device", req.cookie("device"));
            }

            model.put("title", "Audio Pila!");
            HashMap<String, String> flash = captureFlashMessage(req);
            model.put("flash", flash);
        });


        // GET / (index of Audios)
        get("/", (req, res) -> new ModelAndView(model, "index"), new JadeTemplateEngine());


        // GET /archives (index of Archives)
        get("/archives", (req, res) -> {
            model.put("archives", dao.findArchives());
            return new ModelAndView(model, "archives");
        }, new JadeTemplateEngine());


        // POST /archives (create Archive)
        post("/archives", (req, res) -> {
            try {
                Archive archive = new Archive(req.queryParams("path"));
                dao.addArchive(archive);
                setFlashMessage(req, "Archive added.", "success");
                res.redirect("/archives");
            } catch (NotDirectoryException e) {
                setFlashMessage(req, "Archive not added. Error: " + e.getMessage(), "alert");
                res.redirect("/archives");
            }
            return null;
        });

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
