package com.thehoick.audiopila.model;

import com.thehoick.audiopila.exc.DAOException;
import javafx.scene.shape.Arc;
import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import org.sqlite.ExtendedCommand;

import java.nio.file.NotDirectoryException;
import java.util.List;

import static org.junit.Assert.*;

public class Sql2oArchiveDAOTest {
    private Sql2oArchiveDAO dao;
    Sql2o sql2o;
    Connection con;

    @Before
    public void setUp() throws Exception {
        String connectionString = migrate();
        sql2o = new Sql2o(connectionString, "", "");
        dao = new Sql2oArchiveDAO(sql2o);
        con = sql2o.open();
    }

    public String migrate() {
        String dbPath = this.getClass().getResource("/db/").toString();
        System.out.println("dbPath: " + dbPath);
        // Live database.
//        String connectionString = "jdbc:sqlite:" + dbPath + "audiopila.db";
        // Test database.
        String connectionString = "jdbc:sqlite:" + dbPath + "test.db";

        // Uncomment to perform migration.
//        Flyway flyway = new Flyway();
//        flyway.setDataSource(connectionString, null, null);
//        flyway.migrate();

        return connectionString;
    }

    @After
    public void tearDown() throws Exception {
        // Clear the data manually.
        String sql = "delete from archives; delete from sqlite_sequence where name = 'archives';";
        try {
            con.createQuery(sql)
                .executeUpdate();
        } catch (Sql2oException ex) {
            throw new DAOException(ex, "Problem adding archive.");
        }
        con.close();
    }

    @Test
    public void addingArchiveSetsId() throws Exception {
        Archive archive = new Archive("/Users/adam/Music");
        int originalId = archive.getId();

        dao.add(archive);

        assertNotEquals(originalId, archive.getId());
    }

    @Test(expected = NotDirectoryException.class)
    public void archiveIsNotAddedIfBadDirectory() throws Exception {
        Archive archive = new Archive("/Users/adam/Musicssss");
        dao.add(archive);
    }

    @Test
    public void allCoursesAreReturnedByFindAll() throws Exception {
        Archive archive = new Archive("/Volumes/sands/Music");
        dao.add(archive);

        assertEquals(dao.findAll().size(), 1);
    }

    @Test
    public void noArchivesReturnsEmptyList() throws Exception {
        assertEquals(dao.findAll().size(), 0);
    }

    @Test
    public void existingArchivesCanBeFoundById() throws Exception {
        Archive archive = new Archive("/Volumes/TarDisk/Music");
        dao.add(archive);

        Archive savedArchive = dao.findById(archive.getId());

        assertEquals(savedArchive, archive);
    }

    // TODO:as add the device name to the archives table... or create a devices table and link it to the archives table.

    @Test
    public void existingArchiveCanBeUpdated() throws Exception {
        Archive archive = new Archive("/Volumes/TarDisk/Music");
        dao.add(archive);

        assertEquals(archive.getPath(), "/Volumes/TarDisk/Music");
        archive = dao.update(archive, "path", "/Volumes/sands/Music");
        assertEquals(archive.getPath(), "/Volumes/sands/Music");
    }

    @Test
    public void deleteRemovesExistingArchive() throws Exception {
        Archive archive = new Archive("/Users/adam/Downloads");
        dao.add(archive);
        assertEquals(dao.findAll().size(), 1);

        dao.destroy(archive);
        Archive destroyedArchive = dao.findById(archive.getId());

        assertEquals(destroyedArchive, null);
        assertEquals(dao.findAll().size(), 0);
    }

}