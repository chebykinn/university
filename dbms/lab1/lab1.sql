CREATE OR REPLACE PROCEDURE S191940.GENERATE_DATE_COLUMNS(v_target_table IN VARCHAR2)
AUTHID CURRENT_USER AS
  V_Q                 VARCHAR2(1) := chr(39);
  v_index             NUMBER(3);
  v_is_valid          NUMBER(1);
  v_owner             VARCHAR2(30);
  v_table_name        VARCHAR2(30);
  v_tables_count      NUMBER(10);
  v_alter_statement   VARCHAR2(4000);
  v_update_statement  VARCHAR2(4000);
  v_old_column_name   VARCHAR2(30);
  v_new_column_name   VARCHAR2(35);

  v_generated_cols_count  NUMBER(10) := 0;
  v_integer_cols_count    NUMBER(10) := 0;
  BEGIN
    SELECT NVL2(REGEXP_SUBSTR(v_target_table, '^(("[^"]+"|[[:alnum:]_$#]+)\.)?("[^"]+"|[[:alnum:]_$#]+)$'), 1, 0) INTO v_is_valid FROM DUAL;

    IF v_is_valid = 0 THEN
      dbms_output.put_line('unable to resolve target table name');
    ELSE
      v_table_name := REGEXP_SUBSTR(v_target_table, '("[^"]+"|[[:alnum:]_$#]+)$');
      v_owner := REGEXP_SUBSTR(v_target_table, '^("[^"]+"|[[:alnum:]_$#]+)\.');

      v_index := INSTR(v_owner, '"');
      IF v_owner IS NULL THEN
        v_owner := SYS_CONTEXT('userenv', 'current_schema');
      ELSIF v_index != 0 THEN
        v_owner := REGEXP_REPLACE(v_owner, '^"|"\.$');
      ELSE
        v_owner := UPPER(v_owner);
      END IF;

      v_index := INSTR(v_table_name, '"');
      IF (v_index != 0) THEN
        v_table_name := REPLACE(v_table_name, '"');
      ELSE
        v_table_name := UPPER(v_table_name);
      END IF;

      SELECT count(1) INTO v_tables_count FROM all_all_tables WHERE owner = v_owner AND table_name = v_table_name;
      IF v_tables_count != 1 THEN
        dbms_output.put_line('unable to identify table: ' || v_target_table);
        RETURN;
      END IF;

      FOR v_row IN (
        SELECT column_name, constraint_type
        FROM all_tab_columns
        LEFT OUTER JOIN (
            SELECT owner, table_name, column_name, constraint_type FROM all_constraints
            LEFT OUTER JOIN all_cons_columns USING(owner, constraint_name, table_name)
            WHERE constraint_type = 'P'
        ) USING(owner, table_name, column_name)
        WHERE
          owner = v_owner
            AND table_name = v_table_name
            AND data_type = 'NUMBER'
            AND data_scale = 0
      ) LOOP
        v_integer_cols_count := v_integer_cols_count + 1;

        IF v_row.constraint_type IS NULL THEN
          v_generated_cols_count := v_generated_cols_count + 1;
          v_old_column_name := '"' || v_row.column_name || '"';
          v_new_column_name := '"' || v_row.column_name || '_DATE"';
          v_alter_statement := v_alter_statement || ' ADD ' || v_new_column_name || ' DATE';
          v_update_statement := v_update_statement || ', ' || v_new_column_name || ' = TO_DATE('
                                  || V_Q || '1970-01-01' || V_Q || ',' || V_Q || 'YYYY-MM-DD' || V_Q ||
                                ') + NUMTODSINTERVAL(' || v_old_column_name || ',' || V_Q || 'SECOND' || V_Q || ') ';
        END IF;
      END LOOP;
      v_update_statement := ltrim(v_update_statement,',');

      IF v_generated_cols_count > 0 THEN
        EXECUTE IMMEDIATE 'ALTER TABLE ' || v_target_table || v_alter_statement;
        EXECUTE IMMEDIATE 'UPDATE ' || v_target_table || ' SET' || v_update_statement;
      END IF;
      dbms_output.put_line('Таблица: ' || v_target_table);
      dbms_output.put_line('Целочисленных столбцов: ' || v_integer_cols_count);
      dbms_output.put_line('Столбцов добавлено: ' || v_generated_cols_count);
    END IF;
  END;
