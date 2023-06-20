package ru.job4j.grabber.utils;

import ru.job4j.grabber.model.Post;

import java.sql.SQLException;
import java.util.List;

public interface Store {
    void save(Post post);

    List<Post> getAll() throws SQLException;

    Post findById(int id) throws SQLException;

    void close() throws SQLException;
}
