# Providentia
A web-based bench marking tool for testing query speeds of JanusGraph vs other DBMS in the context of spatio-temporal data.

## Project Structure

* Client is based on Angular6+.
* Flask is used to communicate with the client and the Python backend is used for analysis and machine learning.
* Spring REST controller communicates with Flask backend and queries the database.