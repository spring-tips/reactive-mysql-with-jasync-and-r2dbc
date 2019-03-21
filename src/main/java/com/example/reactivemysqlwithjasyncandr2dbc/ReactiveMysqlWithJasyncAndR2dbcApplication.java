package com.example.reactivemysqlwithjasyncandr2dbc;

import com.github.jasync.r2dbc.mysql.JasyncConnectionFactory;
import com.github.jasync.sql.db.mysql.pool.MySQLConnectionFactory;
import com.github.jasync.sql.db.mysql.util.URLParser;
import io.r2dbc.spi.ConnectionFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.nio.charset.StandardCharsets;

@Log4j2
@SpringBootApplication
public class ReactiveMysqlWithJasyncAndR2dbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveMysqlWithJasyncAndR2dbcApplication.class, args);
	}

	@Bean
	ApplicationRunner run(OrderRepository repository) {
		return args -> repository.findAll().subscribe(log::info);
	}
}

@Table("orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
class Order {

	@Id
	private Long id;
	private String fn;
}

interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
}


@Configuration
@EnableR2dbcRepositories
class MysqlApplicationConfiguration extends AbstractR2dbcConfiguration {

	@Override
	public ConnectionFactory connectionFactory() {
		String url = "mysql://orders:orders@127.0.0.1:3306/orders";
		return new JasyncConnectionFactory(new MySQLConnectionFactory(
			URLParser.INSTANCE.parseOrDie(url, StandardCharsets.UTF_8)));

	}
}


