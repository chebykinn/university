#!/usr/bin/env python3
from mysql_pump import *

source_oracle_conn = "merged/merged@chebykin.org:10200/plaza"
target_oracle_conn = "plaza/plaza@chebykin.org:10200/plaza"


def simple_merge_tables(src, tgt, table_name, columns=None):
    src_rows = src.get_rows(table_name)
    tgt_rows = tgt.get_rows(table_name)
    tgt_dict = {}
    merged_tables = {}
    for tgtr in tgt_rows:
        tgt_dict[tgtr["name"]] = tgtr
    for src_row in src_rows:
        if not columns:
            columns = src_row.keys()
        for c in src_row.keys():
            if c not in columns:
                src_row[c] = None
        src_row["id"] = tgt_dict[src_row["name"]]["id"] if src_row["name"] in tgt_dict else 0
        merged_tables[src_row["id"]] = src_row
        if src_row["name"] in tgt_dict:
            continue
        out_row = {}
        for c in columns:
            out_row[c] = src_row[c]
        new_id = tgt.insert("person", out_row)
        src_row["id"] = int(new_id)
        merged_tables[src_row["id"]] = src_row
        print(src_row)
    tgt.commit()
    return merged_tables

def get_person_id_by_participant(persons, src_row, src_column_name, tgt_column_name):
    return persons[int(src_row[src_column_name])][tgt_column_name]

def convert_table_with_person_foreign(src_table, tgt_table, uniq_key, persons, src, tgt, src_column_name, tgt_column_name):
    src_rows = src.get_rows(src_table)
    tgt_rows = tgt.get_rows(tgt_table)
    dict = {}
    for tgtr in tgt_rows:
        dict[tgtr[uniq_key]] = tgtr;
    for src_row in src_rows:
        person_id = get_person_id_by_participant(persons, src_row, src_column_name, tgt_column_name)
        if src_row[uniq_key] in dict:
            continue
        src_row[tgt_column_name] = person_id
        if src_column_name != tgt_column_name:
            src_row[src_column_name] = None
        tgt.insert(tgt_table, src_row, return_id=False)
    tgt.commit()

def copy_person(src, tgt):
    return simple_merge_tables(src, tgt, "person")

def copy_department(src, tgt):
    return simple_merge_tables(src, tgt, "department")

def copy_job(src, tgt):
    return copy_simple("job", tgt, src)

def copy_ac_group(src, tgt):
    return copy_simple("academic_group", tgt, src, idColumn='id')

def copy_program(src, tgt):
    return copy_simple("program", tgt, src)

def copy_specialty(src, tgt):
    return simple_merge_tables(src, tgt, "specialty", ["id", "name"])

def copy_class():
    convert_table_with_person_foreign("class", "class", )

def main():
    source = Oracle(conn=source_oracle_conn)
    target = Oracle(conn=target_oracle_conn)
    # copy simple tables
    # copy_person(source, target)
    # copy_department(source, target)
    # copy_job(source, target)
    # copy_ac_group(source, target)
    # copy_program(source, target)
    copy_specialty(source, target)
    copy_class()
    # insert all new persons and match mysql ids to oracle ids
    # my_orcl_persons = merge_persons(source, target)
    # using this id to id table convert all tables with foreign keys
    # convert_person_conference(my_orcl_persons, my, orcl)
    # convert_person_science_project(my_orcl_persons, my, orcl)
    # convert_publication(my_orcl_persons, my, orcl)
    # convert_reader_list(my_orcl_persons, my, orcl)


if __name__ == "__main__":
    main()
