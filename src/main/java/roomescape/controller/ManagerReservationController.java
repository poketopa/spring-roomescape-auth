package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.AuthenticatedMember;
import roomescape.auth.LoginMember;
import roomescape.auth.LoginRequired;
import roomescape.domain.Reservation;
import roomescape.dto.ReservationResponse;
import roomescape.dto.UpdateReservationRequest;
import roomescape.service.ReservationService;

@Tag(name = "매장 매니저 - 예약 관리", description = "매장 매니저용 예약 조회·변경·삭제 API")
@RestController
@RequestMapping("/manager/reservations")
@LoginRequired
public class ManagerReservationController {

    private final ReservationService reservationService;

    public ManagerReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "매장 예약 목록 조회", description = "로그인한 매니저의 매장에 속한 모든 예약을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요"),
            @ApiResponse(responseCode = "403", description = "매장 관리자 권한 없음")
    })
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> readStoreReservations(
            @LoginMember AuthenticatedMember loginMember) {
        List<ReservationResponse> responses = reservationService.getStoreReservations(loginMember.id())
                .stream()
                .map(ReservationResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "매장 예약 변경", description = "매니저가 자기 매장의 예약 날짜·시간을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 데이터 또는 과거로의 변경 시도"),
            @ApiResponse(responseCode = "401", description = "로그인 필요"),
            @ApiResponse(responseCode = "403", description = "다른 매장의 예약에 접근"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 예약 ID"),
            @ApiResponse(responseCode = "409", description = "해당 시간에 이미 예약이 존재함")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ReservationResponse> updateStoreReservation(
            @Parameter(description = "변경할 예약 ID", example = "1") @PathVariable Long id,
            @LoginMember AuthenticatedMember loginMember,
            @RequestBody UpdateReservationRequest request) {
        Reservation updated = reservationService.updateStoreReservation(id, loginMember.id(), request);
        return ResponseEntity.ok(ReservationResponse.from(updated));
    }

    @Operation(summary = "매장 예약 삭제", description = "매니저가 자기 매장의 예약을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "예약 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요"),
            @ApiResponse(responseCode = "403", description = "다른 매장의 예약에 접근"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 예약 ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStoreReservation(
            @Parameter(description = "삭제할 예약 ID", example = "1") @PathVariable Long id,
            @LoginMember AuthenticatedMember loginMember) {
        reservationService.deleteStoreReservation(id, loginMember.id());
        return ResponseEntity.noContent().build();
    }
}
