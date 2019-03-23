CREATE TABLE "persons" (
    id bigserial NOT NULL,
    name character varying(200),
    surname character varying(200),
    job character varying(200),
    city character varying(200),
    age integer,
    avatar bytea,
    CONSTRAINT "Persons_pkey" PRIMARY KEY (id)
);
INSERT INTO persons(name, surname, job, city, age, avatar) values ('Петр', 'Петров', 'Программист', 'Москва', 25, NULL);
INSERT INTO persons(name, surname, job, city, age, avatar) values ('Владимир', 'Иванов', 'Уборщик', 'Пермь', 26, NULL);
INSERT INTO persons(name, surname, job, city, age, avatar) values ('Иван', 'Иванов', 'Врач', 'Санкт-Петербург', 27, NULL);
INSERT INTO persons(name, surname, job, city, age, avatar) values ('Иммануил', 'Кант', 'Продавец', 'Москва', 28, NULL);
INSERT INTO persons(name, surname, job, city, age, avatar) values ('Джордж', 'Клуни', 'Охранник', 'Санкт-Петербург', 29, NULL);
INSERT INTO persons(name, surname, job, city, age, avatar) values ('Билл', 'Рубцов', 'Полицейский', 'Челябинск', 30, NULL);
INSERT INTO persons(name, surname, job, city, age, avatar) values ('Марк', 'Марков', 'Программист', 'Фершампенуаз', 31, NULL);
INSERT INTO persons(name, surname, job, city, age, avatar) values ('Галина', 'Матвеева', 'Дальнобойщик', 'Новосибирск', 32, NULL);
INSERT INTO persons(name, surname, job, city, age, avatar) values ('Святослав', 'Павлов', 'Политик', 'Магнитогорск', 33, NULL);
INSERT INTO persons(name, surname, job, city, age, avatar) values ('Ольга', 'Бергольц', 'Писатель', 'Калининград', 34, NULL);
INSERT INTO persons(name, surname, job, city, age, avatar) values ('Лев', 'Рабинович', 'Инженер', 'Москва', 35, NULL);
