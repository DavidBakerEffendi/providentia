"""
The views of the API contain all the routed paths and blueprints.
"""

from providentia.views.analysis_views import kate, review_trends

__all__ = ['kate', 'review_trends']
