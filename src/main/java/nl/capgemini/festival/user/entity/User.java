package nl.capgemini.festival.user.entity;

import jakarta.persistence.*;
import nl.capgemini.festival.role.entity.Role;
import nl.capgemini.festival.user.repository.UserRepository;
import nl.capgemini.festival.userfunction.entity.UserFunction;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Transient
    private UserRepository repository;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "uid", unique = true, nullable = false)
    public String uid;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "encrypted")
    private boolean encrypted = false;
    @Column(name = "email")
    private String email;
    @Column(name = "dob")
    private String dob;


    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateOfBirth;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_function",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "function_id")
    )
    private Set<UserFunction> functions = new HashSet<>();

    public User() {}
    public User(String username, String password) {this(username, password, null, (Date) null, null, null);}
    public User(String username, String password, String dob){
        this(username, password);
        this.setDob(parseStringDate(dob));
    }
    public User(String username, String password, String dob, Role role, UserFunction function) {
        this(username, password, null, dob, role, function);
    }
    public User(String username, String password, String email, String dob, Role role, UserFunction function) {
        this(username, password);
        this.setEmail(email);
        this.setDob(dob);
        this.addRole(role);
        this.addFunction(function);
    }
    public User(String username, String password, String email, Date dob, Role role, UserFunction function) {
        if (username == null || password == null) throw new IllegalArgumentException("Username and/or password is not available!");
        this.username = username;
        this.password = password;
        this.email = email;
        this.dateOfBirth = dob;
        if (role != null) roles.add(role);
        if (function != null) functions.add(function);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUid() { return this.uid; }
    public void setUid(String uid) { this.uid = uid; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDob() {
        if (dateOfBirth != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            return dateFormat.format(dateOfBirth);
        }
        return null; // or return an empty string or a default value
    }
    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return this.email; }
    public void setDob(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public void setDob(String dateOfBirth) { if (dateOfBirth != null) this.dateOfBirth = parseStringDate(dateOfBirth); }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean isEncrypted() { return encrypted; }
    public void setEncrypted(boolean encrypted) { this.encrypted = encrypted; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public void addRole(Role role) { roles.add(role); }
    public Set<UserFunction> getFunctions() { return functions;}
    public void setFunctions(Set<UserFunction> functions) { this.functions = functions; }
    public void addFunction(UserFunction function) {functions.add(function); }

    private List<User> findAllByName(String username) {
        List<User> users = repository.findAll();
        ArrayList<User> foundUsers = new ArrayList<>();
        for (User user : users){
            if (user.getUsername().equals(username)){
                foundUsers.add(user);
            }
        }
        return foundUsers;
    }

    private Date parseStringDate(String dob) {
        if (dob == null || dob.isEmpty()) {
            throw new IllegalArgumentException("Date of birth cannot be null or empty");
        }
        try {
            return new java.text.SimpleDateFormat("dd-MM-yyyy").parse(dob);
        } catch (java.text.ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use dd-MM-yyyy format.", e);
        }
    }

    public User deepCopy() {
        User copy = new User(getUsername(), getPassword());
        copy.setId(getId());
        copy.setEncrypted(isEncrypted());
        copy.setEmail(getEmail());
        copy.setDob(getDob());
        copy.setRoles(getRoles());
        copy.setFunctions(getFunctions());
        return copy;
    }
}
