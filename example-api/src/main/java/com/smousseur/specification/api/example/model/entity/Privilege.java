package com.smousseur.specification.api.example.model.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "privilege")
@Data
public class Privilege {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name")
  private String name;

  @ManyToMany(mappedBy = "privileges")
  private List<User> users;
}
