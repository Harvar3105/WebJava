package app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Data
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Authorities")
public class Authority {
    @Id
    @GeneratedValue
    private long id;
    @NonNull
    private String authorityName;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
}
