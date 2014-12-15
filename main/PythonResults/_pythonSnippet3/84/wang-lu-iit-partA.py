import requests

def main():
    # requests
    s = requests.session()
    s.headers['X-TechChallege'] = 'true'
    
    res = s.get('http://2014.fallchallenge.org', headers={'X-TechChallenge':'true'})
    
    print(res)
    print(len(res.content))
    
    fp = open('wang-lu-iit-partA-result.gz', mode='wb')
    fp.write(res.content)
    fp.close()

if __name__ == '__main__':
    main()