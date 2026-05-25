package com.moreira.financial_api.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    @Builder.Default
    private boolean includeInDashboard = true;

    @Column(nullable = false)
    @Builder.Default
    private String type = "CUSTOM";

    @PrePersist
    protected void onCreate() {
        if (this.type == null) {
            this.type = "CUSTOM";
        }
    }
}
