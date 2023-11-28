package com.svipb.pam.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"}),
        @UniqueConstraint(columnNames = {"rfid"}),
        @UniqueConstraint(columnNames = {"faceid"}),
        @UniqueConstraint(columnNames = {"fingerid"})
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private int id;
    @NotEmpty(message = "Nama tidak boleh kosong")
    private String name;
    @Email(message = "Email tidak valid")
    @NotEmpty(message = "Email tidak boleh kosong")
    @Column(unique = true )
    private String email;
    @NotEmpty(message = "Jabatan tidak boleh kosong")
    private String status;
    @NotEmpty(message = "Jenis Kelamin tidak boleh kosong")
    private String gender;
    @NotNull
    private int phone;
    @Column(unique = true)
    private int rfid;
    @Column(unique = true)
    private int faceid;
    @Column(unique = true)
    private int fingerid;
}
