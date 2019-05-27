#!/usr/bin/env python3
import json
import sys
import random
import time
from datetime import datetime,timedelta

dorms = [
    {
        "location": "пер. Вяземский, д. 5/7",
        "room_amount": 1769,
    },
    {
        "location": "ул. Ленсовета, д. 23, лит.А",
        "room_amount": 585,
    },
    {
        "location": "Альпийский пер., д.15, к. 2, лит. А",
        "room_amount": 540,
    },
    {
        "location": "ул. Белорусская, д. 6, лит. А",
        "room_amount": 921,
    },
]


def strTimeProp(start, end, format, prop):
    """Get a time at a proportion of a range of two formatted times.

    start and end should be strings specifying times formated in the
    given format (strftime-style), giving an interval [start, end].
    prop specifies how a proportion of the interval to be taken after
    start.  The returned time will be in the specified format.
    """

    stime = time.mktime(time.strptime(start, format))
    etime = time.mktime(time.strptime(end, format))

    ptime = stime + prop * (etime - stime)

    return time.strftime(format, time.localtime(ptime))


def randomDate(start, end, prop):
    return strTimeProp(start, end, '%Y-%m-%d', prop)


def gen_rooms():
    total = 0
    for d in dorms:
        total += d["room_amount"]
    rooms = []
    for i in range(0, total):
        rnd_date = randomDate("2000-01-01", "2019-01-01", random.random())
        room = {
            "room_number": str(random.randrange(1, 501)) + random.choice('абвгд'),
            "room_capacity": random.randrange(2, 5),
            "renters_amount": 0,
            "last_disinfection": rnd_date,
            "has_bugs": bool(random.getrandbits(1)),
            "warnings_amount": random.randrange(0, 4),
        }
        # room["renters_amount"] = random.randrange(0, room["room_capacity"] + 1)
        rooms.append(room)
    return rooms

def main():
    random.seed()
    data = ""
    with open(sys.argv[1], 'r') as f:
        data = f.read()
    studs = json.loads(data)
    rooms = gen_rooms()
    for stud in studs:
        room_num = 0
        dorm_num = random.randrange(0, len(dorms))
        while True:
            room_num = random.randrange(0, len(rooms))
            # print(rooms[room_num]["renters_amount"] == rooms[room_num]["room_capacity"])
            if rooms[room_num]["renters_amount"] == rooms[room_num]["room_capacity"]:
                continue
            break
        rooms[room_num]["renters_amount"] += 1
        stud["room"] = rooms[room_num]
        stud["dormitory"] = dorms[dorm_num]
        stud["has_privileges"] = bool(random.getrandbits(1))
        stud["payment_amount"] = random.randrange(300, 2000, 50)
        visiting_days = random.randrange(10, 100)
        visiting_period = []
        for i in range(0, visiting_days):
            in_st_dt = stud["start_date"].split(" ")[0]
            in_en_dt = stud["end_date"].split(" ")[0]
            st_dt = randomDate(in_st_dt, in_en_dt, random.random())
            stime = datetime.strptime(st_dt, "%Y-%m-%d")
            etime = stime + timedelta(days=1)
            en_dt = etime.strftime("%Y-%m-%d")
            period = {
                    "enter_dt": st_dt,
                    "exit_dt": en_dt
            }
            visiting_period.append(period)

        stud["visiting_period"] = visiting_period


    print(json.dumps(studs, indent=4, ensure_ascii=False))
    pass

if __name__ == '__main__':
    main()
