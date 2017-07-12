package memcache.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import memcache.service.AddressCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping(value = "/address")
@Api(value = "Address Cache",
        description = "API for performing actions on in memory cache")
public class CacheController {

    private AddressCache addressCache;

    @Autowired
    public CacheController(AddressCache addressCache) {
        this.addressCache = addressCache;
    }

    @RequestMapping(value = "/{ipAddress:.+}", method = RequestMethod.PUT)
    @ApiOperation(value = "Add entry to address cache",
            notes = "Add the inet address of the given ip address",
            httpMethod = "PUT")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Address not found for the given ip  address"),
    })

    public ResponseEntity<String> add(@PathVariable String ipAddress) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            return new ResponseEntity<>("Address not found for the given ip  address", HttpStatus.BAD_REQUEST);
        }
        addressCache.add(inetAddress);
        return new ResponseEntity<>("Address added successfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/{ipAddress:.+}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Remove entry from address cache",
            notes = "Remove the ip address from address cache",
            produces = "text/plain",
            httpMethod = "DELETE")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Address not found for the given ip  address"),
            @ApiResponse(code = 404, message = "Address not present in cache"),
    })

    public ResponseEntity<String> remove(@PathVariable String ipAddress) {
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName(ipAddress);

        } catch (UnknownHostException e) {
            return new ResponseEntity<>("Address not found for the given ip  address", HttpStatus.BAD_REQUEST);
        }
        if (addressCache.remove(inetAddress))
            return new ResponseEntity<>("Address removed successfully", HttpStatus.OK);
        return new ResponseEntity<>("Address not present in cache", HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/peek", method = {RequestMethod.GET})
    @ApiOperation(value = "Get last added entry from address cache",
            notes = "Get last added entry from address cache",
            produces = "text/plain",
            httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 404, message = "Last added element not found"),
    })
    public ResponseEntity<String> peek() throws UnknownHostException {
        InetAddress peekedAddress = addressCache.peek();
        if (peekedAddress == null)
            return new ResponseEntity<>("Last added element not found", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(peekedAddress.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/take", method = {RequestMethod.GET})
    @ApiOperation(value = "Remove last added entry from address cache",
            notes = "Remove the last added address from address cache",
            produces = "text/plain",
            httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success")})
    public ResponseEntity<String> take() throws UnknownHostException {
        InetAddress address = addressCache.take();
        return new ResponseEntity<>(address.toString(), HttpStatus.OK);
    }
}
