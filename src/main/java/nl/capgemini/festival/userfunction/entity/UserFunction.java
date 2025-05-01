package nl.capgemini.festival.userfunction.entity;

import jakarta.persistence.*;
import nl.capgemini.festival.user.entity.User;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "functions")
public class UserFunction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_function", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "varchar(255) default 'No description'")
    private String description;
    
    @ManyToMany(mappedBy = "functions", fetch = FetchType.LAZY)
    private Set<User> functions = new HashSet<>();

    private UserFunction() {}
    public UserFunction(String name) {this(name, null);}
    public UserFunction(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public Set<User> getFunctions() {
        return functions;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setFunctions(Set<User> functions) {
        this.functions = functions;
    }
    
}
