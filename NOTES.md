
###### drop dev database

    psql secret_hitler -c "DROP SCHEMA public CASCADE;CREATE SCHEMA public;"

###### generate Slick classes from local DB
     
 sbt > console >


    slick.codegen.SourceCodeGenerator.main(Array("slick.driver.PostgresDriver", "org.postgresql.Driver", "jdbc:postgresql://localhost:5432/secret_hitler", "app/TablesGen", "", "manu", ""))
