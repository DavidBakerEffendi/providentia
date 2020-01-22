from flask import current_app

from providentia.db.this import get_db
from providentia.models import sim1_decoder, Sim1

TABLE = 'sim1'
COLUMNS = ("id", "benchmark_id", "avg_ttas", "avg_tth")


def get_results(benchmark_id):
    with current_app.app_context():
        cur = get_db().cursor()
        query = "SELECT id, benchmark_id, avg_ttas, avg_tth " \
                "FROM {} WHERE benchmark_id = %s::uuid".format(TABLE)

        cur.execute(query, (benchmark_id,))

        rows = []
        if cur.rowcount > 0:
            for row in cur.fetchall():
                rows.append(dict(zip(COLUMNS, row)))
        else:
            return None

        return [sim1_decoder(row) for row in rows]


def insert(sim: Sim1):
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
        if sim.avg_ttas is not None:
            insert_into += "{}, ".format(COLUMNS[2])
            values += "%s, "
            values_arr.append(sim.avg_ttas)
        if sim.avg_tth is not None:
            insert_into += "{}, ".format(COLUMNS[3])
            values += "%s, "
            values_arr.append(sim.avg_tth)

        insert_into = insert_into[:-2] + ") "
        values = values[:-2] + ");"

        # execute and commit
        db = get_db()
        query = db.cursor().mogrify(insert_into + values, values_arr)
        db.cursor().execute(query, values_arr)
        db.commit()
