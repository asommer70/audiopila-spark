package com.thehoick.audiopila.model;

import com.thehoick.audiopila.exc.DAOException;

import java.util.List;

public interface ArchiveDAO {
    void add(Archive archive) throws DAOException;

    List<Archive> findAll() throws DAOException;

    Archive findById(int archiveId) throws DAOException;
}
