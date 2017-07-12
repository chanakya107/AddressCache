package memcache.controller;

import memcache.service.AddressCache;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.net.InetAddress;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CacheController.class)
public class CacheControllerTest {

    @MockBean
    private AddressCache addressCache;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAddIsSuccessWhenValidIPIsPassed() throws Exception {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");

        mockMvc.perform(put("/address/127.0.0.1")
                .header("x-correlation-id", "correlationId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Address added successfully"));

        verify(addressCache, times(1))
                .add(eq(inetAddress));
    }

    @Test
    public void testAddIsThrowsBadRequestWhenInValidIPIsPassed() throws Exception {

        JSONObject body = new JSONObject();
        body.put("ipAddress", "invalidAddress");

        mockMvc.perform(put("/address/invalidAddress")
                .content(body.toString())
                .header("x-correlation-id", "correlationId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Address not found for the given ip  address"));
    }

    @Test
    public void testRemoveIsSuccessWhenValidIPIsPassed() throws Exception {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        when(addressCache.remove(inetAddress)).thenReturn(true);

        mockMvc.perform(delete("/address/127.0.0.1")
                .header("x-correlation-id", "correlationId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Address removed successfully"));

        verify(addressCache, times(1))
                .remove(eq(inetAddress));
    }

    @Test
    public void testRemoveReturnsNotFoundIfTheElementIsNotPresent() throws Exception {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        when(addressCache.remove(inetAddress)).thenReturn(false);

        mockMvc.perform(delete("/address/127.0.0.1")
                .header("x-correlation-id", "correlationId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Address not present in cache"));

        verify(addressCache, times(1))
                .remove(eq(inetAddress));
    }

    @Test
    public void testRemoveIsThrowsBadRequestWhenInValidIPIsPassed() throws Exception {

        mockMvc.perform(delete("/address/invalidAddress")
                .header("x-correlation-id", "correlationId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Address not found for the given ip  address"));
    }


    @Test
    public void testPeekIsSuccessWhenElementIsPresent() throws Exception {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        when(addressCache.peek()).thenReturn(inetAddress);

        mockMvc.perform(get("/address/peek")
                .header("x-correlation-id", "correlationId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("/127.0.0.1"));

        verify(addressCache, times(1))
                .peek();
    }

    @Test
    public void testPeekIsSuccessWhenElementIsNotPresent() throws Exception {
        when(addressCache.peek()).thenReturn(null);

        mockMvc.perform(get("/address/peek")
                .header("x-correlation-id", "correlationId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Last added element not found"));

        verify(addressCache, times(1))
                .peek();
    }

    @Test
    public void testTakeIsSuccessWhenElementIsPresent() throws Exception {
        InetAddress inetAddress = InetAddress.getByName("127.0.0.1");
        when(addressCache.take()).thenReturn(inetAddress);

        mockMvc.perform(get("/address/take")
                .header("x-correlation-id", "correlationId")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("/127.0.0.1"));

        verify(addressCache, times(1))
                .take();
    }

}