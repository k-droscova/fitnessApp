package cz.cvut.fit.tjv.fitnessApp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Trainee extends IdentifiableImpl<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_trainee")
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @ManyToMany(mappedBy = "trainees")
    private Set<FitnessClass> classes = new HashSet<>();

    public Trainee(Integer id, String email, String name, String surname, Set<FitnessClass> classes) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.classes = classes != null ? classes : new HashSet<>();
    }
}