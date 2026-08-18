/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Product {
    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private int id;
    private String name;
    private String slug;
    private String sku;
    private String image;
    private String coverImageUrl;
    private String trailerUrl;
    private double price;
    private double salePrice;
    private String description;
    private int publisherId;
    private int developerId;
    private int categoryId;
    private LocalDate releaseDate;
    private String ageRating;
    private double rating;
    private int stockQuantity;
    private boolean isDigital;
    private boolean isFeatured;
    private boolean isActive;
    private int viewsCount;
    
    // Reference to category object for backward compatibility
    private Category category;

    public Product() {
    }
    
    // Constructor with minimal fields for backward compatibility
    public Product(int id, String name, String image, double price, String description, 
                  String releaseDate, double rating, Category category) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.description = description;
        this.releaseDate = parseReleaseDate(releaseDate);
        this.rating = rating;
        this.category = category;
        this.categoryId = category != null ? category.getId() : 0;
        
        // Generate a slug from the name
        this.slug = name.toLowerCase().replace(' ', '-').replaceAll("[^a-z0-9-]", "");
        this.isActive = true;
        this.isDigital = true;
    }
    
    // Full constructor
    public Product(int id, String name, String slug, String sku, String image,
                  String coverImageUrl, String trailerUrl, double price, double salePrice,
                  String description, int publisherId, int developerId, int categoryId,
                  LocalDate releaseDate, String ageRating, double rating, int stockQuantity,
                  boolean isDigital, boolean isFeatured, boolean isActive, int viewsCount) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.sku = sku;
        this.image = image;
        this.coverImageUrl = coverImageUrl;
        this.trailerUrl = trailerUrl;
        this.price = price;
        this.salePrice = salePrice;
        this.description = description;
        this.publisherId = publisherId;
        this.developerId = developerId;
        this.categoryId = categoryId;
        this.releaseDate = releaseDate;
        this.ageRating = ageRating;
        this.rating = rating;
        this.stockQuantity = stockQuantity;
        this.isDigital = isDigital;
        this.isFeatured = isFeatured;
        this.isActive = isActive;
        this.viewsCount = viewsCount;
    }

    // Helper to parse string date to LocalDate
    private LocalDate parseReleaseDate(String date) {
        if (date == null || date.isEmpty()) {
            return null;
        }
        try {
            if (date.contains("/")) {
                String[] parts = date.split("/");
                if (parts.length == 3) {
                    int day = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    int year = Integer.parseInt(parts[2]);
                    return LocalDate.of(year, month, day);
                }
            }
            return LocalDate.parse(date, INPUT_DATE_FORMAT);
        } catch (Exception e) {
            // If parsing fails, return null
        }
        return null;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getSlug() {
        return slug;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    public String getSku() {
        return sku;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    
    public String getCoverImageUrl() {
        return coverImageUrl;
    }
    
    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }
    
    public String getTrailerUrl() {
        return trailerUrl;
    }
    
    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
    public double getSalePrice() {
        return salePrice;
    }
    
    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getPublisherId() {
        return publisherId;
    }
    
    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }
    
    public int getDeveloperId() {
        return developerId;
    }
    
    public void setDeveloperId(int developerId) {
        this.developerId = developerId;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    // For backward compatibility
    public String getReleasedate() {
        if (releaseDate == null) return "";
        return String.format("%02d/%02d/%d", 
            releaseDate.getDayOfMonth(), 
            releaseDate.getMonthValue(),
            releaseDate.getYear());
    }

    /** Value expected by an HTML input[type=date]. */
    public String getReleaseDateInput() {
        return releaseDate == null ? "" : releaseDate.format(INPUT_DATE_FORMAT);
    }
    
    // For backward compatibility
    public void setReleasedate(String releasedate) {
        this.releaseDate = parseReleaseDate(releasedate);
    }
    
    public String getAgeRating() {
        return ageRating;
    }
    
    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
    
    public int getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public boolean isDigital() {
        return isDigital;
    }
    
    public void setDigital(boolean isDigital) {
        this.isDigital = isDigital;
    }
    
    public boolean isFeatured() {
        return isFeatured;
    }
    
    public void setFeatured(boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    public int getViewsCount() {
        return viewsCount;
    }
    
    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    // For backward compatibility
    public Category getCat() {
        return category;
    }

    // For backward compatibility
    public void setCat(Category category) {
        this.category = category;
        if (category != null) {
            this.categoryId = category.getId();
        }
    }
    
    // For more modern naming
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            this.categoryId = category.getId();
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        var product = (Product) obj;
        return id == product.id && 
               Double.compare(product.price, price) == 0 &&
               Double.compare(product.rating, rating) == 0 &&
               Objects.equals(name, product.name) &&
               Objects.equals(slug, product.slug) &&
               Objects.equals(description, product.description);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name, slug, price, description, rating);
    }
    
    @Override
    public String toString() {
        return "Product{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", price=" + price +
               ", categoryId=" + categoryId +
               ", rating=" + rating +
               '}';
    }
}
