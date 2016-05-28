package com.thehoick.audiopila.model;

import com.thehoick.audiopila.exc.DAOException;

import java.util.ArrayList;
import java.util.List;

public class SimpleArchiveDAO implements ArchiveDAO {
    private List<Archive> archives;

    public SimpleArchiveDAO() {
        archives = new ArrayList<>();
    }

    @Override
    public void add(Archive archive) {
        archives.add(archive);
    }

    @Override
    public List<Archive> findAll() {
        return new ArrayList<>(archives);
    }


    @Override
    public Archive findById(int archiveId) throws DAOException {
        return null;
    }

    @Override
    public Archive update(Archive archive, String field, String value) throws DAOException {
        return null;
    }

    @Override
    public void destroy(Archive archive) throws DAOException {

    }
}
