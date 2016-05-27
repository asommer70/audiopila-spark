package com.thehoick.audiopila.model;

import org.apache.commons.jexl2.UnifiedJEXL;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class SimpleArchiveDAOTest {
    private SimpleArchiveDAO dao = new SimpleArchiveDAO();

    @Before
    public void setUp() throws Exception {
        Archive archive = new Archive("/Volumes/sands/Music");
        dao.add(archive);
    }

    @Test
    public void archivesCanBeAdded() throws Exception {
        Archive archive = new Archive("/Users/adam/Music");
        dao.add(archive);

        assertEquals(archive.getName(), "adam Music");
    }

    @Test
    public void archivesCanBeSaved() throws Exception {
        List<Archive> archives = dao.findAll();
        assertEquals(archives.size(), 1);
        assertEquals(archives.get(0).getName(), "sands Music");
    }
}