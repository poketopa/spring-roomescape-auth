package roomescape.domain;

public class Theme {

    private final Long id;
    private final String name;
    private final String description;
    private final String thumbnailImageUrl;

    private final Long storeId;

    public Theme(Long id, String name, String description, String thumbnailImageUrl) {
        this(id, name, description, thumbnailImageUrl, null);
    }

    public Theme(Long id, String name, String description, String thumbnailImageUrl, Long storeId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("테마 이름은 필수입니다.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("테마 설명은 필수입니다.");
        }
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.storeId = storeId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public Long getStoreId() {
        return storeId;
    }
}
