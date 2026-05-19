package roomescape.repository;

import java.util.Map;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import roomescape.domain.User;

@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final RowMapper<User> rowMapper = (resultSet, rowNum) -> new User(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("email"),
            resultSet.getString("password")
    );

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
    }

    public User findById(Long id) {
        String sql = "SELECT id, name, email, password FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, name, email, password FROM users WHERE email = ?";
        return jdbcTemplate.query(sql, rowMapper, email).stream().findFirst();
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public User save(User user) {
        Number key = jdbcInsert.executeAndReturnKey(Map.of(
                "name", user.getName(),
                "email", user.getEmail(),
                "password", user.getPassword()
        ));
        return new User(key.longValue(), user.getName(), user.getEmail(), user.getPassword());
    }
}
