package websocket.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@WebSocket(maxTextMessageSize = 64 * 1024)
public class WebSocketClientImpl {

	private final CountDownLatch closeLatch;

	private Session session;

	public WebSocketClientImpl() {
		this.closeLatch = new CountDownLatch(1);
	}

	public boolean isClosed() {
		return this.session == null;
	}

	public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
		return this.closeLatch.await(duration, unit);
	}

	@OnWebSocketClose
	public void onClose(int statusCode, String reason) {
		System.out.println("closed");
		this.session = null;
		this.closeLatch.countDown();
	}

	@OnWebSocketConnect
	public void onConnect(Session session) {
		System.out.println("connected");
		this.session = session;
	}

	@OnWebSocketMessage
	public void onMessage(String msg) {
		System.out.println("receive msg:" + msg);

		JSONObject msgJson = JSONObject.parseObject(msg);
		String msgId = msgJson.getString("id");
		String type = "feedback";

		Map<String, String> params = new HashMap<String, String>();
		params.put("msgId", msgId);
		params.put("type", type);
		try {
			session.getRemote().sendString(JSON.toJSONString(params));
		} catch (IOException e) {
			System.out.println("消息反馈失败");
			e.printStackTrace();
		}
	}

	@OnWebSocketError
	public void onError(Throwable cause) {
		System.out.println("error");
		cause.printStackTrace();
		this.session = null;
		this.closeLatch.countDown();
	}

}