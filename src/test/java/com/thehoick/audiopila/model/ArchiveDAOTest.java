package com.thehoick.audiopila.model;

import com.thehoick.audiopila.exc.DAOException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.nio.file.NotDirectoryException;

import static org.junit.Assert.*;

public class ArchiveDAOTest {
    private Sql2oSqliteDAO dao;
    Sql2o sql2o;
    Connection con;
    private Device device;

    @Before
    public void setUp() throws Exception {
        String connectionString = migrate();
        sql2o = new Sql2o(connectionString, "", "");
        dao = new Sql2oSqliteDAO(sql2o);
        con = sql2o.open();

        String hostname = java.net.InetAddress.getLocalHost().getHostName();
        String osName = System.getProperty("os.name");
        device = new Device(hostname, osName);
    }

    public String migrate() {
        String dbPath = this.getClass().getResource("/db/").toString();

        // Live database.
//        String connectionString = "jdbc:sqlite:" + dbPath + "audiopila.db";
        // Test database.
        String connectionString = "jdbc:sqlite:" + dbPath + "test.db";

        // Uncomment to perform migration.
        // May have to delete database first to avoide checksum mismatch...
//        Flyway flyway = new Flyway();
//        flyway.setDataSource(connectionString, null, null);
//        flyway.migrate();

        return connectionString;
    }

    @After
    public void tearDown() throws Exception {
        // Clear the data manually.
        String sql = "delete from archives; delete from devices; delete from sqlite_sequence;";
        con.createQuery(sql)
            .executeUpdate();
        con.close();
    }

    @Test
    public void addingArchiveSetsId() throws Exception {
        Archive archive = new Archive("/Users/adam/Music");
        int originalId = archive.getId();

        dao.addArchive(archive);

        assertNotEquals(originalId, archive.getId());
    }

    @Test
    public void addingArchiveSetsDeviceId() throws Exception {
        Archive archive = new Archive("/Users/adam/Music");
        int originalId = archive.getId();

        archive.setDeviceId(device.getId());
        dao.addArchive(archive);
        Archive dbArchive = dao.findArchiveById(archive.getId());

        assertNotEquals(originalId, dbArchive.getId());
        assertEquals(dbArchive.getDeviceId(), device.getId());
    }

    @Test(expected = NotDirectoryException.class)
    public void archiveIsNotAddedIfBadDirectory() throws Exception {
        Archive archive = new Archive("/Users/adam/Musicssss");
        dao.addArchive(archive);
    }

    @Test
    public void allArchivesAreReturnedByFindArchives() throws Exception {
        Archive archive = new Archive("/Volumes/sands/Music");
        dao.addArchive(archive);

        assertEquals(1, dao.findArchives().size());
    }

    @Test
    public void noArchivesReturnsEmptyList() throws Exception {
        assertEquals(dao.findArchives().size(), 0);
    }

    @Test
    public void existingArchivesCanBeFoundById() throws Exception {
        Archive archive = new Archive("/Volumes/TarDisk/Music");
        dao.addArchive(archive);

        Archive savedArchive = dao.findArchiveById(archive.getId());

        assertEquals(savedArchive, archive);
    }

    @Test
    public void existingArchiveCanBeUpdated() throws Exception {
        Archive archive = new Archive("/Volumes/TarDisk/Music");
        dao.addArchive(archive);

        assertEquals(archive.getPath(), "/Volumes/TarDisk/Music");
        archive = dao.updateArchive(archive, "path", "/Volumes/sands/Music");
        assertEquals(archive.getPath(), "/Volumes/sands/Music");
    }

    @Test
    public void deleteRemovesExistingArchive() throws Exception {
        Archive archive = new Archive("/Users/adam/Downloads");
        dao.addArchive(archive);
        assertEquals(dao.findArchives().size(), 1);

        dao.destroyArchive(archive);
        Archive destroyedArchive = dao.findArchiveById(archive.getId());

        assertEquals(destroyedArchive, null);
        assertEquals(dao.findArchives().size(), 0);

//        System.getProperties().list(System.out);
        String osName  = System.getProperty("os.name");
        String hostname = java.net.InetAddress.getLocalHost().getHostName();
        System.out.println("os: " + osName + " hostname: " + hostname);
    }

}