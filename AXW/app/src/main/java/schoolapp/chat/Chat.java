package schoolapp.chat;

import com.google.gson.Gson;
import io.nats.client.*;
import io.nats.client.Message;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeoutException;

public class Chat {
    private final Logger logger = LoggerFactory.getLogger(Chat.class);

    public interface ChatCallback {
        void recv(org.json.JSONObject msgjson);
    }

    public interface SentFailedCallBack {
        void fail(PMsgType m);
    }

    class PMsgType {
        String dest;
        Messages msg;
    }

    // default timeout set to 5s;
    private int timeout = 2000;
    private boolean connected = false;

    private String token;
    private String url = Constants.DEFAULT_URL;
    // vars for sent message
    private ConcurrentLinkedQueue<PMsgType> sentMessages = new ConcurrentLinkedQueue<>();
    private Semaphore sentAvail = new Semaphore(0);
    // vars for receive Message
    private Vector<ChatCallback> callbacks = new Vector<>();
    private Vector<SentFailedCallBack> failCallbacks = new Vector<>();

    ConnectionFactory cf = null;
    Connection connection = null;
    Connection recvConn = null;

    AsyncSubscription recvSub = null;
    Thread sendThread;
    public static char encode( int i ) {
        return encodeMap[i&0x3F];
    }
    private static final char[] encodeMap = initEncodeMap();

    private static char[] initEncodeMap() {
        char[] map = new char[64];
        int i;
        for( i= 0; i<26; i++ )        map[i] = (char)('A'+i);
        for( i=26; i<52; i++ )        map[i] = (char)('a'+(i-26));
        for( i=52; i<62; i++ )        map[i] = (char)('0'+(i-52));
        map[62] = '+';
        map[63] = '/';

        return map;
    }
    public Chat(String _token, String _url) {
        byte[] input = _token.getBytes();
        int len = input.length;
        int offset = 0;
        int ptr =0;
        char[] buf = new char[((len+2)/3)*4];
        for( int i=offset; i<len; i+=3 ) {
            switch( len-i ) {
                case 1:
                    buf[ptr++] = encode(input[i]>>2);
                    buf[ptr++] = encode(((input[i])&0x3)<<4);
                    buf[ptr++] = '=';
                    buf[ptr++] = '=';
                    break;
                case 2:
                    buf[ptr++] = encode(input[i]>>2);
                    buf[ptr++] = encode(
                            ((input[i]&0x3)<<4) |
                                    ((input[i+1]>>4)&0xF));
                    buf[ptr++] = encode((input[i+1]&0xF)<<2);
                    buf[ptr++] = '=';
                    break;
                default:
                    buf[ptr++] = encode(input[i]>>2);
                    buf[ptr++] = encode(
                            ((input[i]&0x3)<<4) |
                                    ((input[i+1]>>4)&0xF));
                    buf[ptr++] = encode(
                            ((input[i+1]&0xF)<<2)|
                                    ((input[i+2]>>6)&0x3));
                    buf[ptr++] = encode(input[i+2]&0x3F);
                    break;
            }
        }
        token = new String(buf);
        if (_url != null && !_url.equals("")) {
            url = _url;
        }
    }

    public void setTimeOut(int newTimeOut) {
        timeout = newTimeOut;
    }

    public void addRecvCallBack(ChatCallback _callback) {
        callbacks.add(_callback);
    }

    public void addFailCallBack(SentFailedCallBack _callback) {
        failCallbacks.add(_callback);
    }

    public void publish(String message, String receiver) {
        Messages m = new Messages();
        m.addMessage(message);
        PMsgType p = new PMsgType();
        p.dest = receiver;
        p.msg = m;
        sentMessages.add(p);
        sentAvail.release();
    }

    public void stop() throws IOException {
        if(recvSub != null){
            recvSub.unsubscribe();
        }
        sendThread.interrupt();
        try {
            sendThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException, TimeoutException {
        // subscribe "Recv.token"
        cf = new ConnectionFactory(url);
        connection = cf.createConnection();
        recvConn = cf.createConnection();

        recvSub = connection.subscribe("Recv." + token, new MessageHandler() {
            @Override
            public void onMessage(Message msg) {
            //    Gson gson = new Gson();
                String txt = new String(msg.getData(),StandardCharsets.UTF_8);
               // Messages recvMessages = gson.fromJson(
                 //       txt,
                //        Messages.class);
                org.json.JSONObject jsonmsg = null;
                try {
                    jsonmsg = new org.json.JSONObject(txt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println(jsonmsg.toString()+callbacks.size());
                // sent ACK to caller
                try {
                    recvConn.publish(msg.getReplyTo(), "Ack".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    recvConn.close();
                    return;
                }
                // call callbacks
                for (ChatCallback e : callbacks) {
                    e.recv(jsonmsg);

                }
            }
        });
        // Sent start up signal
        Connection startupConnection = cf.createConnection();
        try {
            startupConnection.request(
                    "StartUp." + token,
                    "Start up information".getBytes(),
                    timeout);
        } catch (TimeoutException e) {
            logger.warn("Time out sending startup info");
            throw e;
        }
        startupConnection.close();
        // starting sending threads;
        sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Connection sendConn;
                try {
                    sendConn = cf.createConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                while (true) {
                    try {
                        sentAvail.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                    PMsgType sentMsg = sentMessages.poll();
                    try {
                        sendConn.request(
                                "Sent." + token + "." + sentMsg.dest,
                                sentMsg.msg.toString().getBytes(),
                                timeout
                        );
                    } catch (TimeoutException t) {
                        logger.warn("Time Out");
                        for (SentFailedCallBack sendSentFailedCallBack : failCallbacks) {
                            sendSentFailedCallBack.fail(sentMsg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        for (SentFailedCallBack sendSentFailedCallBack : failCallbacks) {
                            sendSentFailedCallBack.fail(sentMsg);
                        }
                    }
                }

            }
        });

        sendThread.start();
    }
}
