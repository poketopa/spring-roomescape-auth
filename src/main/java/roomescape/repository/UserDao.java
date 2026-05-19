package roomescape.repository;

import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import roomescape.domain.User;

@Repository
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> rowMapper = (resultSet, rowNum) -> new User(
            resultSet.getLong("id"),
            resultSet.getString("name"),
            resultSet.getString("email"),
            resultSet.getString("password")
    );

    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User findById(Long id) {
        String sql = "SELECT id, name, email, password FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, rowMapper, id);
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, name, email, password FROM users WHERE email = ?";
        return jdbcTemplate.query(sql, rowMapper, email).stream().findFirst();
    }
}
