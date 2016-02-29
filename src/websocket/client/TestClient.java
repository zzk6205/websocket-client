package websocket.client;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import com.alibaba.fastjson.JSONObject;

public class TestClient {

	public static void main(String[] args) throws UnsupportedEncodingException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", "1234");
		params.put("platform", "chrome");
		params.put("userType", "01");

		String destUri = "ws://wfycgslx.6655.la:9081/mcenter/webSocketServer?user=" + URLEncoder.encode(JSONObject.toJSONString(params), "UTF-8");

		WebSocketClient client = new WebSocketClient();
		WebSocketClientImpl socket = new WebSocketClientImpl();
		try {
			client.start();
			client.setConnectTimeout(3000);
			URI echoUri = new URI(destUri);
			ClientUpgradeRequest request = new ClientUpgradeRequest();
			client.connect(socket, echoUri, request);
			socket.awaitClose(60, TimeUnit.SECONDS);
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			try {
				client.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
