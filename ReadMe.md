直接上代码，感谢使用：

JAVA：
````
HttpServer server = HttpServer.create(new InetSocketAddress(8009), 0);
server.createContext("/1", l->{
InputStream inputStream = l.getRequestBody();
BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
StringBuilder stringBuilder = new StringBuilder();
String line;
while ((line = reader.readLine()) != null) {
stringBuilder.append(line);
}
reader.close();
String requestBody = stringBuilder.toString();
//在这里使用requestBody并给resText赋值
String resText=null;
l.sendResponseHeaders(200, resText.length());
l.getResponseBody().write(resText.getBytes());
l.getResponseBody().close();
});
server.setExecutor(null); // creates a default executor
server.start();
````

PYTHON:
````
from http.server import BaseHTTPRequestHandler, HTTPServer

class L(BaseHTTPRequestHandler):
    def do_POST(self):
        self.send_response(200)
        self.end_headers()
        content_length = int(self.headers['Content-Length'])
        body = self.rfile.read(content_length).decode('utf-8')
        #在这里使用body并给resText赋值
        resText = None
        self.wfile.write(resText.encode('utf-8'))

server_address = ('0.0.0.0', 8000)
httpd = HTTPServer(server_address, L)
httpd.serve_forever()
````