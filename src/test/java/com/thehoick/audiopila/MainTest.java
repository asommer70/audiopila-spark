package com.thehoick.audiopila;

import com.thehoick.audiopila.testing.MainClient;
import com.thehoick.audiopila.testing.MainResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.Spark;

import static org.junit.Assert.*;

public class MainTest {

    private static final String PORT = "4568";
    private static final String DATABASE = "test.db";
    private Connection con;
    private MainClient client;

    @BeforeClass
    public static void startServer() {
        String[] args = {PORT, DATABASE};
        Main server = new Main();
        server.main(args);
    }

    @AfterClass
    public static void stopServer() {
        Spark.stop();
    }

    @Before
    public void setUp() throws Exception {
        String dbPath = this.getClass().getResource("/db/").toString();
        String connectionString = "jdbc:sqlite:" + dbPath + DATABASE;
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        con = sql2o.open();

        client = new MainClient("http://localhost:" + PORT);
    }

    @After
    public void tearDown() throws Exception {
        con.close();
    }

    @Test
    public void archivesReturnsArchivesIndexPage() throws Exception {
        MainResponse res = client.request("GET", "/archives", null);
        assertEquals(200, res.getStatus());
    }

    @Test
    public void archiveIsListedAfterCreation() throws Exception {
        MainResponse res = client.request("POST", "/archives", "path=/Users/adam/Music");
        assertEquals(200, res.getStatus());

        Document doc = Jsoup.parse(res.getBody());

        Elements strongs = doc.select("strong");
        assertEquals(strongs.get(0).text(), "adam Music");
    }
}