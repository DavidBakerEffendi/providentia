INSERT INTO results (
    database,
    dataset,
    date_executed,
    title,
    description,
    query_time,
    analysis_time
)
VALUES
    (
        'JanusGraph',
        'Yelp',
        '2019-05-13 12:05:06',
        'Graph benchmark for Flask testing',
        'Example result for testing Flask/Angular connection and displaying of results.',
	    50432,
        110465
    ),
    (
        'Cassandra',
        'Yelp',
        '2019-05-13 13:05:06',
        'Cassandra benchmark for Flask testing',
        'Example result for testing Flask/Angular connection and displaying of results.',
	    70432,
        110265
    ),
    (
        'Postgres',
        'Yelp',
        '2019-05-13 13:30:06',
        'Postgres benchmark for Flask testing',
        'Example result for testing Flask/Angular connection and displaying of results.',
	    72432,
        110365
    );


