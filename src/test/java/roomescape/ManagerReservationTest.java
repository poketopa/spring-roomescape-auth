package roomescape;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ManagerReservationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void 매니저_자기_매장_예약_목록_조회_성공() {
        insertStore(1L, "매장 A");
        insertManager(1L, "매니저A", "manager@test.com", 1L);
        insertThemeWithStore(1L, "테마A", 1L);
        insertReservationTime(1L, "10:00:00");
        insertReservation(1L, 1L, "2030-08-05", 1L);
        String token = login("manager@test.com");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .when().get("/manager/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void 매니저_다른_매장_예약은_조회_결과에_포함되지_않음() {
        insertStore(1L, "매장 A");
        insertStore(2L, "매장 B");
        insertManager(1L, "매니저A", "manager@test.com", 1L);
        insertThemeWithStore(1L, "매장A 테마", 1L);
        insertThemeWithStore(2L, "매장B 테마", 2L);
        insertReservationTime(1L, "10:00:00");
        insertReservation(1L, 1L, "2030-08-05", 1L); // 매장 A 테마(id=1) 예약
        insertReservation(1L, 2L, "2030-08-06", 1L); // 매장 B 테마(id=2) 예약 — 같은 사람이지만 다른 매장
        String token = login("manager@test.com");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .when().get("/manager/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1)); // 매장 A 예약 1건만 반환
    }

    @Test
    void 일반_유저가_매니저_조회_엔드포인트_접근_시_403_반환() {
        insertUser(1L, "일반유저", "user@test.com");
        String token = login("user@test.com");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .when().get("/manager/reservations")
                .then().log().all()
                .statusCode(403)
                .body("code", equalTo("FORBIDDEN"))
                .body("message", equalTo("매장 관리자 권한이 없습니다."));
    }

    @Test
    void 미로그인_상태_매니저_엔드포인트_접근_시_401_반환() {
        RestAssured.given().log().all()
                .when().get("/manager/reservations")
                .then().log().all()
                .statusCode(401)
                .body("code", equalTo("AUTHENTICATION_REQUIRED"));
    }

    @Test
    void 매니저_자기_매장_예약_삭제_성공() {
        insertStore(1L, "매장 A");
        insertManager(1L, "매니저A", "manager@test.com", 1L);
        insertUser(2L, "예약자", "user@test.com");
        insertThemeWithStore(1L, "매장A 테마", 1L);
        insertReservationTime(1L, "10:00:00");
        insertReservation(2L, 1L, "2030-08-05", 1L); // 예약자가 만든 매장A 예약
        String token = login("manager@test.com");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .when().delete("/manager/reservations/1")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    void 매니저_다른_매장_예약_삭제_시도_시_403_반환() {
        insertStore(1L, "매장 A");
        insertStore(2L, "매장 B");
        insertManager(1L, "매니저A", "manager@test.com", 1L); // 매장 A 매니저
        insertUser(2L, "예약자", "user@test.com");
        insertThemeWithStore(1L, "매장B 테마", 2L);            // 매장 B 소속 테마
        insertReservationTime(1L, "10:00:00");
        insertReservation(2L, 1L, "2030-08-05", 1L);           // 매장 B 테마 예약
        String token = login("manager@test.com");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .when().delete("/manager/reservations/1")
                .then().log().all()
                .statusCode(403)
                .body("code", equalTo("FORBIDDEN"))
                .body("message", equalTo("해당 매장의 예약에 접근할 권한이 없습니다."));
    }

    @Test
    void 매니저_자기_매장_예약_변경_성공() {
        insertStore(1L, "매장 A");
        insertManager(1L, "매니저A", "manager@test.com", 1L);
        insertUser(2L, "예약자", "user@test.com");
        insertThemeWithStore(1L, "매장A 테마", 1L);
        insertReservationTime(1L, "10:00:00");
        insertReservationTime(2L, "14:00:00");
        insertReservation(2L, 1L, "2030-08-05", 1L);
        String token = login("manager@test.com");

        Map<String, Object> params = new HashMap<>();
        params.put("date", "2030-09-10");
        params.put("timeId", 2);

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(params)
                .when().patch("/manager/reservations/1")
                .then().log().all()
                .statusCode(200)
                .body("date", equalTo("2030-09-10"))
                .body("time.id", equalTo(2));
    }

    @Test
    void 매니저_다른_매장_예약_변경_시도_시_403_반환() {
        insertStore(1L, "매장 A");
        insertStore(2L, "매장 B");
        insertManager(1L, "매니저A", "manager@test.com", 1L); // 매장 A 매니저
        insertUser(2L, "예약자", "user@test.com");
        insertThemeWithStore(1L, "매장B 테마", 2L);            // 매장 B 소속 테마
        insertReservationTime(1L, "10:00:00");
        insertReservationTime(2L, "14:00:00");
        insertReservation(2L, 1L, "2030-08-05", 1L);           // 매장 B 테마 예약
        String token = login("manager@test.com");

        Map<String, Object> params = new HashMap<>();
        params.put("date", "2030-09-10");
        params.put("timeId", 2);

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(params)
                .when().patch("/manager/reservations/1")
                .then().log().all()
                .statusCode(403)
                .body("code", equalTo("FORBIDDEN"))
                .body("message", equalTo("해당 매장의 예약에 접근할 권한이 없습니다."));
    }

    @Test
    void 인증_실패는_401_인가_실패는_403으로_구분된다() {
        insertUser(1L, "일반유저", "user@test.com");
        String token = login("user@test.com");

        // 인증 실패: 토큰 없음 → 401
        RestAssured.given()
                .when().get("/manager/reservations")
                .then()
                .statusCode(401);

        // 인가 실패: 토큰 있지만 MANAGER 역할 아님 → 403
        RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .when().get("/manager/reservations")
                .then()
                .statusCode(403);
    }

    private void insertStore(Long id, String name) {
        jdbcTemplate.update("INSERT INTO store(id, name) VALUES (?, ?)", id, name);
    }

    private void insertManager(Long id, String name, String email, Long storeId) {
        jdbcTemplate.update(
                "INSERT INTO users(id, name, email, password, role, store_id) VALUES (?, ?, ?, ?, 'MANAGER', ?)",
                id, name, email, "password1234", storeId);
    }

    private void insertUser(Long id, String name, String email) {
        jdbcTemplate.update("INSERT INTO users(id, name, email, password) VALUES (?, ?, ?, ?)",
                id, name, email, "password1234");
    }

    private void insertThemeWithStore(Long id, String name, Long storeId) {
        jdbcTemplate.update(
                "INSERT INTO theme(id, name, description, thumbnail_image_url, store_id) VALUES (?, ?, '설명', 'https://thumbnail.url', ?)",
                id, name, storeId);
    }

    private void insertReservationTime(Long id, String startAt) {
        jdbcTemplate.update("INSERT INTO reservation_time(id, start_at) VALUES (?, ?)", id, startAt);
    }

    private void insertReservation(Long userId, Long themeId, String date, Long timeId) {
        jdbcTemplate.update("INSERT INTO reservation(user_id, theme_id, date, time_id) VALUES (?, ?, ?, ?)",
                userId, themeId, date, timeId);
    }

    private String login(String email) {
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        body.put("password", "password1234");

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when().post("/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("accessToken");
    }
}
