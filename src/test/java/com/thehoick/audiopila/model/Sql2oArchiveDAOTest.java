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

    // TODO:as test that a directory in Archive path exists before creating it.

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
    public void findById() throws Exception {
        Archive archive = new Archive("/Volumes/TarDisk/Music");
        dao.add(archive);

        Archive savedArchive = dao.findById(archive.getId());

        assertEquals(savedArchive.getId(), archive.getId());
    }

    // TODO:as create test for updating an Archive.

    // TODO:as create test for deleting an Archive.
}