package ru.job4j.grabber;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.assertj.core.api.Assertions.*;

class HabrCareerParseTest {

    @Test
    void parseInvalidFormatDateThenException() {
        HabrCareerParse parser = new HabrCareerParse();
        String in = "2021/09/03T07:19:14+03:00";
        assertThatThrownBy(() -> parser.parse(in))
                .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    void parseValidDateTimePattern() {
        HabrCareerParse parser = new HabrCareerParse();
        String in = "2021-09-03T07:19:14+03:00";
        String expect = "2021-09-03T07:19:14";
        assertThat(parser.parse(in).toString()).isEqualTo(expect);
    }
}
