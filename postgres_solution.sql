------------------------------------------------
-- Create the raw csv input table
create table raw_csv_input
(
	userid text,
	lastname text,
	firstname text,
	version integer,
	company text
);
comment on table raw_csv_input is 'Represents the raw CSV input table';


------------------------------------------------
-- Import the CSV with Postgres handy COPY command TODO: parameterize the filename
COPY raw_csv_input FROM '/Users/josh/proj/csv-demo/example.csv' with CSV HEADER;

------------------------------------------------
-- Filter out lower versions with a self-join
SELECT a.* FROM raw_csv_input a LEFT OUTER JOIN raw_csv_input b ON a.userid = b.userid AND a.version < b.version WHERE b.userid IS NULL;


CREATE OR REPLACE FUNCTION process_csv() RETURNS integer AS $$
DECLARE
  company RECORD;
BEGIN
  RAISE NOTICE 'Processing CSV input...';
  COPY raw_csv_input FROM '/Users/josh/proj/csv-demo/example.csv' with CSV HEADER;

  FOR company IN
    SELECT DISTINCT company from raw_csv_input
  LOOP
    RAISE NOTICE 'Writing company file %.csv...', quote_ident(company);
    EXECUTE format('COPY ' ||
                   '(SELECT a.* FROM raw_csv_input a LEFT OUTER JOIN raw_csv_input b ON a.userid = b.userid AND a.version < b.version WHERE b.userid IS NULL AND company IS %' ||
                   'ORDER BY TRIM(lastname) ASC TRIM(firstname) ASC)' ||
                   'TO /Users/josh/proj/csv-demo/target/%.csv WITH FORMAT CSV', quote_ident(company), quote_ident(company));
  END LOOP;

  RAISE NOTICE 'Done writing CSV files.';
  RETURN 1;
END;
$$ LANGUAGE plpgsql;

select from process_csv()
