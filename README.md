# CashAccountsManagerBackend
Web Java (ver 26, Maven) Spring backend application for registering, managing actions from many accounts in different banks

Java (ver 26, Maven) application for registering, managing actions from many accounts in different banks. This is simple example of application using many aspects of java programming language like core keywords, polymorphism, inheritance, abstraction, IO, collections and many others.
Application hierarchy is didived into three layers:
- data (models, repositories):
  This layer has direct access to data and manages operations allowing to get, read, update and remove particular type of stored data.
  In this app we have storage for personal info data (ex. name, e-mail, phone, address) and bank accounts registry representation (ex. name, type, personal info, number, funds, balance and action records)
- services (business logic):
  This layer allows logical operations performed on data provided by data layer. It allows to access data, perform validation and handle different exceptions in case of errors
- controllers:
  REST endpoints allowing to access and process data through services 

  Application has unit and integration tests created with usage of JUnit 5

