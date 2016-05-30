package com.thehoick.audiopila;

import com.sun.java.browser.dom.DOMAccessException;
import com.thehoick.audiopila.exc.DAOException;
import com.thehoick.audiopila.exc.MainError;
import com.thehoick.audiopila.model.*;
import org.sql2o.Sql2o;
import spark.ModelAndView;
import spark.Request;
import spark.template.jade.JadeTemplateEngine;

import java.net.UnknownHostException;
import java.nio.file.NotDirectoryException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

public class Main {
    private static final java.lang.String FLASH_MESSAGE_KEY = "flash_message";
    private static final java.lang.String FLASH_TYPE_KEY = "flash_type";
    private static Device device;
    private static String hostname;
    private static Sql2oSqliteDAO dao;

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
        dao = new Sql2oSqliteDAO(sql2o);

        Map<String, Object> model = new HashMap<>();

        Main.setHostname();
        if (device == null) Main.setDevice();

        // TODO:as maybe create a JSON API as well...

        before((req, res) -> {
            if (req.cookie("device") != null) {
                req.attribute("device", req.cookie("device"));
            }

            model.put("title", "Audio Pila!");
            model.put("device", device);
            HashMap<String, String> flash = captureFlashMessage(req);
            model.put("flash", flash);
        });


        // GET / (index of Audios)
        get("/", (req, res) -> new ModelAndView(model, "index"), new JadeTemplateEngine());


        // GET /archives (index of Archives)
        get("/archives", (req, res) -> {
            model.put("archives", dao.findArchives(device.getId()));
            return new ModelAndView(model, "archives");
        }, new JadeTemplateEngine());


        // POST /archives (create Archive)
        post("/archives", (req, res) -> {
            try {
                Archive archive = new Archive(req.queryParams("path"));
                archive.setDeviceId(device.getId());
                dao.addArchive(archive);
                setFlashMessage(req, "Archive added.", "success");
                res.redirect("/archives");
            } catch (NotDirectoryException e) {
                setFlashMessage(req, "Archive not added. Error: " + e.getMessage(), "alert");
                res.redirect("/archives");
            }
            return null;
        });

        // GET /archives/:id/referesh (update Audios in Archive directory)
        get("/archives/:id/refresh", (req, res) -> {
            Archive archive = dao.findArchiveById(Integer.parseInt(req.params("id")));
            archive.refreshAudios(dao);
            res.redirect("/");
            return null;
        });

        exception(MainError.class, (exc, req, res) -> {
            MainError error = (MainError) exc;
            res.status(error.getStatus());
            res.body("<html><head><title>" + error.getStatus() +
                    "</title></head><body>Sorry there was a problem. " +
                    " Status: " + error.getStatus() +
                    "</body></html>");
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
        String txt = req.session().attribute(FLASH_MESSAGE_KEY);
        String type = req.session().attribute(FLASH_TYPE_KEY);
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

    private static void setHostname() {
        try {
            hostname = java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private static void setDevice() {
        try {
            device = dao.findDeviceByName(hostname);
        } catch (DOMAccessException e) {
            e.printStackTrace();
        }

        if (device == null) {
            String osName = System.getProperty("os.name");
            Device thisDevice = new Device(hostname, osName);
            try {
                dao.addDevice(thisDevice);
            } catch (DAOException e) {
                e.printStackTrace();
            }
            device = thisDevice;
        }
    }
}
