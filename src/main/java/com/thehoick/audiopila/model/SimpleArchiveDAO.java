package com.thehoick.audiopila.model;

import java.util.ArrayList;
import java.util.List;

public class SimpleArchiveDAO implements ArchiveDAO {
    private List<Archive> archives;

    public SimpleArchiveDAO() {
        archives = new ArrayList<>();
    }

    @Override
    public boolean add(Archive archive) {
        return archives.add(archive);
    }

    @Override
    public List<Archive> findAll() {
        return new ArrayList<>(archives);
    }
}
