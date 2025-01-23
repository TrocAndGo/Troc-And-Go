package com.trocandgo.trocandgo.entity;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Services {
     @OneToMany(mappedBy = "service")
    private List<Favorites> favorites;
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NonNull
    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp(source = SourceType.DB)
    private Date creationDate;

    @NonNull
    @ManyToOne(optional = false)
    private Users createdBy;

    @NonNull
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    private ServiceTypes type;

    @NonNull
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    private ServiceStatuses status;

    @NonNull
    @ManyToOne(optional = false)
    private ServiceCategories category;

    @NonNull
    @ManyToOne(optional = false)
    private Adresses adress;
}
