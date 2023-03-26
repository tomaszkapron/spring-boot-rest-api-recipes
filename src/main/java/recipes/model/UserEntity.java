package recipes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "email is mandatory")
    @Pattern(regexp = ".+@.+\\..+")
    private String email;

    @Size(min = 8)
    @NotBlank
    private String password;

    @JsonIgnore
    private String role;

    @PrePersist
    protected void prePersist() {
        this.role = "ROLE_USER";
    }
}
