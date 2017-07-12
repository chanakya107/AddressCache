package memcache.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class AddressCacheTest {

    private AddressCache cache;

    @Before
    public void setup() {
        cache = new AddressCache(1, TimeUnit.SECONDS);
    }

    @After
    public void tearDown() {
        cache = null;
    }

    @Test
    public void testAddShouldBeAbleToAddElement() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        InetAddress inetAddress1 = InetAddress.getByName("127.0.0.2");

        assertTrue(cache.add(inetAddress));
        assertTrue(cache.add(inetAddress1));

        assertEquals(2, cache.size());
    }

    @Test
    public void testAddDoesNotAddDuplicateElements() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");

        cache.add(inetAddress);
        assertEquals(1, cache.size());

        cache.add(inetAddress);
        assertEquals(1, cache.size());
    }

    @Test
    public void testRemoveShouldBeAbleToRemoveElement() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");

        cache.add(inetAddress);

        assertTrue(cache.remove(inetAddress));
        assertEquals(0, cache.size());
    }

    @Test
    public void testRemoveRemovesSpecifiedElement() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        InetAddress inetAddress1 = InetAddress.getByName("127.0.0.2");

        cache.add(inetAddress);
        cache.add(inetAddress1);
        assertEquals(2, cache.size());

        cache.remove(inetAddress);
        assertEquals(1, cache.size());
    }

    @Test
    public void testRemoveReturnsFalseIfSpecifiedElementIsNotPresent() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        InetAddress inetAddress1 = InetAddress.getByName("127.0.0.2");

        cache.add(inetAddress);

        assertFalse(cache.remove(inetAddress1));
        assertEquals(1, cache.size());
    }

    @Test
    public void testPeekReturnsNullIfNoElements() throws UnknownHostException {

        InetAddress peekedElement = cache.peek();

        assertNull(peekedElement);
    }

    @Test
    public void testPeekReturnsLastAddedElement() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        InetAddress inetAddress1 = InetAddress.getByName("127.0.0.2");

        cache.add(inetAddress);
        cache.add(inetAddress1);

        InetAddress peekedElement = cache.peek();
        assertEquals("127.0.0.2", peekedElement.getHostName());
    }

    @Test
    public void testPeekReturnsLastAddedElementAndDoesNotRemoveIt() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        InetAddress inetAddress1 = InetAddress.getByName("127.0.0.2");

        cache.add(inetAddress);
        cache.add(inetAddress1);

        InetAddress peekedElement = cache.peek();
        assertEquals("127.0.0.2", peekedElement.getHostName());
        assertEquals(2, cache.size());
    }

    @Test
    public void testPeekReturnsNullIfLastAddedElementIsRemovedAlready() throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        InetAddress inetAddress1 = InetAddress.getByName("127.0.0.2");

        cache.add(inetAddress);
        cache.add(inetAddress1);

        cache.remove(inetAddress1);
        InetAddress peekedElement = cache.peek();
        assertNull(peekedElement);
        assertEquals(1, cache.size());
    }

    @Test
    public void testTakeReturnsLastAddedElementAndRemovesIt() throws UnknownHostException, InterruptedException {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        InetAddress inetAddress1 = InetAddress.getByName("127.0.0.2");

        cache.add(inetAddress);
        cache.add(inetAddress1);

        InetAddress removedElement = cache.take();
        assertEquals("127.0.0.2", removedElement.getHostName());
        assertEquals(1, cache.size());
    }

    @Test
    public void testElementsAreClearedAfterMaxAge() throws UnknownHostException, InterruptedException {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        InetAddress inetAddress1 = InetAddress.getByName("127.0.0.2");

        cache.add(inetAddress);
        Thread.sleep(500);

        cache.add(inetAddress1);
        assertEquals(2, cache.size());

        Thread.sleep(900);
        assertEquals(1, cache.size());
        Thread.yield();
    }

    @Test
    public void testTakeWaitsForElementIfLastAddedElementIsNotPresent() throws UnknownHostException, InterruptedException {
        InetAddress inetAddress = InetAddress.getByName("127.78.76.1");
        InetAddress inetAddress1 = InetAddress.getByName("127.78.76.2");

        Thread t1 = new Thread(() -> cache.take());
        t1.start();

        Thread.sleep(5);
        assertEquals(Thread.State.WAITING, t1.getState());

        Thread t2 = new Thread(() -> {
            cache.add(inetAddress);
            cache.add(inetAddress1);
        });
        t2.start();
        Thread.sleep(100);

        assertEquals(1, cache.size());
        Thread.yield();
    }
}