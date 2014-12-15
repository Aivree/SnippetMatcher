#!/usr/bin/env python
import requests


def main():
    url = "http://www.python.org"
    r = requests.get(url)

    # write the content to test_request.html
    with open("test_request.html", "wb") as code:
        code.write(r.content)


if __name__ == "__main__":
    main()
