package memcache.service;

import memcache.domain.CacheObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class AddressCache {

    private final Map<String, CacheObject> cacheMap;
    private volatile String lastAddedKey = null;
    private long age;

    public AddressCache(long maxAge, TimeUnit unit) {
        this.age = MILLISECONDS.convert(maxAge, unit);
        cacheMap = new ConcurrentHashMap<>();

        if (age > 0) {
            Thread t = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    cleanup();
                }
            });

            t.setDaemon(true);
            t.start();
        }
    }

    public boolean add(InetAddress inetAddress) {
        String key = inetAddress.getHostName();
        synchronized (cacheMap) {
            cacheMap.putIfAbsent(key, new CacheObject(inetAddress));
            this.lastAddedKey = key;
            cacheMap.notify();
        }
        return true;
    }

    public boolean remove(InetAddress inetAddress) {
        String key = inetAddress.getHostName();
        CacheObject remove = cacheMap.remove(key);
        if (remove == null)
            return false;
        resetLastAddedKey(key);
        return true;
    }

    private void resetLastAddedKey(String key) {
        if (key.equals(this.lastAddedKey))
            this.lastAddedKey = null;
    }

    public InetAddress peek() {
        if (this.lastAddedKey == null)
            return null;
        CacheObject lastAddedObject = cacheMap.get(this.lastAddedKey);
        return lastAddedObject.inetAddress;
    }

    public InetAddress take() {
        synchronized (cacheMap) {
            while (this.lastAddedKey == null) {
                try {
                    cacheMap.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            CacheObject lastAddedObject = cacheMap.remove(this.lastAddedKey);
            this.lastAddedKey = null;
            return lastAddedObject.inetAddress;
        }
    }

    public int size() {
        return cacheMap.size();
    }

    private void cleanup() {
        long now = System.currentTimeMillis();
        ArrayList<String> deleteKey = new ArrayList<>(cacheMap.size());
        for (Map.Entry<String, CacheObject> entry : cacheMap.entrySet()) {
            CacheObject c = entry.getValue();
            if (c != null && (now > (this.age + c.createdTime))) {
                String key = entry.getKey();
                deleteKey.add(key);
                resetLastAddedKey(key);
            }
        }
        for (String key : deleteKey) {
            synchronized (cacheMap) {
                cacheMap.remove(key);
            }

            Thread.yield();
        }
    }
}