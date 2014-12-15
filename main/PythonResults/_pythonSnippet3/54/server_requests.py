import requests
with open('dataset_3378_3.txt', "r") as inf:
    url = inf.readline()

r = requests.get(url)
with open('text.txt', 'w') as text_file:
    text_file.write(r.text)

with open('text.txt', 'r') as text_file:
    l = 0
    for line in text_file:
        l += 1

with open("res.txt", "w") as res:
    res.write(str(l))

