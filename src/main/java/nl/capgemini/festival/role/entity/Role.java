package nl.capgemini.festival.role.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ROLES")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, columnDefinition = "varchar(255) default 'No name'")
    private String name;

    @Column(name = "description", columnDefinition = "varchar(255) default 'No description'")
    private String description;

    @Column(name = "accessLevel", columnDefinition = "int default 99")
    private int accessLvl;

    public Role() {}

    public Role(String name, String description) {this(name, description, null);}

    public Role(String name, String description, Integer accessLvl) {
        if (name == null) name = "ROLE_USER";
        if (!name.contains("ROLE_")) name = "ROLE_" + name;
        this.name = name.toUpperCase();;
        this.description = description;
        this.accessLvl = accessLvl != null ? accessLvl : 99;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getAccessLvl() { return accessLvl; }
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }


}
