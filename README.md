# Code Design Practice - Bookstore

## About Code Design Practice

This project is one of a series of projects focused on code design practices I've been adopting to develop web backend
applications. The goal is to:

1. Improve fluency in writing these types of applications
2. Document the decisions and why they were made
3. Have clear guidelines to follow when developing new applications, mainly for myself, but also as a suggestion to 
others, e.g., teammates that might find some value in the practices described here

## About the Bookstore

The Bookstore is a web application that allows users to browse and purchase books. Is a basic application, and 
it will fit nicely as the first of the series, showing the code design practices been applied in one of the most simple
scenarios.

For the technologies here I'm using the Kotlin + Spring Boot stack with a relational database. It is a widespread 
stack found in enterprise applications, and I'm using it to learn more about it too. Although the code design guidelines
are not specific to this stack.

## The Guidelines

I'll try my best to not attach the guidelines to only Object-Oriented Programming, although I'm using a language here
that supports it; the point is to make guidelines that are general enough to be applicable to any paradigm. The only
real focus here is on backend web applications.

The order of the guidelines is not important as well.

### 1 – Maximize Cohesion

This is a way of thinking about code organization. Where to put new constructs? The idea is that to maximize the 
cohesion of the code, everything conceptually tied together should be very close to each other. And the more one
concept is close to another, the closer they should be in the project. For example:

- If there are three classes related to the concept of purchase, they should, at least, be together in a `purchase`
package.
- If a behavior acts on data of only one given class, for example, the behavior should be written in that class.

### 2 – Protect the borders of the system as if there is no tomorrow


