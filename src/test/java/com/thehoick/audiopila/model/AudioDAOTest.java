package com.thehoick.audiopila.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.xml.crypto.dsig.spec.ExcC14NParameterSpec;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AudioDAOTest {
    private SqliteDAO dao;
    private Connection con;
    private Device device;
    private Archive archive;
    private Audio audio;

    @Before
    public void setUp() throws Exception {
        String connectionString = migrate();
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        dao = new Sql2oSqliteDAO(sql2o);
        con = sql2o.open();

        String hostname = java.net.InetAddress.getLocalHost().getHostName();
        String osName = System.getProperty("os.name");
        device = new Device(hostname, osName);
        dao.addDevice(device);

        archive = new Archive("/Users/adam/Music");
        archive.setDeviceId(device.getId());
        dao.addArchive(archive);

        audio =  new Audio("/Users/adam/Music/taco.mp3", archive.getId());
    }

    private String migrate() {
        // Uncomment to perform migration.
        // May have to delete database first to avoide checksum mismatch...
//        Flyway flyway = new Flyway();
//        flyway.setDataSource(connectionString, null, null);
//        flyway.migrate();

        String dbPath = this.getClass().getResource("/db/").toString();

        // Live database.
//      return "jdbc:sqlite:" + dbPath + "audiopila.db";
        // Test database.
        return "jdbc:sqlite:" + dbPath + "test.db";
    }

    @After
    public void tearDown() throws Exception {
        // Clear the data manually.
        con.createQuery("delete from devices;").executeUpdate();
        con.createQuery("delete from archives;").executeUpdate();
        con.createQuery("delete from audios;").executeUpdate();
        con.createQuery("delete from sqlite_sequence;").executeUpdate();
        con.close();
    }

    @Test
    public void addingAudioSetsId() throws Exception {
        int originalId = audio.getId();

        dao.addAudio(audio);

        assertNotEquals(originalId, audio.getId());
    }

    @Test
    public void canFindAudioById() throws Exception {
        dao.addAudio(audio);

        Audio dbAudio = dao.findAudioById(audio.getId());

        assertEquals(audio, dbAudio);
    }

    @Test
    public void listAudiosInArchiveReturnsListOfAudios() throws Exception {
        dao.addAudio(audio);
        Audio audio2 = new Audio("/Users/adam/Music/alert.mkv", archive.getId());
        dao.addAudio(audio2);

        List<Audio> audios = dao.findAudiosByArchive(archive.getId());

        assertEquals(audios.size(), 2);
    }

    @Test
    public void canListAudiosForDevice() throws Exception {
        dao.addAudio(audio);
        Audio audio2 = new Audio("/Users/adam/Music/alert.mkv", archive.getId());
        dao.addAudio(audio2);

        Archive archive2 = new Archive("/Volumes/sands/Music");
        archive2.setDeviceId(device.getId());
        dao.addArchive(archive2);
        Audio audio3 = new Audio("/Volumes/sands/Music/alert.mkv", archive2.getId());
        dao.addAudio(audio3);

        List<Audio> audios = dao.findAudiosByDevice(device.getId());

        assertEquals(3, audios.size());
    }

    @Test
    public void deleteRemovesExistingAudio() throws Exception {
        dao.addAudio(audio);
        assertEquals(dao.findAudiosByArchive(archive.getId()).size(), 1);


        dao.destroyAudio(audio);
        Audio destroyedAudio = dao.findAudioById(audio.getId());

        assertEquals(destroyedAudio, null);
        assertEquals(dao.findAudiosByArchive(archive.getId()).size(), 0);
    }

    @Test
    public void existingAudioCanBeUpdated() throws Exception {
        dao.addAudio(audio);
        assertEquals(audio.getPlaybackTime(), 0);

        audio.setPlaybackTime(10);
        Audio savedAudio = dao.updateAudio(audio, "playbackTime", "10");

        assertEquals(audio, savedAudio);
    }

}
