package com.smousseur.specification.api.example.model.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "role")
@Data
public class Role {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @ManyToMany(mappedBy = "roles")
  private List<User> users;
}
