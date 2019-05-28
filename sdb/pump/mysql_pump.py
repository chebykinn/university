#!/usr/bin/env python3
import mysql.connector
import cx_Oracle

oracle_conn = "merged/merged@chebykin.org:10200/plaza"
mysql_user = "root"
mysql_password = "myitmolab"
mysql_host = "chebykin.org"
mysql_port = "10101"
mysql_db = "hibd"

class Mysql:
    def __init__(self):
        self.conn = mysql.connector.connect(host=mysql_host, port=mysql_port,
                user=mysql_user, password=mysql_password,
                database=mysql_db)
        self.cursor = self.conn.cursor(dictionary=True)

    def get_rows(self, table_name):
        query = ("SELECT * from {}".format(table_name))
        self.cursor.execute(query, ())
        table = []
        for row in self.cursor:
            table.append(row)
        return table


def makeDictFactory(cursor):
    columnNames = [d[0].lower() for d in cursor.description]
    def createRow(*args):
        return dict(zip(columnNames, args))
    return createRow

class Oracle:
    def __init__(self, conn=oracle_conn):
        self.conn = cx_Oracle.connect(conn, encoding='utf-8')

    def get_rows(self, table_name):
        cursor = self.conn.cursor()
        query = ("SELECT * from {}".format(table_name))
        cursor.execute(query, ())
        cursor.rowfactory = makeDictFactory(cursor)
        table = []
        for row in cursor:
            table.append(row)
        cursor.close()
        return table

    def insert(self, table_name, row, return_id=True, id_names=["id"]):
        cursor = self.conn.cursor()
        out_columns = []
        for c in row.keys():
            out_columns.append('"{}"'.format(c.upper()))
        columns = ",".join(out_columns)
        placeholders = []
        for i in range(0, len(row)):
            placeholders.append(":{}".format(i + 2))
        placeholders_str = ", ".join(placeholders)
        onDuplicate = []
        for c in row.keys():
            if c not in id_names:
                onDuplicate.append('"{}" = src."{}"'.format(c.upper(), c.upper()))
        onDuplicateStr = ",".join(onDuplicate)

        vals = None
        out_id = None
        if return_id:
            out_id = cursor.var(cx_Oracle.NUMBER)
            stmt = "insert into \"{}\" ({}) values({}) returning id into :{} ".format(
                    table_name.upper(), columns,
                    placeholders_str, len(row) + 2,
                    onDuplicateStr)
            vals = tuple(row.values()) + (out_id,);
        else:
            stmt = "insert into \"{}\" ({}) values({}) on duplicate key update set {}".format(
                    table_name.upper(), columns,
                    placeholders_str,
                    onDuplicateStr)
            vals = tuple(row.values());
        print(stmt)
        print(row.values())
        cursor.execute(stmt, vals)
        if return_id:
            new_id = out_id.getvalue()
            print(new_id)
        cursor.close()
        if return_id:
            return new_id

    def commit(self):
        pass
# self.conn.commit()


def copy_simple(table_name, my, orcl, remap=None, columns=None, idColumn='name'):
    rows = my.get_rows(table_name)
    orcl_rows = orcl.get_rows(table_name)
    orcl_dict = {}
    for olr in orcl_rows:
        orcl_dict[olr[idColumn]] = olr
    for my_row in rows:
        for c in my_row.keys():
            if columns and c not in columns:
                my_row[c] = None
        if my_row[idColumn] in orcl_dict:
            continue
        if remap:
            for column in remap:
                my_row[column["to"]] = my_row[column["from"]]
                my_row.pop(column["from"], None)
        orcl.insert(table_name, my_row)
        print(my_row)
    orcl.commit()

def get_person_id_by_participant(my_orcl_persons, my_row):
    return my_orcl_persons[int(my_row["participant_id"])]["person_id"]

def convert_many_to_many(my_table, orcl_table, first_key_part, my_orcl_persons, my, orcl):
    my_rows = my.get_rows(my_table)
    orcl_rows = orcl.get_rows(orcl_table)
    orcl_dict = {}
    for olr in orcl_rows:
        orcl_dict["{}:{}".format(olr[first_key_part], olr["person_id"])] = olr;
    for my_row in my_rows:
        person_id = get_person_id_by_participant(my_orcl_persons, my_row)
        key = "{}:{}".format(my_row[first_key_part],person_id)
        if key in orcl_dict:
            continue
        out_row = {}
        out_row["person_id"] = person_id;
        out_row[first_key_part] = my_row[first_key_part]
        orcl.insert(orcl_table, out_row, return_id=False)
    orcl.commit()

def convert_table_with_one_foreign(my_table, orcl_table, uniq_key, \
        my_orcl_persons, my, orcl):
    my_rows = my.get_rows(my_table)
    orcl_rows = orcl.get_rows(orcl_table)
    orcl_dict = {}
    for olr in orcl_rows:
        orcl_dict[olr[uniq_key]] = olr;
    for my_row in my_rows:
        person_id = get_person_id_by_participant(my_orcl_persons, my_row)
        if my_row[uniq_key] in orcl_dict:
            continue
        my_row["person_id"] = person_id
        my_row.pop("participant_id", None)
        orcl.insert(orcl_table, my_row, return_id=False)
    orcl.commit()


def convert_conference(my, orcl):
    copy_simple("conference", my, orcl, [{"from": "date", "to": "event_date"}])

def convert_science_project(my, orcl):
    copy_simple("science_project", my, orcl, [])

def merge_persons(my, orcl, source_table_name='person'):
    my_rows = my.get_rows(source_table_name)
    orcl_rows = orcl.get_rows("person")
    orcl_dict = {}
    merged_persons = {}
    for olr in orcl_rows:
        orcl_dict[olr["name"]] = olr;
    for my_row in my_rows:
        my_row["person_id"] = orcl_dict[my_row["name"]]["id"] if my_row["name"] in orcl_dict else 0
        merged_persons[my_row["id"]] = my_row
        if my_row["name"] in orcl_dict:
            continue
        out_row = {}
        out_row["name"] = my_row["name"]
        new_id = orcl.insert("person", out_row)
        my_row["person_id"] = int(new_id)
        merged_persons[my_row["id"]] = my_row
        print(my_row)
    orcl.commit()
    return merged_persons

def convert_person_conference(my_orcl_persons, my, orcl):
    convert_many_to_many("conference_participant", "person_conference",
            "conference_id", my_orcl_persons, my, orcl)

def convert_person_science_project(my_orcl_persons, my, orcl):
    convert_many_to_many("participant_science_project", "person_science_project",
            "science_project_id", my_orcl_persons, my, orcl)

def convert_publication(my_orcl_persons, my, orcl):
    convert_table_with_one_foreign("publication", "publication", "name", \
            my_orcl_persons, my, orcl)

def convert_reader_list(my_orcl_persons, my, orcl):
    convert_table_with_one_foreign("reader_list", "reader_list", "title", \
            my_orcl_persons, my, orcl)

def main():
    orcl = Oracle()
    my = Mysql()
    # copy simple tables
    convert_conference(my, orcl)
    convert_science_project(my, orcl)
    # insert all new persons and match mysql ids to oracle ids
    my_orcl_persons = merge_persons(my, orcl, source_table_name="participant")
    # using this id to id table convert all tables with foreign keys
    convert_person_conference(my_orcl_persons, my, orcl)
    convert_person_science_project(my_orcl_persons, my, orcl)
    convert_publication(my_orcl_persons, my, orcl)
    convert_reader_list(my_orcl_persons, my, orcl)


if __name__ == "__main__":
    main()
