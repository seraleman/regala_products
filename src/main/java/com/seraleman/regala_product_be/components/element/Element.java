package com.seraleman.regala_product_be.components.element;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seraleman.regala_product_be.components.category.Category;
import com.seraleman.regala_product_be.components.collection.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "elements")
public class Element {

    @Id
    private String id;

    @NotNull
    private Collection collection;

    @NotNull
    private String description;

    @NotNull
    private String name;

    @NotNull
    private List<ElementComposition> primaries;

    @NotNull
    private List<Category> categories;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updated;

    public Element() {
    }

    public Element(@NotNull Collection collection, @NotNull String description, @NotNull String name,
            @NotNull List<ElementComposition> primaries, @NotNull List<Category> categories) {
        this.collection = collection;
        this.description = description;
        this.name = name;
        this.primaries = primaries;
        this.categories = categories;
    }

    public Element(@NotNull Collection collection, @NotNull String description, @NotNull String name,
            @NotNull List<ElementComposition> primaries, @NotNull List<Category> categories,
            @NotNull LocalDateTime created, @NotNull LocalDateTime updated) {
        this.collection = collection;
        this.description = description;
        this.name = name;
        this.primaries = primaries;
        this.categories = categories;
        this.created = created;
        this.updated = updated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Collection getCollection() {
        return collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ElementComposition> getPrimaries() {
        return primaries;
    }

    public void setPrimaries(List<ElementComposition> primaries) {
        this.primaries = primaries;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public Float getCost() {
        Float cost = 0f;
        for (ElementComposition primary : primaries) {
            cost += primary.getCost();
        }
        return cost;
    }

    public Integer getCategoriesQuantity() {
        return categories.size();
    }

}
