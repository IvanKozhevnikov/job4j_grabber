[![Build Status](https://travis-ci.com/IvanKozhevnikov/job4j_grabber.svg?branch=master)](https://travis-ci.com/IvanKozhevnikov/job4j_grabber)
[![codecov](https://codecov.io/gh/IvanKozhevnikov/job4j_grabber/branch/master/graph/badge.svg?token=UHBPUPMT47)](https://codecov.io/gh/IvanKozhevnikov/job4j_grabber)

# job4j_grabber

Описание.

Система запускается по расписанию - раз в минуту.  Период запуска указывается в настройках - app.properties.

Первый сайт будет career.habr.com. Работаем с разделом 
https://career.habr.com/vacancies/java_developer. 
Программа должна считывать все вакансии c первых
5 страниц относящиеся к Java и записывать их в базу.