package roomescape.domain;

public class User {

    private final Long id;
    private final String name;
    private final String email;
    private final String password;
    private final String role;
    private final Long storeId;

    public User(Long id, String name, String email, String password) {
        this(id, name, email, password, "USER", null);
    }

    public User(Long id, String name, String email, String password, String role, Long storeId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("사용자 이름은 필수입니다.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("사용자 이메일은 필수입니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("사용자 비밀번호는 필수입니다.");
        }
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role != null ? role : "USER";
        this.storeId = storeId;
    }

    public boolean isManager() {
        return "MANAGER".equals(role);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public Long getStoreId() {
        return storeId;
    }
}
