import click
import psycopg2
from flask import current_app, g
from flask.cli import with_appcontext


# This is the main database where analysis results are stored. The database used for this is
# PostgreSQL.


def get_db():
    """Connect to the application's configured database. The connection
    is unique for each request and will be reused if this is called
    again.
    """
    if "db" not in g:
        if current_app.config['DEBUG']:
            # Use debug config
            g.db = psycopg2.connect(current_app.config['DATABASE_URI'])
        else:
            # Use prod config
            g.db = psycopg2.connect(current_app.config['DATABASE_URI'])

    return g.db


def close_db(e=None):
    """If this request connected to the database, close the
    connection.
    """
    db = g.pop("db", None)

    if db is not None:
        db.close()


def init_db():
    """Clear existing data and create new tables."""
    db = get_db()
    click.echo("Initializing database...")
    with current_app.open_resource("../db/schema.sql") as f:
        db.cursor().execute(f.read().decode("utf8"))
    db.commit()


def load_example_data():
    """Load example data for testing and debugging"""
    db = get_db()
    click.echo("Loading example data...")
    with current_app.open_resource("../db/example-data.sql") as f:
        db.cursor().execute(f.read().decode("utf8"))
    db.commit()


@click.command("init-db")
@with_appcontext
def init_db_command():
    """Clear existing data and create new tables."""
    init_db()
    if current_app.config['DEBUG']:
        load_example_data()
    click.echo("Initialized the database.")


def init_app(app):
    """Register database functions with the Flask app. This is called by
    the application factory.
    """
    app.teardown_appcontext(close_db)
    app.cli.add_command(init_db_command)
