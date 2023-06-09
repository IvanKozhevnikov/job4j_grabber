package ru.job4j.grabber.store;

import ru.job4j.grabber.HabrCareerDateTimeParser;
import ru.job4j.grabber.HabrCareerParse;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.utils.Store;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private Connection cnn;

    private long millis = System.currentTimeMillis();

    private Timestamp timestamp = new Timestamp(millis);

    private LocalDateTime localDateTime = timestamp.toLocalDateTime();

    public PsqlStore(Properties cfg) throws ClassNotFoundException, SQLException {
            Class.forName(cfg.getProperty("hibernate.connection.driver_class"));
            cnn = DriverManager.getConnection(
                    cfg.getProperty("hibernate.connection.url"),
                    cfg.getProperty("hibernate.connection.username"),
                    cfg.getProperty("hibernate.connection.password")
            );
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement =
                     cnn.prepareStatement("INSERT INTO post(name, text, link, created)"
                                     + " VALUES (?, ?, ?, ?) ON CONFLICT (link) DO NOTHING",
                             Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() throws SQLException {
        List<Post> list = new ArrayList<>();
        try (Statement statement = cnn.createStatement()) {
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM post ORDER BY id DESC;"
            );
            while (resultSet.next()) {
                int i = 0;
                int id = resultSet.getInt("id");
                String title = resultSet.getString("name");
                String description = resultSet.getString("text");
                String link = resultSet.getString("link");
                localDateTime = resultSet.getTimestamp("created").toLocalDateTime();
                list.add(i, new Post(id, title, link, description, localDateTime));
            }
        }
        return list;
    }

    @Override
    public Post findById(int id) throws SQLException {
        Post post = null;
        try (Statement statement = cnn.createStatement()) {
            ResultSet resultSet = statement.executeQuery(String.format(
                    "SELECT * FROM post WHERE id = '%s' ORDER BY id DESC;", id
            ));
            while (resultSet.next()) {
                int setId = resultSet.getInt("id");
                String title = resultSet.getString("name");
                String description = resultSet.getString("text");
                String link = resultSet.getString("link");
                localDateTime = resultSet.getTimestamp("created").toLocalDateTime();
                post = new Post(setId, link, title, description, localDateTime);
            }
        }

        return post;
    }

    @Override
    public void close() throws SQLException {
        if (cnn != null) {
            cnn.close();
        }
    }
}
