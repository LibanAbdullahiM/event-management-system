[![CircleCI](https://dl.circleci.com/status-badge/img/gh/LibanAbdullahiM/event-management-system/tree/main.svg?style=svg)](https://dl.circleci.com/status-badge/redirect/gh/LibanAbdullahiM/event-management-system/tree/main)

# event-management-system

A Rest-API-based Event Management System that allows users to explore events, read event details, and register for
participation.

----

# Features

* User registration and authentication
* Browse available events
* View detailed event information
* Register for events
* Event creation and management

----

# System Overview

The system allows users to discover events and register for them, while organizers can create and manage events.

Core components of the system include:

* Users – People who browse and register for events
* Events – Activities available for registration 
* Registrations – Records linking users to events

----

# System Architecture

The system follows a typical RESTful architecture.

Client → REST API → Business Logic → Database

Example flow:

User → API Request → Controller → Service → Repository → Database

----

# Core Entities
##  User
Represents a system user.

---
## Event
Represents an event that users can attend.

---
## Registration
Represents a user's registration for an event.

---
# API Endpoints
# Events
    GET /api/events
    Retrieve all events

    GET /api/events/event_id
    Retrieve event details

    POST /api/events
    Create new event

    PUT /api/events/event_id/edit
    Update an event

    DELETE /api/events/event_id/delete
    Delete an event

----
# Registrations
    GET /api/user_id/registrations
    Retrieve user registrations

    POST /api/user_id/registrations
    Register a user for an event

    DELETE /api/user_id/registrations/registration_id/delete
    Cancel registration

----
# Database Design
Main tables:
* users 
* events 
* registrations 
* roles 
* privileges

----
# Technologies
Example technologies used in this project:

### Backend
* Java / Spring 
* Rest API

### Database 
* PostgreSQL

### Tools
* Git 
* Github 
* Postman

-----

# Future Improvements
* Event search and filtering 
* Pagination for events 
* Event capacity management 
* Email notifications

-----

# Author
### Liban A M
