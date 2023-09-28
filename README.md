# Tickets

"Tickets", a demo web app for ticket management such as IT support. Spring Boot &amp; Spring JPA (w/ PostgreSQL) and Spring Security for the backend, VueJS w/ Vuetify on the frontend.

I made this application mainly to practice Spring Boot and learn Vuetify. 

A list of some of its features:

- Login by OAuth2 (Spring Security) with Google.
- User roles of "User", "Support" and "Admin".
- Ticket creation, viewing, assignment, comments, and replies (to other comments).
- Separate paginated table views with bi-directional sort by columns for showing created & assigned tickets, all tickets and all users.
- A few other features.

# Notes

- `application.properties` was excluded as it contains OAuth2 keys and DB information. 
- If you want to test or use it yourself, you would have to set up your own to have a Google OAuth2 Client/Secret and PostgreSQL login and dialect.
- Frontend client is not included (yet). It will be added in soon.
