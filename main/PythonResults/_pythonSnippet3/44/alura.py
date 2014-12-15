from lxml import html
import requests

page = requests.get('http://alura.com.br/')
tree = html.fromstring(page.text)

cursos = tree.xpath('//h2[@class="course-shortName"]/text()')

f = open('./cursos', 'w')
for curso in cursos:
    curso = curso.encode('utf-8', 'replace')
    f.write(curso + '\n')
