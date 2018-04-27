---
number: 4
type: Домашняя работа
subject: Конструкторско-техническое обеспечение производства ЭВМ
author:
    - Чебыкин И. Б.
inspector:
    - Поляков В. И.
---


\begin{tikzpicture}[->,>=stealth',shorten >=1pt,auto,node distance=4cm,
                    semithick, text width=1.5cm]
\footnotesize
\node[state] (life)                                                      {Довольство жизнью};
\node[state] (health)   [right of=life,         xshift=0,yshift=0]       {Здоровье};
\node[state] (family)   [below of=health,       xshift=0,yshift=0]       {Отношения};
\node[state] (flat)     [below left of=life,    xshift=0,yshift=0]       {Собственное жилье};
\node[state] (car)      [below left of=flat,    xshift=0,yshift=0]       {Личный автомобиль};
\node[state] (time)     [below of=car,          xshift=0,yshift=0]       {Свободное время};
\node[state] (party)    [below of=time,         xshift=0,yshift=0]       {Развлечения};
\node[state] (money)    [below right of=family, xshift=0,yshift=0]       {Деньги};
\node[state] (friends)  [below right of=health, xshift=0,yshift=0]       {Друзья};
\node[state] (usuccess) [right of=money,        xshift=0,yshift=0]       {Успехи в учебе};
\node[state] (wsuccess) [below left of=money,   xshift=0,yshift=0]       {Успехи в работе};
\node[state] (utime)    [below of=usuccess,     xshift=0,yshift=0]       {Время на учебу};
\node[state] (wtime)    [below of=wsuccess,     xshift=0,yshift=0]       {Время на работу};

\path (flat)         edge [right]                node [very near start] {+}          (life)
      (car)          edge [bend left=30,right]   node [very near start] {+}          (life)
      (party)        edge [right]                node [very near start] {+}          (life)
      (wsuccess)     edge [right]                node [very near start] {+}          (life)
      (usuccess)     edge [right]                node [very near start] {+}          (life)
      (family)       edge [bend left=5,right]    node [very near start] {+}          (life)
      (health)       edge [bend left=5,right]    node [very near start] {+}          (life)
      (friends)      edge [bend left=5,right]    node [very near start] {+}          (life)

      (life)         edge [bend left=5,right]    node [near start]      {+}          (family)
      (life)         edge [bend left=5,right]    node [very near start] {+}          (friends)
      (life)         edge [bend left=5,right]    node [midway]          {+}          (health)

      (car)          edge [bend left=5,right]    node [very near start] {-}          (money)
      (flat)         edge [bend left=5,right]    node [very near start] {-}          (money)

      (money)        edge [right]                node [very near start] {+}          (health)
      (money)        edge [right]                node [very near start] {+}          (family)
      (money)        edge [bend left=5,right]    node [very near start] {+}          (flat)
      (money)        edge [bend left=5,right]    node [very near start] {+}          (car)

      (usuccess)     edge [right]                node [very near start] {+}          (money)
      (wsuccess)     edge [right]                node [very near start] {+}          (money)
      (party)        edge [right]                node [very near start] {-}          (money)

      (friends)      edge [right]                node [very near start] {+}          (usuccess)

      (utime)        edge [right]                node [very near start] {+}          (usuccess)
      (utime)        edge [bend left=10,right]   node [near start]      {-}          (time)
      (utime)        edge [right]                node [near start]      {-}          (party)
      (utime)        edge [bend left=5,right]    node [very near start] {-}          (wtime)

      (wtime)        edge [right]                node [very near start] {+}          (wsuccess)
      (wtime)        edge [right]                node [very near start] {-}          (time)
      (wtime)        edge [right]                node [very near start] {-}          (party)
      (wtime)        edge [bend left=5,right]    node [very near start] {-}          (utime)

      (time)         edge [right]                node [very near start] {+}          (party)

      (car)          edge [right]                node [very near start] {+}          (time);


\end{tikzpicture}
