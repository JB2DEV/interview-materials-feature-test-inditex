# Business Case

There's a business need that requires offering an asset upload feature for several of our internal tools which basically implies: 

    1. Managing the file upload process, which implies receiving the file and delegating the upload to the internal publisher. 
    2. Allowing the user to search the uploaded assets (against the persisted information in this system).

## Considerations
- See the attached openapi.yml for the API spec.
- The upload process must be done in an async manner.
- This implementation is not the "final resting place" of the uploaded assets, just a proxy that keeps information of it and acts as a middleman for the upload.

## Requirements
- Java 17/21.
- Spring Boot.
- HTTP/REST.
- Unit & integration tests.
- Fully functional (compiles, starts locally).
- Hexagonal design (You can go modular or package based, no worries on that). 
- Implement whichever defined filter you see fit, no need to implement them all but sorting is a must.
- Persistence.

## Optional
- Implement all filters.
- Dockerized.
- Reactive (WebFlux). 
- Virtual Threads.
- Resilient.
- Postman collection.
- Security.

## To be covered during the interview
- Which improvements would you do to the API definition?
- How would you include paging?

