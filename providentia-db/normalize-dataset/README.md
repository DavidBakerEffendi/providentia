# normalize-dataset

The following script preprocesses the dataset and performs feature selection. This has three functionalities which are configurable under `config.py`. Each step has to be performed in order to properly perform the next.

## Feature Selection and Normalization

This takes the raw Yelp Challenge Dataset and converts it to conventional JSON while discarding unnecessary features.

## Generating a Subset

Given a percentage of the dataset, a subset of the dataset will be generated. This percentage applies to the businesses and users. The reviews which are present and connect users and businesses in the given subset are then added.

## Converting to CSV

Since TigerGraph's bulk offline loader uses CSV, is it necessary to convert the data to CSV. This is to be done before calling `../docker-containers/tigergraph/transfer_data`.
