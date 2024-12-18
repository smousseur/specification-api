package com.smousseur.specification.api.example.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "password")
  private String password;

  @Column(name = "email")
  private String email;

  @Column(name = "last_connection")
  private LocalDateTime lastConnection;

  @Column(name = "city")
  private String city;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  @JoinTable(
      name = "users_role",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private List<Role> roles;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  @JoinTable(
      name = "users_privilege",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "privilege_id"))
  private List<Privilege> privileges;

  @ElementCollection
  @CollectionTable(name = "user_aliases", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "name")
  private Set<String> aliases;

  @OneToOne
  @JoinColumn(name = "id", referencedColumnName = "user_id")
  private Address address;
}
