import requests

r = requests.get('http://search-s10:8983/solr/xwiki/select', params={'wt':'json', 'q':'lang_s:en', 'sort': 'wam_i desc', 'rows':'5000', 'fl':'id'})
ids = [doc['id'] for doc in r.json()['response']['docs']]

with open('top5k.txt', 'w') as f:
    f.write('\n'.join(ids))
