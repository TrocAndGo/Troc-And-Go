package com.trocandgo.trocandgo.model;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
@IdClass(ReviewsPK.class)
public class Reviews {
    @Id
    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn
    @NonNull
    private Users user;

    @Id
    @OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn
    @NonNull
    private Services service;

    @NonNull
    @Column(columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp(source = SourceType.DB)
    private Date createdAt;

    @NonNull
    @DecimalMin("0")
    @DecimalMax("5")
    private Integer rating;
}
