package com.trocandgo.trocandgo.entity;

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

@Entity
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class Messages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @OneToOne
    private Conversations conversation;

    @NonNull
    @OneToOne
    private Users sender;

    @NonNull
    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp(source = SourceType.DB)
    private Date createdAt;
}
