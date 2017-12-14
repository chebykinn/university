---
number: 4
subject: Системы управления базами данных
author:
	- Айтуганов Д. А.
	- Чебыкин И. Б.
inspector:
	- Афанасьев Д. Б.
name: Вариант 676
header-includes:
	- \usepackage{verbatim}
---

# Задание

Этап 1. Сконфигурировать экземпляр Oracle ASM на выделенном сервере и настроить его на работу с базой данных, созданной при выполнении лабораторной работы №2:

Необходимо использовать тот же узел, что и в лабораторных работах №2 и №3.

Имя сервиса: ASM.100000, где 100000 - ID студента.

ASM_POWER_LIMIT: 4.

Количество дисковых групп: 2.

Имена и размерности дисковых групп: cutegoat[7], cleverdog[3].

В качестве хранилища данных (дисков) необходимо использовать файлы.
Имена файлов должны строиться по шаблону \$DISKGROUP_NAME\$X, где \$DISKGROUP_NAME --
имя дисковой группы, а \$X - порядковый номер файла в группе (нумерация начинается с нуля).

Путь к файлам ASM - /u01/\$DISKGROUP_NAME/\$DISK_FILE_NAME.

Существующие файлы БД мигрировать в хранилище ASM не нужно - ASM
должен управлять только вновь добавленными файлами.

В процессе конфигурации ASM можно пользоваться только интерфейсом командной
строки и утилитой SQLPlus; использовать графический конфигуратор нельзя.

Этап 2. Внести в конфигурацию ASM ряд изменений в приведённой ниже последовательности:

Добавить новый диск в группу cutegoat.

Пересоздать группу cutegoat, сконфигурировав в ней избыточность следующим образом:

Размер группы - 4 элементов.

Тип избыточности - NORMAL; количество failure-групп - 2.

Равномерно распределить диски по failure-группам.

Одной командой удалить дисковую группу cutegoat и добавить новую дисковую
	группу luckyowl[6]"; размер AU - 16 МБ.

Добавить новую дисковую группу interestingzebra[3]"; размер AU - 2 МБ.

Добавить новый диск в группу luckyowl.

Добавить новую дисковую группу popularshark[5]"; размер AU - 8 МБ.

Удалить дисковую группу interestingzebra.

# Скрипты

### asm_env

\begin{scriptsize}
\verbatiminput{../src/asm_env}
\end{scriptsize}

### init+ASM.191954.ora

\begin{scriptsize}
\verbatiminput{../src/init+ASM.191954.ora}
\end{scriptsize}

### common

\begin{scriptsize}
\verbatiminput{../src/common}
\end{scriptsize}

### create_disk_groups.sh

\begin{scriptsize}
\verbatiminput{../src/create_disk_groups.sh}
\end{scriptsize}

### create_spfile.sh

\begin{scriptsize}
\verbatiminput{../src/create_spfile.sh}
\end{scriptsize}

### s2_add_disk.sh

\begin{scriptsize}
\verbatiminput{../src/s2_add_disk.sh}
\end{scriptsize}

### s2_recreate_group.sh

\begin{scriptsize}
\verbatiminput{../src/s2_recreate_group.sh}
\end{scriptsize}

### s2_drop_add.sh

\begin{scriptsize}
\verbatiminput{../src/s2_drop_add.sh}
\end{scriptsize}

### s2_add_zebra.sh

\begin{scriptsize}
\verbatiminput{../src/s2_add_zebra.sh}
\end{scriptsize}
