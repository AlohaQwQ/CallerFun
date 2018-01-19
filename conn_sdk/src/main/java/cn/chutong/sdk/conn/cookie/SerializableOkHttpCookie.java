package cn.chutong.sdk.conn.cookie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpCookie;

/**
 * 序列化Cookie
 *
 * 主要功能：
 *  1.将Cookie对象输出为ObjectStream
 *  2.将ObjectStream序列化成Cookie对象
 *
 * @author shiliang.zou
 * @version 0.0.1
 */
public class SerializableOkHttpCookie implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient final HttpCookie cookie;
    private transient HttpCookie clientCookie;

    public SerializableOkHttpCookie(HttpCookie cookie) {
        this.cookie = cookie;
    }

    public HttpCookie getCookie() {
        HttpCookie bestCookie = cookie;
        if (clientCookie != null) {
            bestCookie = clientCookie;
        }
        return bestCookie;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(cookie.getName());
        out.writeObject(cookie.getValue());
        out.writeObject(cookie.getComment());
        out.writeObject(cookie.getCommentURL());
        out.writeObject(cookie.getDomain());
        out.writeObject(cookie.getPath());
        out.writeObject(cookie.getPortlist());
        out.writeBoolean(cookie.getDiscard());
        out.writeBoolean(cookie.getSecure());
        out.writeLong(cookie.getMaxAge());
        out.writeInt(cookie.getVersion());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        String name = (String) in.readObject();
        String value = (String) in.readObject();
        String comment = (String) in.readObject();
        String commentURL = (String) in.readObject();
        String domain = (String) in.readObject();
        String path = (String) in.readObject();
        String portList = (String) in.readObject();
        boolean discard = in.readBoolean();
        boolean secure = in.readBoolean();
        long maxAge = in.readLong();
        int version =  in.readInt();

        clientCookie = new HttpCookie(name, value);
        clientCookie.setComment(comment);
        clientCookie.setCommentURL(commentURL);
        clientCookie.setDomain(domain);
        clientCookie.setPath(path);
        clientCookie.setPortlist(portList);
        clientCookie.setDiscard(discard);
        clientCookie.setSecure(secure);
        clientCookie.setMaxAge(maxAge);
        clientCookie.setVersion(version);
    }

}
