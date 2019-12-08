"""
This module contains the analysis result data for this web application. Each module below acts as a repository for
the data in its respective table.
"""

from providentia.repository import tbl_analysis
from providentia.repository import tbl_benchmark
from providentia.repository import tbl_databases
from providentia.repository import tbl_datasets
from providentia.repository import tbl_queries
from providentia.repository.analysis_tables import tbl_kate, tbl_review_trends, tbl_city_sentiment

__all__ = ['tbl_analysis', 'tbl_benchmark', 'tbl_databases', 'tbl_datasets', 'tbl_queries', 'tbl_kate',
           'tbl_review_trends', 'tbl_city_sentiment']
