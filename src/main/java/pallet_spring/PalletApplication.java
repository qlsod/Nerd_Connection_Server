package pallet_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class PalletApplication {

	public static void main(String[] args) {
		SpringApplication.run(PalletApplication.class, args);
	}

}
