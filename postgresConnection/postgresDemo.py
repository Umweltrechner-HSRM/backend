import psycopg2

#Connect to database
conn = psycopg2.connect(
    database="exampledb",
    user="docker",
    password="docker",
    host="localhost"
)

cur = conn.cursor()

#check if table exists
def table_exists(tableName: str) -> bool:
    cur.execute(f'SELECT * from {tableName}')
    if cur.fetchone()[0]:
        return True
    return False

#create new Table
newTable = 'newTable'
if not table_exists(newTable):
    cur.execute(f"CREATE TABLE {newTable} (id serial PRIMARY KEY, date timestamp(0) not null default CURRENT_TIMESTAMP, value integer)")
    conn.commit()

#insert value to table
cur.execute("INSERT INTO newTable (value) VALUES (30)")
conn.commit()


cur.close()
conn.close()