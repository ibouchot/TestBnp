package com.example.demo.services.load;

import com.example.demo.dto.TransactionDto;
import com.example.demo.services.event.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

@Component
@RequiredArgsConstructor
@Order(1)
public class SqlScriptLoader implements CommandLineRunner {

    private final DataSource dataSource;
    private final EventService eventService;

    @Override
    public void run(String... args) throws Exception {
        try{
            long count = eventService.count();
            if (count == 0) {
                try (Connection connection = dataSource.getConnection()) {

                    System.out.println("Loading events...");
                    ScriptUtils.executeSqlScript(connection, new ClassPathResource("data/data.sql"));
                    System.out.println("Events loaded.");
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'exécution du script SQL : " + e.getMessage());
                }
            } else {
                System.out.println("La table Event n'est pas vide, chargement ignoré.");
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

    }
}
