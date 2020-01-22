from flask import current_app

from providentia.db.this import get_db
from providentia.models import sim2_decoder, Sim2

TABLE = 'sim2'
COLUMNS = ("id", "benchmark_id", "p1", "p2", "p3")


def get_results(benchmark_id):
    with current_app.app_context():
        cur = get_db().cursor()
        query = "SELECT id, benchmark_id, p1, p2, p3 " \
                "FROM {} WHERE benchmark_id = %s::uuid".format(TABLE)

        cur.execute(query, (benchmark_id,))

        rows = []
        if cur.rowcount > 0:
            for row in cur.fetchall():
                rows.append(dict(zip(COLUMNS, row)))
        else:
            return None

        return [sim2_decoder(row) for row in rows]


def insert(sim: Sim2):
    with current_app.app_context():
        insert_into = "INSERT INTO {} (".format(TABLE)
        values = "VALUES ("
        values_arr = []

        if sim.id is not None:
            insert_into += "{}, ".format(COLUMNS[0])
            values += "%s::uuid, "
            values_arr.append(sim.id)
        if sim.benchmark is not None:
            insert_into += "{}, ".format(COLUMNS[1])
            values += "%s::uuid, "
            values_arr.append(sim.benchmark.benchmark_id)
        if sim.p1 is not None:
            insert_into += "{}, ".format(COLUMNS[2])
            values += "%s, "
            values_arr.append(sim.p1)
        if sim.p2 is not None:
            insert_into += "{}, ".format(COLUMNS[3])
            values += "%s, "
            values_arr.append(sim.p2)
        if sim.p3 is not None:
            insert_into += "{}, ".format(COLUMNS[4])
            values += "%s, "
            values_arr.append(sim.p3)

        insert_into = insert_into[:-2] + ") "
        values = values[:-2] + ");"

        # execute and commit
        db = get_db()
        query = db.cursor().mogrify(insert_into + values, values_arr)
        db.cursor().execute(query, values_arr)
        db.commit()
