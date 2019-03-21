# Reactive Mysql with Jasync and R2DBC 

Hi Spring fans! Welcome to another super short mid-season interregnum installment of Spring Tips in which I introduce a *super* preliminary / not-GA version of an R2DBC binding for MySQL by the Jasync SQL project. 

If you want to spin up a version of MySQL for your local machine, then you can try this:

```
docker run --name=orders-mysqldb -d -p 3306:3306 -e MYSQL_ROOT_HOST=% -e MYSQL_ROOT_PASSWORD=root -e MYSQL_USER=orders -e MYSQL_PASSWORD=orders -e MYSQL_DATABASE=orders mysql/mysql-server:5.7.24
```

If you want to get dropped into the container and work against the MySQL database instance, use:

```
docker exec -it orders-mysqldb bash 
```

You can access the container from your host machine like this: 

```
mysql -u orders -h 127.0.0.1 -p orders
```
