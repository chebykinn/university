---
number: 3
subject: Системы управления базами данных
author:
	- Айтуганов Д. А.
	- Чебыкин И. Б.
inspector:
	- Афанасьев Д. Б.
name: Вариант 405
header-includes:
	- \usepackage{verbatim}
---

# Цель работы

Цель работы - настроить процедуру периодического резервного копирования базы
данных, сконфигурированной в ходе выполнения лабораторной работы №2.
В процессе конфигурации процедуры резервного копирования по-прежнему необходимо
пользоваться только интерфейсом командной строки и утилитой SQLPlus;
использовать графические утилиты нельзя.

# Задание

В процессе выполнения работы необходимо создать резервную копию БД, настроить
процесс репликации, и осуществить процедуру восстановления БД с резервной копии.

Репликацию необходимо организовать посредством периодического применения на
реплике изменений из журнала повторов "оригинала".

Требования к настройке резервного копирования:

- Вся логика сервиса, осуществляющего репликацию БД, должна быть реализована
в виде shell-скриптов.
- Необходимо реализовать задачу для планировщика cron, осуществляющую
периодический (например, раз в час) запуск скрипта репликации.
- Каталог, в котором будет создаваться резервная копия экземпляра Oracle,
выбирается на усмотрение студента.
- Для того, чтобы можно было продемонстрировать корректность работы
репликации, тестовая база не должна быть пустой. Т.е. предварительно в ней
нужно создать тестовые таблицы и заполнить их тестовыми данными, осуществив
несколько транзакций.

# Скрипты

### Изменения в env

\begin{scriptsize}
\verbatiminput{../src/env}
\end{scriptsize}

### Изменения в inits191954.ora

\begin{scriptsize}
\verbatiminput{../src/inits191954.ora}
\end{scriptsize}

### inits191954_rep.ora

\begin{scriptsize}
\verbatiminput{../src/inits191954_rep.ora}
\end{scriptsize}

### Скрипт создания резервной копии через rman

\begin{scriptsize}
\verbatiminput{../src/backup.sh}
\end{scriptsize}

### Скрипт восстановления из резервной копии

\begin{scriptsize}
\verbatiminput{../src/recover.sh}
\end{scriptsize}

### Скрипт создания реплики

\begin{scriptsize}
\verbatiminput{../src/create_replica.sh}
\end{scriptsize}

### Скрипт обновления реплики

\begin{scriptsize}
\verbatiminput{../src/do_replica.sh}
\end{scriptsize}

#### SQL

\begin{scriptsize}
\verbatiminput{../src/dump_log.sql}
\end{scriptsize}

# Задача cron для обновления реплики

```
0 * * * * /u01/scripts/do_replica.sh
```
