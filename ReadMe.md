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

浏览器控制台：
````
let jm=function sendHeartbeat() {
                fetch('http://localhost:1111/hb') // 发送GET请求到第一个路径
                .then(response => response.text()) // 获取响应体文本
                .then(body => {
                    if (body !== '') {
                    // 如果响应体不为空
                    const originalStr = body; // 原始字符串
                    const encryptedStr = imexamplevar(originalStr); //这里进行处理 输出 加密后的字符串
                    const url = `http://localhost:1111/enc?key=${encodeURIComponent(originalStr)}&value=${encodeURIComponent(encryptedStr)}`; // 构建第二个路径的URL
                    return fetch(url); // 发送GET请求到第二个路径
                    } else {
                    // 如果响应体为空，直接返回一个resolved的Promise对象
                    return Promise.resolve();
                    }
                })
                .then(response => {
                    if (response && response.ok) {
                    console.log('Data sent successfully');
                    } else {
                    console.log('Failed to send data');
                    }
                })
                .catch(error => {
                    console.log('Error:', error);
                });
            }
    setInterval(jm, 500);
````

使用教程：
https://www.yuque.com/august-wyz6t/gf2vao/ruqmihpa7v37i9yz