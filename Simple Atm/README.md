# Client Server application

This application is made for academic purposes for the needs of lesson "Distributed Systems". It is a simple ATM application, where a user can do the followings:

  - Authenticate himself (for now it is simple, with no db)
  - Withdraw money from his account
  - Deposit money
  - Display the balance 
  - and EXIT the application.
  
The server supports Multithreading.

To test it, download all java file into the same folder, complile them all
```sh
$ javac *.java
```
To start the server choose a PORT number (e.g 20000)
```sh
$ java Server PORT
```
And to run the client add localhost and the port number that the server listens to. You can run as many clients as you wish. The server is multithreaded.
```sh
$ java Client localhost PORT
```


