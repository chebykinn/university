# -*- coding: utf-8 -*-

import http.server
import socketserver
import random
import os
from googleapiclient.discovery import build

PORT = 8080

class GameHandler(http.server.SimpleHTTPRequestHandler):
    service = build("customsearch", "v1", developerKey="AIzaSyA0T5P96XjGMIsnJVU3H69XXY85XMjCu1o")

    def write_str(self, msg):
        self.wfile.write(msg.encode('utf-8'))

    def do_GET(self):
        level = 1

        names = self.read_names(level)
        selected_name = names[random.randint(0, len(names) - 1)]
        links = self.get_links(selected_name)

        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

        if not links:
            self.write_str("ERROR: Search limit was achieved, no images in cache.")
        else:
            options = '\n'.join(["<option>{}</option>".format(n) for n in names])
            with open("index.html", "r") as template_html:
                data = template_html.read().format(links[random.randint(0, len(links) - 1)], selected_name, options)
            self.write_str(data)

    def get_links(self, name):
        if self.can_search():
            res = self.service.cse().list(
                q=name,
                cx="013511285222309850888:zu1n-1yhcms",
                searchType="image",
                num=10,
            ).execute()
            if "items" not in res:
                print("No result!")
            else:
                links = [item["link"] for item in res["items"]]
                with open("{}.txt".format(name), "w+") as cache_file:
                    cache_file.write("\n".join(links))
                return links
        else:
            if os.path.isfile("{}.txt".format(name)):
                with open("{}.txt".format(name)) as cache_file:
                    return cache_file.read().split("\n")
            else:
                print("No cache!")

    def can_search(self):
        with open("limit.txt", "r+") as limit_file:
            count = int(limit_file.read())
            can = count < 100
            if can:
                count += 1
                limit_file.seek(0, 0)
                limit_file.write("%d" % count)
            return can

    def read_names(self, level):
        if level not in range(1, 3):
            raise ValueError("Incorrect level value")

        names_count = [10, 50, 100][level - 1]

        with open("guess.txt", "r") as names_file:
            names = names_file.read().split("\n")[:names_count - 1]

        return names


def main():
    httpd = socketserver.TCPServer(("", PORT), GameHandler)
    httpd.serve_forever()


if __name__ == "__main__":
    main()
