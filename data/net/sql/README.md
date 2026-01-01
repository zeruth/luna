# SQL Schemas

This ```sql/main``` dir contains the default schemas required in order to enable SQL based persistence.
  
They must all be ran once or the server will throw Exceptions.  
  
# Security

Please do not use [the default password](https://github.com/luna-rs/luna/blob/master/src/main/java/io/luna/util/SqlConnectionPool.java#L38) provided to create your user. __Do not__ disable the BCrypt password encryption, this is especially important when storing player data in a database.