//package com.svipb.pam.accessmethod;
//
//import com.svipb.pam.accessmethod.entity.AccessMethodType;
//import com.svipb.pam.entity.User;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//
//import jakarta.persistence.*;
//import java.time.LocalDateTime;
//
//@Data
//@Entity
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "access_methods")
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
//public class AccessMethod {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id") // Assuming there's a user_id column in access_methods table
//    private User user;
//
//    @Column(nullable = false)
//    private String status;
//
//    @Column(nullable = false)
//    private LocalDateTime registrationDate;
//
//    @Column(nullable = false)
//    private LocalDateTime expirationDate;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name = "access_method_type", nullable = false)
//    private AccessMethodType accessMethodType; // Represents the access method type (RFID, FaceID, FingerprintID)
//
//    @Column(unique = true, nullable = false)
//    private String identifier; // Identifier for the access method
//
//    @Column(name = "rfid_number")
//    private String rfidNumber; // RFID card number
//
//    @Column(name = "face_id_identifier")
//    private String faceIDIdentifier; // FaceID identifier
//
//    @Column(name = "fingerprint_id_identifier")
//    private String fingerprintIDIdentifier; // FingerprintID identifier
//
//    @Column(name = "timestamp")
//    private LocalDateTime timestamp;
//}
