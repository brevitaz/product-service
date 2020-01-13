package com.brevitaz.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Product implements Identifiable{

    private String id;
    private String title;
    private String code;
    private String description;
    private Object specifications;
    private String author;
    private Date creationDate;
    private List<String> categories;
    private List<String> tags;
    private Integer reviewScore;
    private String imageUrl;
    private Double price;

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String generateId() {
        return null;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Integer getReviewScore() {
        return reviewScore;
    }

    public void setReviewScore(Integer reviewScore) {
        this.reviewScore = reviewScore;
    }

    public Object getSpecifications() {
        return specifications;
    }

    public void setSpecifications(Object specifications) {
        this.specifications = specifications;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }


    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", specifications=" + specifications +
                ", author='" + author + '\'' +
                ", creationDate=" + creationDate +
                ", categories=" + categories +
                ", tags=" + tags +
                ", reviewScore=" + reviewScore +
                ", imageUrl='" + imageUrl + '\'' +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) &&
                Objects.equals(title, product.title) &&
                Objects.equals(code, product.code) &&
                Objects.equals(description, product.description) &&
                Objects.equals(specifications, product.specifications) &&
                Objects.equals(author, product.author) &&
                Objects.equals(creationDate, product.creationDate) &&
                Objects.equals(categories, product.categories) &&
                Objects.equals(tags, product.tags) &&
                Objects.equals(reviewScore, product.reviewScore) &&
                Objects.equals(imageUrl, product.imageUrl) &&
                Objects.equals(price, product.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }
}
