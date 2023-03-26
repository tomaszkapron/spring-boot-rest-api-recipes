package recipes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "recipes")
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @Column(name = "name")
    @NotBlank
    private String name;

    @Column(name = "category")
    @NotBlank
    private String category;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "description")
    @NotBlank
    private String description;

    @Column(name = "ingredients")
    @NotNull
    @Size(min = 1)
    @ElementCollection
    private List<String> ingredients;

    @Column(name = "directions")
    @NotNull
    @Size(min = 1)
    @ElementCollection
    private List<String> directions;

    @Column(name = "ownerUser")
    @JsonIgnore
    String ownerUser;

    @PrePersist
    protected void prePersist() {
        if (this.date == null) date = LocalDateTime.now();
    }

    @PreUpdate
    protected void preUpdate() {
        this.date = LocalDateTime.now();
    }
}
