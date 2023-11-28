package com.svipb.pam.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotEmpty(message = "Nama tidak boleh kosong")
    private String name;
    @NotEmpty(message = "Username tidak boleh kosong")
    private String username;
    @Email(message = "Email tidak valid")
    @NotEmpty(message = "Email tidak boleh kosong")
    @Column(unique = true)
    private String email;
    @NotEmpty(message = "Password tidak boleh kosong")
    private String password;

}
