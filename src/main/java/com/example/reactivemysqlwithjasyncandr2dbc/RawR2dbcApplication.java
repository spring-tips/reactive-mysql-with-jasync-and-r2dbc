package com.example.reactivemysqlwithjasyncandr2dbc;

import com.github.jasync.r2dbc.mysql.JasyncConnectionFactory;
import com.github.jasync.sql.db.Configuration;
import com.github.jasync.sql.db.mysql.pool.MySQLConnectionFactory;
import com.github.jasync.sql.db.mysql.util.URLParser;
import io.r2dbc.spi.ConnectionFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
public class RawR2dbcApplication {

	public static void main(String[] args) throws Exception {
		String url = "mysql://orders:orders@127.0.0.1:3306/orders";
		Configuration configuration = URLParser.INSTANCE.parseOrDie(url, StandardCharsets.UTF_8);
		ConnectionFactory connectionFactory = new JasyncConnectionFactory(new MySQLConnectionFactory(configuration));
		DatabaseClient client = DatabaseClient.create(connectionFactory);

		Mono<Void> schemaStatement = executeStatement(client, readFileToString(new ClassPathResource("/schema.sql")));
		Mono<Void> dataStatement = executeStatement(client, readFileToString(new ClassPathResource("/data.sql")));

		Flux<Order> orderFlux = client
			.execute()
			.sql(" SELECT * FROM orders ")
			.fetch()
			.all()
			.map(RawR2dbcApplication::from);

		schemaStatement
			.thenMany(dataStatement)
			.thenMany(orderFlux)
			.subscribe(log::info);

		Thread.sleep(1000);
	}

	private static Order from(Map<String, Object> map) {
		Long id = Long.class.cast(map.get("id"));
		String fn = String.class.cast(map.get("fn"));
		return new Order(id, fn);
	}

	private static String readFileToString(Resource resource) throws IOException {
		try (Reader reader = new InputStreamReader(resource.getInputStream())) {
			return FileCopyUtils.copyToString(reader);
		}
	}

	private static String[] statementsFrom(String sql) {

		String[] lines;
		if (sql.contains(";")) {
			lines = sql.split(";");
		}
		else {
			lines = new String[]{sql};
		}
		return Arrays
			.stream(lines)
			.filter(str -> StringUtils.hasText(("" + str).trim()))
			.toArray(String[]::new);
	}

	private static Mono<Void> executeStatement(DatabaseClient client, String sql) {
		String[] statements = statementsFrom(sql);
		return Flux
			.fromArray(statements)
			.flatMap(s -> client.execute().sql(s).then())
			.then();
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Order {

		private Long id;
		private String name;

	}

}

