CREATE TABLE "persons" (
    id bigserial NOT NULL,
    name character varying(200),
    surname character varying(200),
    job character varying(200),
    city character varying(200),
    age integer,
    CONSTRAINT "Persons_pkey" PRIMARY KEY (id)
);
INSERT INTO persons(name, surname, job, city, age) values ('Петр', 'Петров', 'Программист', 'Москва', 25);
INSERT INTO persons(name, surname, job, city, age) values ('Владимир', 'Иванов', 'Уборщик', 'Пермь', 26);
INSERT INTO persons(name, surname, job, city, age) values ('Иван', 'Иванов', 'Врач', 'Санкт-Петербург', 27);
INSERT INTO persons(name, surname, job, city, age) values ('Иммануил', 'Кант', 'Продавец', 'Москва', 28);
INSERT INTO persons(name, surname, job, city, age) values ('Джордж', 'Клуни', 'Охранник', 'Санкт-Петербург', 29);
INSERT INTO persons(name, surname, job, city, age) values ('Билл', 'Рубцов', 'Полицейский', 'Челябинск', 30);
INSERT INTO persons(name, surname, job, city, age) values ('Марк', 'Марков', 'Программист', 'Фершампенуаз', 31);
INSERT INTO persons(name, surname, job, city, age) values ('Галина', 'Матвеева', 'Дальнобойщик', 'Новосибирск', 32);
INSERT INTO persons(name, surname, job, city, age) values ('Святослав', 'Павлов', 'Политик', 'Магнитогорск', 33);
INSERT INTO persons(name, surname, job, city, age) values ('Ольга', 'Бергольц', 'Писатель', 'Калининград', 34);
INSERT INTO persons(name, surname, job, city, age) values ('Лев', 'Рабинович', 'Инженер', 'Москва', 35);
