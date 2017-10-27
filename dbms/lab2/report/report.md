---
number: 2
subject: Системы управления базами данных
author:
	- Айтуганов Д. А.
	- Чебыкин И. Б.
inspector:
	- Афанасьев Д. Б.
name: Вариант 835
header-includes:
	- \usepackage{verbatim}
---

# Цель работы

Цель работы - сконфигурировать базу данных Oracle на выделенном сервере.
В процессе конфигурации БД необходимо пользоваться только интерфейсом командной
строки и утилитой SQLPlus; использовать графический установщик нельзя.

# Задание

Порядок конфигурации БД:

* Задать значения необходимых для конфигурации переменных окружения.

* Задать метод аутентификации администратора (зависит от варианта).

* Создать конфигурационные файлы, необходимые для инициализации и запуска экземпляра Oracle.

* Запустить экземпляр Oracle.

* Создать новую базу данных (параметры конфигурации зависят от варианта).

* Создать дополнительные табличные пространства (определяются вариантом).

* Сформировать представления словаря данных.

* Параметры конфигурации Oracle:

Имя узла
: db135.

Точка монтирования
: /u01/bcu71.

SID
: s100000, где s100000 - ID студента.

Метод аутентификации администратора БД
: файл.

Имя БД
: loudoven.

Размер блока данных
: 8192 байт.

Размер SGA
: 600 МБ.

Кодировка
: UTF-8.

Файлы данных табличного пространства SYSTEM
: \$ORADATA/node02/enofe98.dbf.

Файлы данных табличного пространства SYSAUX
: \$ORADATA/node02/gew78.dbf.

Файлы данных табличного пространства USERS
: \$ORADATA/node01/ixayuyu664.dbf.

__Файлы данных дополнительных табличных пространств__

__NICE_ORANGE_BIRD__

* \$ORADATA/node03/niceorangebird01.dbf.
* \$ORADATA/node03/niceorangebird02.dbf.
* \$ORADATA/node01/niceorangebird03.dbf.

__NICE_ORANGE_MEAT__

* \$ORADATA/node03/niceorangemeat01.dbf.
* \$ORADATA/node03/niceorangemeat02.dbf.
* \$ORADATA/node03/niceorangemeat03.dbf.


# Скрипты

\begin{scriptsize}
\verbatiminput{../src/env}
\end{scriptsize}

## create db
\begin{scriptsize}
\verbatiminput{../src/create-db.sh}
\end{scriptsize}

## sqlplus

\begin{scriptsize}
\verbatiminput{../src/create-db.sql}
\end{scriptsize}

#### inits191954.ora

\begin{scriptsize}
\verbatiminput{../src/inits191954.ora}
\end{scriptsize}
