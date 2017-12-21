---
number: 5
subject: Системы управления базами данных
author:
	- Айтуганов Д. А.
	- Чебыкин И. Б.
inspector:
	- Афанасьев Д. Б.
name: Вариант 28321
header-includes:
	- \usepackage{verbatim}
---

# Задание

Этап 1. Сконфигурировать кластер на платформе Oracle RAC, состоящий из двух узлов, размещённых на отдельных виртуальных машинах и создать кластерную базу данных с заданными параметрами.

Особенности конфигурации кластера и БД:

Имя базы данных
: bewilderedprobe.

Имена узлов кластера
: bewilderedprobe0 и bewilderedprobe1.

В качестве хранилища необходимо использовать ASM, сконфигурированный в
результате выполнения лабораторной работы №4.

Хранилище ASM необходимо расположить на диске, доступном со всех узлов
кластера по протоколу NFS.

Этап 2. Создать тестовые таблицы с записями и произвести следующие операции с БД:

Создать нового пользователя emotionalyellowqueen.

Вывести состояние всех сессий БД, запущенных на узле bewilderedprobe0.

Вывести состояние всех сессий БД, запущенных от имени пользователя emotionalyellowqueen.

Создать нового пользователя frightenedpurplelarva.

Закрыть все сессии на всех узлах кластера, запущенные от имени пользователя emotionalyellowqueen.

Этап 3. Осуществить "внештатную" остановку узла кластера bewilderedprobe1,
проверив таким образом, что вся нагрузка будет перенесена на узел bewilderedprobe0
и целостность данных не будет нарушена.

Этап 4. Выполнить ряд операций в следующей последовательности:

Добавить новый файл OCR (Oracle Cluster Repository) по пути /share/happymedic/.
Заменить созданный на предыдущем шаге файл OCR файлом, находящимся по пути /share/jealousvulture/.
Удалить созданный файл OCR.

# Скрипты

### Создание пользователя

\begin{scriptsize}
\verbatiminput{../src/create_user.sh}
\end{scriptsize}

### Состояние сессий на узле

\begin{scriptsize}
\verbatiminput{../src/list_sessions.sh}
\end{scriptsize}

### Состояние сессий пользователя

\begin{scriptsize}
\verbatiminput{../src/list_user_sessions.sh}
\end{scriptsize}

### Закрытие сессий пользователя

\begin{scriptsize}
\verbatiminput{../src/close_sessions.sh}
\end{scriptsize}

### OCR

\begin{scriptsize}
\verbatiminput{../src/ocrconfig}
\end{scriptsize}
