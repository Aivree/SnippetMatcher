import yaml, json, requests

src_url = 'https://raw.github.com/unitedstates/congress-legislators/master/legislators-current.yaml'

data = yaml.safe_load(requests.get(src_url).content)

with open('congress.json', 'w') as f:
    f.write(json.dumps(data))