"""
A set of useful dictionary manipulation template tags
"""
import json
from django import template

register = template.Library()

@register.filter()
def to_json(d):
    """
    Converts a dictionary into the a json string

    :param d: The dictionary to convert
    :return: The json string
    """
    return json.dumps(d)