package dev.codeio.helloworld.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Data
public class Todo {
    @Id
    @GeneratedValue
    Long id;

    @NotNull
    @NotBlank
    @Schema(name = "title", example = "Complete Spring Boot")
    String title;

    Boolean isCompleted;

    String userEmail;


    String priority;
    String date;
    String time;
    String description;
}