package app.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@Data
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Users")
public class User {

    @Id
    @GeneratedValue
    private long id;
    @NonNull
    private String username;
    @NonNull
    private String firstName;
    @NonNull
    private String password;
    private boolean enabled;
    @Valid
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Authority> authorities;

}
