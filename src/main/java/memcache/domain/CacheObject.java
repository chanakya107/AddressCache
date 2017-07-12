package memcache.domain;

import java.net.InetAddress;

public class CacheObject {
    public long createdTime = System.currentTimeMillis();
    public InetAddress inetAddress;

    public CacheObject(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }
}