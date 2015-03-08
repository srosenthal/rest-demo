## Overview
This is a demo REST API for managing a contact information for customers in a CRM-like web application. It implements "CRUD" methods, along with a method for detecting probably duplicate customers, so their contacts can be merged.

## APIs
All of the APIs operate on JSON models of a customer that look like this:

	{
		"id": "54fc20a0a82672962f2f740d",
		"firstName": "Jean-Luc",
		"lastName": "Picard",
		"email": "capt.picard@starfleet.gov"
	}

Get a specific customer:

	GET /customers/{id}

List all customers in the database:

	GET /customers

List customers that are similar to customer with a given id:

	GET /customers?likeId={id}

Create a new customer:

	POST /customers

Update an existing customer:

	PUT /customers/{id}

Delete a customer:

	DELETE /customers/{id}

## Limitations
This is only a demo. Some of the limitations that prevent this from being production-ready:

1. Authentication and authorization - not everyone should be able to access each API.
2. Separate pools of customers for different businesses or departments.
3. Input validation - check for errors (ex: all fields null) before writing to the database.

## Technologies
* Spring MVC - for implementing REST APIs.
* Spring Boot - sets up Spring without much configuration, and no XML.
* Spring Data MongoDB - ORM that requires barely any code. 
* MongoDB - runs "embedded" as part of the Gradle build, allowing for a standalone demo.

## Usage
To run the server on a machine with Java installed use:

	./gradlew run
