#!/usr/bin/env python3

key = "ЬЦЭЬШХНПЮШЕЛЭИЩС АХРРФЫУИРЪГ"
alph = [
    "А",
    "Б",
    "В",
    "Г",
    "Д",
    "Е",
    "Ж",
    "З",
    "И",
    "Й",
    "К",
    "Л",
    "М",
    "Н",
    "О",
    "П",
    "Р",
    "С",
    "Т",
    "У",
    "Ф",
    "Х",
    "Ц",
    "Ч",
    "Ш",
    "Щ",
    "Ъ",
    "Ы",
    "Ь",
    "Э",
    "Ю",
    "Я",
    " ",
];

first_len = 4
second_len = 7

f = [0] * first_len
s = [0] * second_len

# for c in key:
    # print(c + " " + str(alph.index(c)))

# s[0] = int(sys.argv[1])

# print(s[0])

s[0] = 12

f[0] = 28-s[0]
f[1] = 20-s[0]
f[2] = 25-s[0]
f[3] = 15-s[0]

print(str(s[0]) + " " + ''.join(map((lambda x: alph[x]), f)))

s[0] = 28-f[0]
s[1] = 22-f[1]
s[2] = 29-f[2]
s[3] = 28-f[3]
s[4] = 24-f[0]
s[5] = 21-f[1]
s[6] = 13-f[2]

print(''.join(map((lambda x: alph[x]), s)))

#K1.1+K2.1 = Ь 28
#K1.2+K2.2 = Ц 22
#K1.3+K2.3 = Э 29
#K1.4+K2.4 = Ь 28
#K1.1+K2.5 = Ш 24
#K1.2+K2.6 = Х 21
#K1.3+K2.7 = Н 13
#K1.4+K2.1 = П 15
#K1.1+K2.2 = Ю 30
#K1.2+K2.3 = Ш 24
#K1.3+K2.4 = Е 5
#K1.4+K2.5 = Л 11
#K1.1+K2.6 = Э 29
#K1.2+K2.7 = И 8
#K1.3+K2.1 = Щ 25
#K1.4+K2.2 = С 17
#K1.1+K2.3 =   32
#K1.2+K2.4 = А 0
#K1.3+K2.5 = Х 21
#K1.4+K2.6 = Р 16
#K1.1+K2.7 = Р 16
#K1.2+K2.1 = Ф 20
#K1.3+K2.2 = Ы 27
#K1.4+K2.3 = У 19
#K1.1+K2.4 = И 8
#K1.2+K2.5 = Р 16
#K1.3+K2.6 = Ъ 26
#K1.4+K2.7 = Г 3
