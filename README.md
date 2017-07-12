# Address Cache Service

This service is used to perform actions on in memory address cache.
 The actions than can be performed includes
  - Add
  - Remove
  - Peek
  - Take

  Since InetAddress does not have a public constructor the service takes IP address or hostname as its request input.  

### Starting the app locally
```
./gradlew clean build bootrun
```

#### Server Ports
> The app  runs on port :10001
> Add the server.port property in application.properties and set it to desired port number

```
server.port=10001
```

#### Running Tests
> Tests can be run using
```
./gradlew test
```

### Max Age of each entry
- Entries in the cache are checked for age every second and the ones that crossed max age are removed.
- Default max age is 5 second and it can be set to desired value by setting the following values in application.properties

```
max.age.of.entry=10
time.unit=seconds
```
- Value to max.age.of.entry is in numbers
- Value of time.unit is any one from the below without double quotes 
> DAYS  
HOURS
MICROSECONDS   
MILLISECONDS  
MINUTES  
NANOSECONDS  
SECONDS  

#### API Usage
> API documentation is present in swagger
- It can be accessed by navigating to the swagger-ui.html page and the requests can be fired from there.
- Considering the app to be running locally on 10001, Swagger can be accessed by navigating to 

```
http://localhost:10001/swagger-ui.html
```

#### Using Swagger
- Navigate to the swagger page
- All the APIs are displayed under  - List Operations
- Click on the operation desired to be performed
- For PUT(add) operation give the ip address or hostname as URL parameter. ( In the text box present )
- And click on Try it out button
- The corresponding request is fired and the response is displayed
- Similarly fill the hostname or IP address as url parameter for DELETE(remove) operation
- GET(peek and take) operations don't require anything to be entered. Just open the operation and click on try it out button