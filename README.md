# Tickets

"Tickets", a demo web app for ticket management such as IT support. Spring Boot &amp; Spring JPA (w/ PostgreSQL) and Spring Security for the backend, VueJS w/ Vuetify on the frontend.

I made this application mainly to practice Spring Boot and learn Vuetify. 

A list of some of its features:

- Login with OAuth2 (Spring Security) with Google.
- User roles of "User", "Support" and "Admin".
- Ticket creation, viewing, assignment, comments, and replies (to other comments).
- Separate paginated table views with bi-directional sort by columns for showing created & assigned tickets, all tickets and all users.
- A few other features such as editing the ticket, etc.

# Requirements

- Your own PostgreSQL or other SQL DB
- Google OAuth2 API Client credentials
- NPM

# How to run?

Create a `application.properties` file under `src/main/resources` and add the following:

```
spring.datasource.url = Your database URL here
spring.datasource.username = Your database username here
spring.datasource.password = Your database password here

spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect or whatever DB dialect you use

spring.jpa.hibernate.ddl-auto = update
spring.security.oauth2.client.registration.google.client-id = GOOGLE CLIENT ID HERE
spring.security.oauth2.client.registration.google.client-secret = GOGLE CLIENT SECRET HERE

spring.mvc.dispatch-options-request=true
```

Run the Spring Boot `TicketsApplication` backend in Intellij (or whatever your IDE is). 
Run the Vuetify/Vue3 app with `npm run dev` in the `frontend-client` directory through terminal.

Access the web application with your browser using the base URL `localhost:3000`. You will be prompted with a login screen using Google OAuth2 login.

To give your account a certain role like "Support" or "Admin", you will have to do programatically or by editing the DB column yourself (lame, I know).

# Notes

- `application.properties` was excluded as it contains OAuth2 keys and DB information. 
- If you want to test or use it yourself, you would have to set up your own to have a Google OAuth2 Client/Secret and PostgreSQL login and dialect.
- Some code was written hastily, this was intentional. I wrote this in under a week. Therefore some code, especially the frontend code, may not be up to "standard" as functionality was my main focus.
