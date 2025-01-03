package com.trocandgo.trocandgo.model;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Services {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp(source = SourceType.DB)
    private Date creationDate;

    @NonNull
    @OneToOne(optional = false)
    private Users createdBy;

    @NonNull
    @OneToOne(optional = false)
    private ServiceTypes type;

    @NonNull
    @OneToOne(optional = false)
    private ServiceStatuses status;

    @NonNull
    @OneToOne(optional = false)
    private ServiceCategories category;

    @NonNull
    @OneToOne(optional = false)
    private Adresses adress;
}
