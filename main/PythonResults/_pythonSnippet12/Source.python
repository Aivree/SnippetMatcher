jobDict = defaultdict(list)
    def set_default(obj):
        # trickyness needed here via import to know type
        if isinstance(obj, RowProxy):
            return list(obj)
        raise TypeError


    jsonEncoded = json.dumps(jobDict, default=set_default)