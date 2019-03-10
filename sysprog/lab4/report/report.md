---
group: P4110
department:
number: 4
subject: Системная и программная инженерия
author:
    - Чебыкин И. Б.
inspector:
    - Пенской А. В.
---

# Описание проблем / недостатков ядра используемого стандарта (Essence Kernel).

1. Не все сущности применимы и обязательны для какого-то конкретного
проекта(например, альфа команды не всегда соответствует той, что ожидается в
Essence, или чеклисты, которые содержат вопросы, не подходящие для проверки
состояния проекта в данном случае).

2. Не всегда проект можно основывать на решениях стейкохолдеров, зачастую на
многих этапах взаимодействие с ними пропускается. Также не всегда возможно
найти всех стейкхолдеров.

# Описание предлагаемых расширений / изменений.

1. В первом случае возможно сделать некоторые из альф опциональными, т. е.
применять только те, которые необходимы в рассмотрении конкретного проекта и
пересмотреть формулировку чеклистов так чтобы не было дублирования и они были
выполнимы.

2. Пересмотрение пунктов стандарта, связанных непосредственно со
стейкхолдерами.

# Пример использования модифицированного варианта с демонстрацией преимущества.

1. В рассматриваемом проекте необходимо изменить альфы команды и методики
работы, так как над проектом работает один человек. Соответственно в альфе
команды можно объединить Seeded и Formed, а в альфе методики работы объединить
In Place и In Use, так разница в этих состояний лишь в том, что в In Use практика
используется всей командой.

2. Дать возможность не всегда ориентироваться на стейкхолдеров. В первую очередь
необходимо изменить альфу стейкхолдеров на стадиях Involved и In Agreement.
В альфе требований состояния Acceptable, Addressed и Fulfilled должны быть
зависеть не только от стейкхолдеров.

# Выводы

Несмотря на некоторые ограничения стандарта его возможно применить на
дипломном проекте, однако модель Essence недостаточно гибкая для, казалось бы,
универсального стандарта.