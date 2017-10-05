--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.4
-- Dumped by pg_dump version 9.5.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

--
-- Name: passport; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE passport AS (
	serial_number integer,
	id integer
);


ALTER TYPE passport OWNER TO postgres;

--
-- Name: add_person(text, text, text, date, character, text, text, text, bytea, integer, integer, bit[], text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION add_person(last_name text, first_name text, second_name text, date_of_birth date, sex character, place_of_birth text, address text, phone text, photo bytea, passport_serial integer, passport_id integer, timetable bit[], position_name text, OUT person_id integer, OUT sched_id integer) RETURNS record
    LANGUAGE plpgsql
    AS $$
DECLARE pos_id INTEGER;
BEGIN 
select position_id into pos_id from positions where name = position_name;
INSERT INTO persons VALUES(NULL, last_name, first_name, second_name, date_of_birth, sex, place_of_birth, address, phone, photo, (passport_serial, passport_id)::public.passport, pos_id);
SELECT currval(pg_get_serial_sequence('persons','person_id')) into person_id;
insert into person_schedule values(null, person_id, timetable, date(now()));
SELECT currval(pg_get_serial_sequence('person_schedule','sched_id')) into sched_id;
END 
 $$;


ALTER FUNCTION public.add_person(last_name text, first_name text, second_name text, date_of_birth date, sex character, place_of_birth text, address text, phone text, photo bytea, passport_serial integer, passport_id integer, timetable bit[], position_name text, OUT person_id integer, OUT sched_id integer) OWNER TO postgres;

--
-- Name: add_position(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION add_position(name text, description text) RETURNS integer
    LANGUAGE sql
    AS $$

INSERT INTO positions VALUES(null, name, description) RETURNING position_id;

$$;


ALTER FUNCTION public.add_position(name text, description text) OWNER TO postgres;

--
-- Name: add_position_salary(integer, integer, numeric); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION add_position_salary(pos_id integer, sh_id integer, salary numeric) RETURNS integer
    LANGUAGE sql
    AS $$
INSERT INTO position_salary VALUES(pos_id, sh_id, salary) RETURNING pos_id;
$$;


ALTER FUNCTION public.add_position_salary(pos_id integer, sh_id integer, salary numeric) OWNER TO postgres;

--
-- Name: add_product(text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION add_product(name text, type_id integer) RETURNS integer
    LANGUAGE sql
    AS $$
INSERT INTO products VALUES(NULL, name, type_id) RETURNING product_id;
 $$;


ALTER FUNCTION public.add_product(name text, type_id integer) OWNER TO postgres;

--
-- Name: add_product(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION add_product(name text, type_name text) RETURNS integer
    LANGUAGE plpgsql
    AS $$
DECLARE ptype integer;
BEGIN
SELECT type_id INTO ptype FROM product_types WHERE title = type_name;
INSERT INTO products VALUES(NULL, name, ptype) RETURNING product_id; 
END 
 $$;


ALTER FUNCTION public.add_product(name text, type_name text) OWNER TO postgres;

--
-- Name: add_product_amount(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION add_product_amount(pr_id integer, sh_id integer, amount integer) RETURNS integer
    LANGUAGE sql
    AS $$
INSERT INTO product_amounts VALUES(pr_id, sh_id, amount, date(now())) RETURNING product_id; 
$$;


ALTER FUNCTION public.add_product_amount(pr_id integer, sh_id integer, amount integer) OWNER TO postgres;

--
-- Name: add_product_price(integer, integer, numeric); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION add_product_price(pr_id integer, sh_id integer, price numeric) RETURNS integer
    LANGUAGE sql
    AS $$
INSERT INTO product_prices VALUES(pr_id, sh_id, price, date(now())) RETURNING product_id; 
$$;


ALTER FUNCTION public.add_product_price(pr_id integer, sh_id integer, price numeric) OWNER TO postgres;

--
-- Name: add_product_type(text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION add_product_type(title text, description text) RETURNS integer
    LANGUAGE sql
    AS $$
INSERT INTO product_types VALUES(null, title, description) RETURNING type_id; 
$$;


ALTER FUNCTION public.add_product_type(title text, description text) OWNER TO postgres;

--
-- Name: add_sell_log(integer, integer, integer, date); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION add_sell_log(p_id integer, sh_id integer, in_amount integer, date date) RETURNS integer
    LANGUAGE sql
    AS $$
 UPDATE product_amounts SET amount = product_amounts.amount - in_amount WHERE product_id = p_id AND shop_id = sh_id;
 INSERT INTO sell_log VALUES(NULL, p_id, sh_id, in_amount, date) RETURNING log_id; 
$$;


ALTER FUNCTION public.add_sell_log(p_id integer, sh_id integer, in_amount integer, date date) OWNER TO postgres;

--
-- Name: add_shop(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION add_shop(street text) RETURNS integer
    LANGUAGE sql
    AS $$
INSERT INTO shops VALUES(NULL, street) RETURNING shop_id; 
$$;


ALTER FUNCTION public.add_shop(street text) OWNER TO postgres;

--
-- Name: delete_person(integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION delete_person(p_id integer) RETURNS void
    LANGUAGE sql
    AS $$
DELETE FROM persons WHERE person_id = p_id;
$$;


ALTER FUNCTION public.delete_person(p_id integer) OWNER TO postgres;

--
-- Name: test(text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION test(one text) RETURNS integer
    LANGUAGE plpgsql
    AS $$
begin
    RETURN 0;
END
$$;


ALTER FUNCTION public.test(one text) OWNER TO postgres;

--
-- Name: tf_add_log(); Type: FUNCTION; Schema: public; Owner: coursework
--

CREATE FUNCTION tf_add_log() RETURNS trigger
    LANGUAGE plpgsql
    AS $$BEGIN
new.log_id = nextval('sell_log_log_id_seq');
return NEW;
END;$$;


ALTER FUNCTION public.tf_add_log() OWNER TO coursework;

--
-- Name: tf_add_person(); Type: FUNCTION; Schema: public; Owner: coursework
--

CREATE FUNCTION tf_add_person() RETURNS trigger
    LANGUAGE plpgsql
    AS $$BEGIN
new.person_id = nextval('persons_person_id_seq');
return NEW;
END;$$;


ALTER FUNCTION public.tf_add_person() OWNER TO coursework;

--
-- Name: tf_add_position(); Type: FUNCTION; Schema: public; Owner: coursework
--

CREATE FUNCTION tf_add_position() RETURNS trigger
    LANGUAGE plpgsql
    AS $$BEGIN
new.position_id = nextval('positions_position_id_seq');
return NEW;
END;$$;


ALTER FUNCTION public.tf_add_position() OWNER TO coursework;

--
-- Name: tf_add_product(); Type: FUNCTION; Schema: public; Owner: coursework
--

CREATE FUNCTION tf_add_product() RETURNS trigger
    LANGUAGE plpgsql
    AS $$BEGIN
new.product_id = nextval('products_product_id_seq');
return NEW;
END;$$;


ALTER FUNCTION public.tf_add_product() OWNER TO coursework;

--
-- Name: tf_add_product_type(); Type: FUNCTION; Schema: public; Owner: coursework
--

CREATE FUNCTION tf_add_product_type() RETURNS trigger
    LANGUAGE plpgsql
    AS $$BEGIN
new.type_id = nextval('product_types_type_id_seq');
return NEW;
END;$$;


ALTER FUNCTION public.tf_add_product_type() OWNER TO coursework;

--
-- Name: tf_add_schedule_id(); Type: FUNCTION; Schema: public; Owner: coursework
--

CREATE FUNCTION tf_add_schedule_id() RETURNS trigger
    LANGUAGE plpgsql
    AS $$BEGIN
NEW.sched_id = nextval('person_schedule_sched_id_seq');
return new;
END;$$;


ALTER FUNCTION public.tf_add_schedule_id() OWNER TO coursework;

--
-- Name: tf_add_shop(); Type: FUNCTION; Schema: public; Owner: coursework
--

CREATE FUNCTION tf_add_shop() RETURNS trigger
    LANGUAGE plpgsql
    AS $$BEGIN
new.shop_id = nextval('shops_shop_id_seq');
return NEW;
END;$$;


ALTER FUNCTION public.tf_add_shop() OWNER TO coursework;

--
-- Name: tf_update_log(); Type: FUNCTION; Schema: public; Owner: coursework
--

CREATE FUNCTION tf_update_log() RETURNS trigger
    LANGUAGE plpgsql
    AS $$BEGIN
	IF new.amount < 0 THEN
            RAISE EXCEPTION 'Amount can not be neg';
	END IF;
return new;
END;$$;


ALTER FUNCTION public.tf_update_log() OWNER TO coursework;

--
-- Name: tf_update_person(); Type: FUNCTION; Schema: public; Owner: coursework
--

CREATE FUNCTION tf_update_person() RETURNS trigger
    LANGUAGE plpgsql
    AS $$BEGIN
IF not new.sex ~ '[MF]' THEN
            RAISE EXCEPTION 'Sex may be M or F only';
        END IF;
IF (NEW.passport).serial_number < 1000 or (NEW.passport).serial_number > 9999 or
						(NEW.passport).id < 100000 or (NEW.passport).id > 999999 then 
            RAISE EXCEPTION 'passport serial_number between 999 and 10000, id between 99999 and 1000000';
        END IF;
return NEW;
END;$$;


ALTER FUNCTION public.tf_update_person() OWNER TO coursework;

--
-- Name: tf_update_position_salary(); Type: FUNCTION; Schema: public; Owner: coursework
--

CREATE FUNCTION tf_update_position_salary() RETURNS trigger
    LANGUAGE plpgsql
    AS $$BEGIN
	IF new.salary < 0 THEN
            RAISE EXCEPTION 'Salary can not be neg';
	END IF;
return new;
END;$$;


ALTER FUNCTION public.tf_update_position_salary() OWNER TO coursework;

--
-- Name: tf_update_product_price(); Type: FUNCTION; Schema: public; Owner: coursework
--

CREATE FUNCTION tf_update_product_price() RETURNS trigger
    LANGUAGE plpgsql
    AS $$BEGIN
	IF new.price < 0 THEN
            RAISE EXCEPTION 'Amount can not be neg';
	END IF;
return new;
END;$$;


ALTER FUNCTION public.tf_update_product_price() OWNER TO coursework;

--
-- Name: update_person(integer, text, text, text, date, character, text, text, text, bytea, integer, integer, bit[], text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION update_person(p_id integer, lastname text, firstname text, secondname text, dateofbirth date, in_sex character, placeofbirth text, in_address text, in_phone text, in_photo bytea, passport_serial integer, passport_id integer, in_timetable bit[], in_position_name text) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE pos_id INTEGER;
BEGIN
select position_id into pos_id from positions where name = in_position_name;
UPDATE persons SET last_name = lastname,
                   first_name = firstname,
                   second_name = secondname,
                   date_of_birth = dateofbirth,
                   sex = in_sex,
                   place_of_birth = placeofbirth,
                   address = in_address,
                   phone = phone,
                   photo = in_photo,
                   passport = (passport_serial, passport_id)::public.passport,
                   position_id = pos_id WHERE person_id = p_id;

UPDATE person_schedule SET timetable = in_timetable, updated = date(now()) WHERE person_id = p_id;

END
 $$;


ALTER FUNCTION public.update_person(p_id integer, lastname text, firstname text, secondname text, dateofbirth date, in_sex character, placeofbirth text, in_address text, in_phone text, in_photo bytea, passport_serial integer, passport_id integer, in_timetable bit[], in_position_name text) OWNER TO postgres;

--
-- Name: update_position(integer, text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION update_position(pos_id integer, name text, description text) RETURNS void
    LANGUAGE sql
    AS $$
UPDATE positions SET name = name, description = description WHERE position_id = pos_id;
$$;


ALTER FUNCTION public.update_position(pos_id integer, name text, description text) OWNER TO postgres;

--
-- Name: update_position_salary(integer, integer, numeric); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION update_position_salary(pos_id integer, sh_id integer, in_salary numeric) RETURNS void
    LANGUAGE sql
    AS $$
UPDATE position_salary SET salary = in_salary WHERE position_id = pos_id AND shop_id = sh_id;
$$;


ALTER FUNCTION public.update_position_salary(pos_id integer, sh_id integer, in_salary numeric) OWNER TO postgres;

--
-- Name: update_product(integer, text, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION update_product(p_id integer, name text, type_id integer) RETURNS void
    LANGUAGE sql
    AS $$
UPDATE products SET name = name, type_id = type_id WHERE product_id = p_id;
 $$;


ALTER FUNCTION public.update_product(p_id integer, name text, type_id integer) OWNER TO postgres;

--
-- Name: update_product_amount(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION update_product_amount(pr_id integer, sh_id integer, amount integer) RETURNS void
    LANGUAGE sql
    AS $$
UPDATE product_amounts SET amount = amount, updated = date(now()) WHERE product_id = pr_id AND shop_id = sh_id;
$$;


ALTER FUNCTION public.update_product_amount(pr_id integer, sh_id integer, amount integer) OWNER TO postgres;

--
-- Name: update_product_price(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION update_product_price(pr_id integer, sh_id integer, amount integer) RETURNS void
    LANGUAGE sql
    AS $$
UPDATE product_prices SET price = price, updated = date(now()) WHERE product_id = pr_id AND shop_id = sh_id;
$$;


ALTER FUNCTION public.update_product_price(pr_id integer, sh_id integer, amount integer) OWNER TO postgres;

--
-- Name: update_product_type(integer, text, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION update_product_type(t_id integer, title text, description text) RETURNS void
    LANGUAGE sql
    AS $$
UPDATE product_types SET title = title, description = description WHERE type_id = t_id; 
$$;


ALTER FUNCTION public.update_product_type(t_id integer, title text, description text) OWNER TO postgres;

--
-- Name: update_sell_log(integer, integer, integer, integer, date); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION update_sell_log(l_id integer, p_id integer, sh_id integer, in_amount integer, in_date date) RETURNS void
    LANGUAGE plpgsql
    AS $$
DECLARE prev_amount INTEGER;
BEGIN
SELECT amount INTO prev_amount FROM sell_log WHERE product_id = p_id AND shop_id = sh_id;
UPDATE product_amounts SET amount = product_amounts.amount - (prev_amount-in_amount) WHERE product_id = p_id AND shop_id = sh_id;
UPDATE sell_log SET amount = in_amount, date = in_date WHERE log_id = l_id;
END
 $$;


ALTER FUNCTION public.update_sell_log(l_id integer, p_id integer, sh_id integer, in_amount integer, in_date date) OWNER TO postgres;

--
-- Name: update_shop(integer, text); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION update_shop(sh_id integer, street text) RETURNS void
    LANGUAGE sql
    AS $$
UPDATE shops SET street = street WHERE shop_id = sh_id;
$$;


ALTER FUNCTION public.update_shop(sh_id integer, street text) OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: person_schedule; Type: TABLE; Schema: public; Owner: coursework
--

CREATE TABLE person_schedule (
    sched_id integer NOT NULL,
    person_id integer NOT NULL,
    timetable bit(64)[] NOT NULL,
    updated date DEFAULT date(now()) NOT NULL
);


ALTER TABLE person_schedule OWNER TO coursework;

--
-- Name: person_schedule_sched_id_seq; Type: SEQUENCE; Schema: public; Owner: coursework
--

CREATE SEQUENCE person_schedule_sched_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE person_schedule_sched_id_seq OWNER TO coursework;

--
-- Name: person_schedule_sched_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: coursework
--

ALTER SEQUENCE person_schedule_sched_id_seq OWNED BY person_schedule.sched_id;


--
-- Name: person_shop; Type: TABLE; Schema: public; Owner: coursework
--

CREATE TABLE person_shop (
    person_id integer NOT NULL,
    shop_id integer NOT NULL
);


ALTER TABLE person_shop OWNER TO coursework;

--
-- Name: persons; Type: TABLE; Schema: public; Owner: coursework
--

CREATE TABLE persons (
    person_id integer NOT NULL,
    last_name text NOT NULL,
    first_name text NOT NULL,
    second_name text,
    date_of_birth date NOT NULL,
    sex character(1) DEFAULT NULL::bpchar NOT NULL,
    place_of_birth text NOT NULL,
    address text NOT NULL,
    phone text NOT NULL,
    photo bytea,
    passport passport NOT NULL,
    position_id integer NOT NULL
);


ALTER TABLE persons OWNER TO coursework;

--
-- Name: persons_person_id_seq; Type: SEQUENCE; Schema: public; Owner: coursework
--

CREATE SEQUENCE persons_person_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE persons_person_id_seq OWNER TO coursework;

--
-- Name: persons_person_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: coursework
--

ALTER SEQUENCE persons_person_id_seq OWNED BY persons.person_id;


--
-- Name: position_salary; Type: TABLE; Schema: public; Owner: coursework
--

CREATE TABLE position_salary (
    position_id integer NOT NULL,
    shop_id integer NOT NULL,
    salary numeric NOT NULL
);


ALTER TABLE position_salary OWNER TO coursework;

--
-- Name: positions; Type: TABLE; Schema: public; Owner: coursework
--

CREATE TABLE positions (
    position_id integer NOT NULL,
    name text NOT NULL,
    description text
);


ALTER TABLE positions OWNER TO coursework;

--
-- Name: positions_position_id_seq; Type: SEQUENCE; Schema: public; Owner: coursework
--

CREATE SEQUENCE positions_position_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE positions_position_id_seq OWNER TO coursework;

--
-- Name: positions_position_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: coursework
--

ALTER SEQUENCE positions_position_id_seq OWNED BY positions.position_id;


--
-- Name: product_amounts; Type: TABLE; Schema: public; Owner: coursework
--

CREATE TABLE product_amounts (
    product_id integer NOT NULL,
    shop_id integer NOT NULL,
    amount integer NOT NULL,
    updated date DEFAULT date(now()) NOT NULL
);


ALTER TABLE product_amounts OWNER TO coursework;

--
-- Name: product_amounts_product_id_seq; Type: SEQUENCE; Schema: public; Owner: coursework
--

CREATE SEQUENCE product_amounts_product_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE product_amounts_product_id_seq OWNER TO coursework;

--
-- Name: product_amounts_product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: coursework
--

ALTER SEQUENCE product_amounts_product_id_seq OWNED BY product_amounts.product_id;


--
-- Name: product_prices; Type: TABLE; Schema: public; Owner: coursework
--

CREATE TABLE product_prices (
    product_id integer NOT NULL,
    shop_id integer NOT NULL,
    price numeric NOT NULL,
    updated date DEFAULT date(now()) NOT NULL
);


ALTER TABLE product_prices OWNER TO coursework;

--
-- Name: product_prices_product_id_seq; Type: SEQUENCE; Schema: public; Owner: coursework
--

CREATE SEQUENCE product_prices_product_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE product_prices_product_id_seq OWNER TO coursework;

--
-- Name: product_prices_product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: coursework
--

ALTER SEQUENCE product_prices_product_id_seq OWNED BY product_prices.product_id;


--
-- Name: product_types; Type: TABLE; Schema: public; Owner: coursework
--

CREATE TABLE product_types (
    type_id integer NOT NULL,
    title text NOT NULL,
    description text
);


ALTER TABLE product_types OWNER TO coursework;

--
-- Name: product_types_type_id_seq; Type: SEQUENCE; Schema: public; Owner: coursework
--

CREATE SEQUENCE product_types_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE product_types_type_id_seq OWNER TO coursework;

--
-- Name: product_types_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: coursework
--

ALTER SEQUENCE product_types_type_id_seq OWNED BY product_types.type_id;


--
-- Name: products; Type: TABLE; Schema: public; Owner: coursework
--

CREATE TABLE products (
    product_id integer NOT NULL,
    name character varying NOT NULL,
    type_id integer NOT NULL
);


ALTER TABLE products OWNER TO coursework;

--
-- Name: products_product_id_seq; Type: SEQUENCE; Schema: public; Owner: coursework
--

CREATE SEQUENCE products_product_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE products_product_id_seq OWNER TO coursework;

--
-- Name: products_product_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: coursework
--

ALTER SEQUENCE products_product_id_seq OWNED BY products.product_id;


--
-- Name: sell_log; Type: TABLE; Schema: public; Owner: coursework
--

CREATE TABLE sell_log (
    log_id integer NOT NULL,
    product_id integer NOT NULL,
    shop_id integer NOT NULL,
    amount integer NOT NULL,
    date date DEFAULT date(now())
);


ALTER TABLE sell_log OWNER TO coursework;

--
-- Name: sell_log_log_id_seq; Type: SEQUENCE; Schema: public; Owner: coursework
--

CREATE SEQUENCE sell_log_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE sell_log_log_id_seq OWNER TO coursework;

--
-- Name: sell_log_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: coursework
--

ALTER SEQUENCE sell_log_log_id_seq OWNED BY sell_log.log_id;


--
-- Name: shops; Type: TABLE; Schema: public; Owner: coursework
--

CREATE TABLE shops (
    shop_id integer NOT NULL,
    street text NOT NULL
);


ALTER TABLE shops OWNER TO coursework;

--
-- Name: shops_shop_id_seq; Type: SEQUENCE; Schema: public; Owner: coursework
--

CREATE SEQUENCE shops_shop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE shops_shop_id_seq OWNER TO coursework;

--
-- Name: shops_shop_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: coursework
--

ALTER SEQUENCE shops_shop_id_seq OWNED BY shops.shop_id;


--
-- Name: person_id; Type: DEFAULT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY persons ALTER COLUMN person_id SET DEFAULT nextval('persons_person_id_seq'::regclass);


--
-- Name: position_id; Type: DEFAULT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY positions ALTER COLUMN position_id SET DEFAULT nextval('positions_position_id_seq'::regclass);


--
-- Name: product_id; Type: DEFAULT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY product_amounts ALTER COLUMN product_id SET DEFAULT nextval('product_amounts_product_id_seq'::regclass);


--
-- Name: product_id; Type: DEFAULT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY product_prices ALTER COLUMN product_id SET DEFAULT nextval('product_prices_product_id_seq'::regclass);


--
-- Name: type_id; Type: DEFAULT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY product_types ALTER COLUMN type_id SET DEFAULT nextval('product_types_type_id_seq'::regclass);


--
-- Name: product_id; Type: DEFAULT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY products ALTER COLUMN product_id SET DEFAULT nextval('products_product_id_seq'::regclass);


--
-- Name: log_id; Type: DEFAULT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY sell_log ALTER COLUMN log_id SET DEFAULT nextval('sell_log_log_id_seq'::regclass);


--
-- Name: shop_id; Type: DEFAULT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY shops ALTER COLUMN shop_id SET DEFAULT nextval('shops_shop_id_seq'::regclass);


--
-- Data for Name: person_schedule; Type: TABLE DATA; Schema: public; Owner: coursework
--

COPY person_schedule (sched_id, person_id, timetable, updated) FROM stdin;
3	16	{0000000000000000000000000000000000000000000000000000000000000000}	2016-11-01
\.


--
-- Name: person_schedule_sched_id_seq; Type: SEQUENCE SET; Schema: public; Owner: coursework
--

SELECT pg_catalog.setval('person_schedule_sched_id_seq', 3, true);


--
-- Data for Name: person_shop; Type: TABLE DATA; Schema: public; Owner: coursework
--

COPY person_shop (person_id, shop_id) FROM stdin;
\.


--
-- Data for Name: persons; Type: TABLE DATA; Schema: public; Owner: coursework
--

COPY persons (person_id, last_name, first_name, second_name, date_of_birth, sex, place_of_birth, address, phone, photo, passport, position_id) FROM stdin;
16	test	test	test	2016-11-01	M	test	test	test	\N	(1001,666666)	1
26	kekkek	kek	lol	2016-11-14	M	asdasdasd	ololo st.	8123812313	\N	(5710,635585)	1
30	lol	ololo	asdasd	2016-11-14	M	place	address	phone	\N	(6666,666666)	1
32	123	123	123	2016-01-01	M	adsads	asdasd	adasd	\N	(5716,478596)	1
\.


--
-- Name: persons_person_id_seq; Type: SEQUENCE SET; Schema: public; Owner: coursework
--

SELECT pg_catalog.setval('persons_person_id_seq', 38, true);


--
-- Data for Name: position_salary; Type: TABLE DATA; Schema: public; Owner: coursework
--

COPY position_salary (position_id, shop_id, salary) FROM stdin;
1	1	100
\.


--
-- Data for Name: positions; Type: TABLE DATA; Schema: public; Owner: coursework
--

COPY positions (position_id, name, description) FROM stdin;
1	test	test desc
3	name	desc
\.


--
-- Name: positions_position_id_seq; Type: SEQUENCE SET; Schema: public; Owner: coursework
--

SELECT pg_catalog.setval('positions_position_id_seq', 3, true);


--
-- Data for Name: product_amounts; Type: TABLE DATA; Schema: public; Owner: coursework
--

COPY product_amounts (product_id, shop_id, amount, updated) FROM stdin;
9	1	54	2016-11-01
\.


--
-- Name: product_amounts_product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: coursework
--

SELECT pg_catalog.setval('product_amounts_product_id_seq', 1, false);


--
-- Data for Name: product_prices; Type: TABLE DATA; Schema: public; Owner: coursework
--

COPY product_prices (product_id, shop_id, price, updated) FROM stdin;
\.


--
-- Name: product_prices_product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: coursework
--

SELECT pg_catalog.setval('product_prices_product_id_seq', 1, false);


--
-- Data for Name: product_types; Type: TABLE DATA; Schema: public; Owner: coursework
--

COPY product_types (type_id, title, description) FROM stdin;
1	test	asdasd
\.


--
-- Name: product_types_type_id_seq; Type: SEQUENCE SET; Schema: public; Owner: coursework
--

SELECT pg_catalog.setval('product_types_type_id_seq', 2, true);


--
-- Data for Name: products; Type: TABLE DATA; Schema: public; Owner: coursework
--

COPY products (product_id, name, type_id) FROM stdin;
9	test product	1
11	asdasdasdas	1
\.


--
-- Name: products_product_id_seq; Type: SEQUENCE SET; Schema: public; Owner: coursework
--

SELECT pg_catalog.setval('products_product_id_seq', 11, true);


--
-- Data for Name: sell_log; Type: TABLE DATA; Schema: public; Owner: coursework
--

COPY sell_log (log_id, product_id, shop_id, amount, date) FROM stdin;
4	9	1	100	2016-11-01
5	9	1	40	2016-11-01
10	9	1	1	2016-11-15
\.


--
-- Name: sell_log_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: coursework
--

SELECT pg_catalog.setval('sell_log_log_id_seq', 10, true);


--
-- Data for Name: shops; Type: TABLE DATA; Schema: public; Owner: coursework
--

COPY shops (shop_id, street) FROM stdin;
1	olololo
3	kekekeke
5	2343
9	uuu
\.


--
-- Name: shops_shop_id_seq; Type: SEQUENCE SET; Schema: public; Owner: coursework
--

SELECT pg_catalog.setval('shops_shop_id_seq', 19, true);


--
-- Name: newtable_pk; Type: CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY sell_log
    ADD CONSTRAINT newtable_pk PRIMARY KEY (log_id);


--
-- Name: passport_key; Type: CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY persons
    ADD CONSTRAINT passport_key UNIQUE (passport);


--
-- Name: person_schedule_pkey; Type: CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY person_schedule
    ADD CONSTRAINT person_schedule_pkey PRIMARY KEY (sched_id);


--
-- Name: person_shop_pkey; Type: CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY person_shop
    ADD CONSTRAINT person_shop_pkey PRIMARY KEY (person_id);


--
-- Name: persons_pkey; Type: CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY persons
    ADD CONSTRAINT persons_pkey PRIMARY KEY (person_id);


--
-- Name: position_salary_pkey; Type: CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY position_salary
    ADD CONSTRAINT position_salary_pkey PRIMARY KEY (position_id, shop_id);


--
-- Name: positions_pkey; Type: CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY positions
    ADD CONSTRAINT positions_pkey PRIMARY KEY (position_id);


--
-- Name: product_amounts_pkey; Type: CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY product_amounts
    ADD CONSTRAINT product_amounts_pkey PRIMARY KEY (product_id, shop_id);


--
-- Name: product_prices_pkey; Type: CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY product_prices
    ADD CONSTRAINT product_prices_pkey PRIMARY KEY (product_id, shop_id);


--
-- Name: product_types_pkey; Type: CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY product_types
    ADD CONSTRAINT product_types_pkey PRIMARY KEY (type_id);


--
-- Name: products_pkey; Type: CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY products
    ADD CONSTRAINT products_pkey PRIMARY KEY (product_id);


--
-- Name: shops_pkey; Type: CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY shops
    ADD CONSTRAINT shops_pkey PRIMARY KEY (shop_id);


--
-- Name: streets_uniq_key; Type: CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY shops
    ADD CONSTRAINT streets_uniq_key UNIQUE (street);


--
-- Name: title_uniq_key; Type: CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY product_types
    ADD CONSTRAINT title_uniq_key UNIQUE (title);


--
-- Name: fki_pa_product_id; Type: INDEX; Schema: public; Owner: coursework
--

CREATE INDEX fki_pa_product_id ON product_amounts USING btree (product_id);


--
-- Name: fki_pa_shop_id; Type: INDEX; Schema: public; Owner: coursework
--

CREATE INDEX fki_pa_shop_id ON product_amounts USING btree (shop_id);


--
-- Name: fki_person_id; Type: INDEX; Schema: public; Owner: coursework
--

CREATE INDEX fki_person_id ON person_schedule USING btree (person_id);


--
-- Name: fki_position_id; Type: INDEX; Schema: public; Owner: coursework
--

CREATE INDEX fki_position_id ON persons USING btree (position_id);


--
-- Name: fki_pp_product_id; Type: INDEX; Schema: public; Owner: coursework
--

CREATE INDEX fki_pp_product_id ON product_prices USING btree (product_id);


--
-- Name: fki_pp_shop_id; Type: INDEX; Schema: public; Owner: coursework
--

CREATE INDEX fki_pp_shop_id ON product_prices USING btree (shop_id);


--
-- Name: fki_product_type; Type: INDEX; Schema: public; Owner: coursework
--

CREATE INDEX fki_product_type ON products USING btree (type_id);


--
-- Name: fki_ps_shop_id; Type: INDEX; Schema: public; Owner: coursework
--

CREATE INDEX fki_ps_shop_id ON position_salary USING btree (shop_id);


--
-- Name: fki_shop_id; Type: INDEX; Schema: public; Owner: coursework
--

CREATE INDEX fki_shop_id ON person_shop USING btree (shop_id);


--
-- Name: fki_sl_product_id; Type: INDEX; Schema: public; Owner: coursework
--

CREATE INDEX fki_sl_product_id ON sell_log USING btree (product_id);


--
-- Name: fki_sl_shop_id; Type: INDEX; Schema: public; Owner: coursework
--

CREATE INDEX fki_sl_shop_id ON sell_log USING btree (shop_id);


--
-- Name: shop_uniq; Type: INDEX; Schema: public; Owner: coursework
--

CREATE UNIQUE INDEX shop_uniq ON shops USING btree (lower(street));


--
-- Name: add_log_trigger; Type: TRIGGER; Schema: public; Owner: coursework
--

CREATE TRIGGER add_log_trigger BEFORE INSERT ON sell_log FOR EACH ROW EXECUTE PROCEDURE tf_add_log();


--
-- Name: add_person_trigger; Type: TRIGGER; Schema: public; Owner: coursework
--

CREATE TRIGGER add_person_trigger BEFORE INSERT ON persons FOR EACH ROW EXECUTE PROCEDURE tf_add_person();


--
-- Name: add_position_trigger; Type: TRIGGER; Schema: public; Owner: coursework
--

CREATE TRIGGER add_position_trigger BEFORE INSERT ON positions FOR EACH ROW EXECUTE PROCEDURE tf_add_position();


--
-- Name: add_product_trigger; Type: TRIGGER; Schema: public; Owner: coursework
--

CREATE TRIGGER add_product_trigger BEFORE INSERT ON products FOR EACH ROW EXECUTE PROCEDURE tf_add_product();


--
-- Name: add_product_type_trigger; Type: TRIGGER; Schema: public; Owner: coursework
--

CREATE TRIGGER add_product_type_trigger BEFORE INSERT ON product_types FOR EACH ROW EXECUTE PROCEDURE tf_add_product_type();


--
-- Name: add_schedule_trigger; Type: TRIGGER; Schema: public; Owner: coursework
--

CREATE TRIGGER add_schedule_trigger BEFORE INSERT ON person_schedule FOR EACH ROW EXECUTE PROCEDURE tf_add_schedule_id();


--
-- Name: add_shop_trigger; Type: TRIGGER; Schema: public; Owner: coursework
--

CREATE TRIGGER add_shop_trigger BEFORE INSERT ON shops FOR EACH ROW EXECUTE PROCEDURE tf_add_shop();


--
-- Name: update_log_trigger; Type: TRIGGER; Schema: public; Owner: coursework
--

CREATE TRIGGER update_log_trigger BEFORE INSERT OR UPDATE ON sell_log FOR EACH ROW EXECUTE PROCEDURE tf_update_log();


--
-- Name: update_person_trigger; Type: TRIGGER; Schema: public; Owner: coursework
--

CREATE TRIGGER update_person_trigger BEFORE INSERT OR UPDATE ON persons FOR EACH ROW EXECUTE PROCEDURE tf_update_person();


--
-- Name: update_position_salary_trigger; Type: TRIGGER; Schema: public; Owner: coursework
--

CREATE TRIGGER update_position_salary_trigger BEFORE INSERT OR UPDATE ON position_salary FOR EACH ROW EXECUTE PROCEDURE tf_update_position_salary();


--
-- Name: update_product_amount_trigger; Type: TRIGGER; Schema: public; Owner: coursework
--

CREATE TRIGGER update_product_amount_trigger BEFORE INSERT OR UPDATE ON product_amounts FOR EACH ROW EXECUTE PROCEDURE tf_update_log();


--
-- Name: update_product_price_trigger; Type: TRIGGER; Schema: public; Owner: coursework
--

CREATE TRIGGER update_product_price_trigger BEFORE INSERT OR UPDATE ON product_prices FOR EACH ROW EXECUTE PROCEDURE tf_update_product_price();


--
-- Name: fk_position_id; Type: FK CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY position_salary
    ADD CONSTRAINT fk_position_id FOREIGN KEY (position_id) REFERENCES positions(position_id) MATCH FULL ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fk_position_id; Type: FK CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY persons
    ADD CONSTRAINT fk_position_id FOREIGN KEY (position_id) REFERENCES positions(position_id) MATCH FULL ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fk_product_id; Type: FK CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY product_amounts
    ADD CONSTRAINT fk_product_id FOREIGN KEY (product_id) REFERENCES products(product_id) MATCH FULL ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fk_product_id; Type: FK CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY product_prices
    ADD CONSTRAINT fk_product_id FOREIGN KEY (product_id) REFERENCES products(product_id) MATCH FULL ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fk_product_id; Type: FK CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY sell_log
    ADD CONSTRAINT fk_product_id FOREIGN KEY (product_id) REFERENCES products(product_id) MATCH FULL ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fk_shop_id; Type: FK CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY sell_log
    ADD CONSTRAINT fk_shop_id FOREIGN KEY (shop_id) REFERENCES shops(shop_id) MATCH FULL ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fk_shop_id; Type: FK CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY product_amounts
    ADD CONSTRAINT fk_shop_id FOREIGN KEY (shop_id) REFERENCES shops(shop_id) MATCH FULL ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fk_shop_id; Type: FK CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY person_shop
    ADD CONSTRAINT fk_shop_id FOREIGN KEY (shop_id) REFERENCES shops(shop_id) MATCH FULL ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fk_shop_id; Type: FK CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY position_salary
    ADD CONSTRAINT fk_shop_id FOREIGN KEY (shop_id) REFERENCES shops(shop_id) MATCH FULL ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fk_shop_id; Type: FK CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY product_prices
    ADD CONSTRAINT fk_shop_id FOREIGN KEY (shop_id) REFERENCES shops(shop_id) MATCH FULL ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: fk_type_id; Type: FK CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY products
    ADD CONSTRAINT fk_type_id FOREIGN KEY (type_id) REFERENCES product_types(type_id) MATCH FULL ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: ttt; Type: FK CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY person_shop
    ADD CONSTRAINT ttt FOREIGN KEY (person_id) REFERENCES persons(person_id) ON DELETE CASCADE;


--
-- Name: ttt; Type: FK CONSTRAINT; Schema: public; Owner: coursework
--

ALTER TABLE ONLY person_schedule
    ADD CONSTRAINT ttt FOREIGN KEY (person_id) REFERENCES persons(person_id) ON DELETE CASCADE;


--
-- Name: public; Type: ACL; Schema: -; Owner: coursework
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM coursework;
GRANT ALL ON SCHEMA public TO coursework;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

