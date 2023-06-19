package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.model.Post;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.Parse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format(
            "%s/vacancies/java_developer?page=", SOURCE_LINK);

    private static final int NUMBER_OF_PAGES = 5;

    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static void main(String[] args) throws IOException {
        HabrCareerParse habrCareerParse = new HabrCareerParse(new HabrCareerDateTimeParser());
        List<Post> habrListPost = habrCareerParse.list(PAGE_LINK);
        for (Post post : habrListPost) {
            System.out.printf("%d. %s %s"
                            + "%nОписание вакансии:%n"
                            + "%s%n"
                            + "%s"
                            + "%n------------------------------------------------------" +
                            "----------------------------------------------------------" +
                            "----------------------------------------------------------" +
                            "----------------------------------------------------------%n",
                    post.getId(), post.getTitle(), post.getLink(), post.getDescription(), post.getCreated());
        }
    }

    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        return document.select(".vacancy-description__text").text();
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> postList = new ArrayList<>();
        HabrCareerDateTimeParser parser = new HabrCareerDateTimeParser();
        for (int i = 1; i <= NUMBER_OF_PAGES; i++) {
            DateTimeParser dateTimeParse = null;
            HabrCareerParse habrCareerParse = new HabrCareerParse(dateTimeParse);
            Connection connection = Jsoup.connect(String.format("%s%s", link, i));
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element dateElement = row.select(".vacancy-card__date").first();
                Element vacancyDate = dateElement.child(0);
                LocalDateTime date = parser.parse(vacancyDate.attr("datetime"));
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                String linkOfPage = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String description = null;
                try {
                    description = habrCareerParse.retrieveDescription(linkOfPage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Post vacancyPost = new Post(vacancyName, linkOfPage, description, date);
                postList.add(vacancyPost);
                vacancyPost.setId(postList.indexOf(vacancyPost));
            });
        }
        return postList;
    }
}
