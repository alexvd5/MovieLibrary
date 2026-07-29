Movie Library API - Technical Documentation
1. External Rating API
   For fetching external movie ratings, I chose the OMDb API ([https://www.omdbapi.com/](https://www.omdbapi.com/)).

Why OMDb? It's super straightforward to use and returns basic movie details 
(like IMDb rating, plot, release year) with a simple HTTP request using a movie title and an API key.

Endpoint used: GET /?t={title}&apikey={apiKey}

2. Authentication & Authorization
   Security is implemented using Spring Security:

Authentication: Handled via HTTP Basic Auth.

Authorization / Roles:

Public Access: Swagger UI endpoints (/swagger-ui/**, /v3/api-docs/**) are allowed for everyone 
(.permitAll()) so the API can be tested easily.

USER / ADMIN: GET requests (viewing movies) can be done by both regular users and admins.

ADMIN only: Operations that modify data (POST, PUT, DELETE) require ADMIN role.

3. Asynchronous Enrichment
   To avoid making the user wait for the external API call when adding or updating a movie, the rating enrichment runs asynchronously:

When a POST /api/movies request comes in, the movie is saved to the database immediately and returns a 201 Created response.

In the background, a @Async method is triggered.

This background process makes an HTTP call to the OMDb API, gets the rating, and updates the movie record in the DB.

This keeps our REST API fast and responsive.

4. Architectural Decisions & Trade-offs
   Global Exception Handling (@RestControllerAdvice):
   Instead of wrapping every single controller method in try-catch blocks, I created a central GlobalExceptionHandler. 
It catches custom exceptions like EntityNotFoundException (404) or DuplicateEntityException (409) and 
returns structured JSON error responses.

Async vs Sync for External API:

Choice: Asynchronous integration.

Trade-off: The response is fast, but the external rating might take a second or two to appear 
in the database after creation (eventual consistency).

Resilience:
If the OMDb API goes down or fails, the core app still works and movies can still be 
created—only the external rating will be skipped or retried later.