package org.ansj.app.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import love.cq.util.IOUtil;
import love.cq.util.StringUtil;

import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.util.MyStaticValue;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;

/**
 * http 服务类
 * 
 * @author wanggang
 *
 */
@SuppressWarnings("restriction")
public class AnsjServer {

	private final static String WARM_UP_WORD = "你好";

	private static final String FILE_ENCODING = System.getProperty("file.encoding");

	public void startServer(int serverPort) throws Exception {
		MyStaticValue.LIBRARYLOG.info("starting ansj http server");
		HttpServerProvider provider = HttpServerProvider.provider();
		HttpServer httpserver = provider.createHttpServer(new InetSocketAddress(serverPort), 100);// 监听端口6666,能同时接
																									// 受100个请求
		httpserver.createContext("/", new AnsjHttpHandler());
		httpserver.setExecutor(null);
		httpserver.start();
		System.out.println("server started");

	}

	private static class AnsjHttpHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange httpExchange) {
			try {
				String path = httpExchange.getRequestURI().getPath();
				if (path != null && path.startsWith("/page")) {
					writeToClient(httpExchange, readFileToString(path));
					return;
				}

				String responseMsg = "欢迎使用Ansj中文分词!\ndemo:http://localhost:8888/?input=我日世界&method=to"; // 响应信息
				Map<String, String> paramers = parseParamers(httpExchange);
				String input = paramers.get("input");
				String method = paramers.get("method");
				String nature = paramers.get("nature");
				if (StringUtil.isNotBlank(input)) {
					responseMsg = AnsjServlet.processRequest(input, method, nature);
				}

				writeToClient(httpExchange, responseMsg);

			} catch (Exception e) {
				e.printStackTrace();
				try {
					writeToClient(httpExchange, e.getMessage());
				} catch (IOException e1) {
					System.out.println("eee");
				}

			} finally {
				httpExchange.close();
			}
		}

		private String readFileToString(String path) {
			InputStream resourceAsStream = null;
			try {
				resourceAsStream = this.getClass().getResourceAsStream(path);
				resourceAsStream.available();
				return IOUtil.getContent(resourceAsStream, IOUtil.UTF8);
			} catch (Exception e) {
				return String.valueOf("发生了错误 :404 文件找不到鸟！");
			} finally {
				if (resourceAsStream != null) {
					try {
						resourceAsStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		private void writeToClient(HttpExchange httpExchange, String responseMsg) throws IOException {
			byte[] bytes = responseMsg.getBytes();
			httpExchange.sendResponseHeaders(200, bytes.length); // 设置响应头属性及响应信息的长度
			OutputStream out = httpExchange.getResponseBody(); // 获得输出流
			out.write(bytes);
			out.flush();
		}

		private Map<String, String> parseParamers(HttpExchange httpExchange) throws UnsupportedEncodingException,
				IOException {
			BufferedReader reader = null;
			try {
				Map<String, String> parameters = new HashMap<String, String>();
				URI requestedUri = httpExchange.getRequestURI();
				String query = requestedUri.getRawQuery();
				// get 请求解析
				parseQuery(query, parameters);
				// post 请求解析
				reader = IOUtil.getReader(httpExchange.getRequestBody(), FILE_ENCODING);
				query = IOUtil.getContent(reader).trim();
				parseQuery(query, parameters);
				httpExchange.setAttribute("parameters", parameters);
				return parameters;
			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		}

		/**
		 * 从get请求中解析参数
		 * 
		 * @param query
		 * @param parameters
		 */
		private void parseQuery(String query, Map<String, String> parameters) {
			if (StringUtil.isBlank(query)) {
				return;
			}
			String split[] = query.split("\\?");
			query = split[split.length - 1];
			split = query.split("&");
			String[] param = null;
			String key = null;
			String value = null;
			for (String kv : split) {
				try {
					param = kv.split("=");
					if (param.length == 2) {
						key = URLDecoder.decode(param[0], FILE_ENCODING);
						value = URLDecoder.decode(param[1], FILE_ENCODING);
						parameters.put(key, value);
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public static void main(String[] args) throws Exception {
		if (args == null || args.length < 1) {
			System.err.println("Usage: AnsjServer <serverPort> so by default 8888");
			args = new String[] { "8888" };
		}
		/* warm up ansj engine */
		/* FIXME: dirty hack here... */
		NlpAnalysis.parse(WARM_UP_WORD);
		/* set up server */
		int serverPort = Integer.valueOf(args[0]);
		new AnsjServer().startServer(serverPort);
	}

}
