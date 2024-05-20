package com.example.demo;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Data {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Long expiredAt;

    public Data() {
    }

    public Data(Integer id, Long expiredAt) {
        this.id = id;
        this.expiredAt = expiredAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Long expiredAt) {
        this.expiredAt = expiredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Data data)) return false;
        return Objects.equals(id, data.id) && Objects.equals(expiredAt, data.expiredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, expiredAt);
    }

    @Override
    public String toString() {
        return "Data{" +
               "id=" + id +
               ", expiredAt=" + expiredAt +
               '}';
    }
}