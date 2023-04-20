package ohsoontaxi.backend.domain.reservation.service;


import lombok.RequiredArgsConstructor;
import ohsoontaxi.backend.domain.reservation.domain.Reservation;
import ohsoontaxi.backend.domain.reservation.domain.repository.ReservationRepository;
import ohsoontaxi.backend.domain.reservation.exception.ReservationNotFoundException;
import ohsoontaxi.backend.domain.reservation.presentation.dto.request.CreateReservationRequest;
import ohsoontaxi.backend.domain.reservation.presentation.dto.request.UpdateReservationRequest;
import ohsoontaxi.backend.domain.reservation.presentation.dto.response.ReservationBriefInfoDto;
import ohsoontaxi.backend.domain.reservation.presentation.dto.response.ReservationResponse;
import ohsoontaxi.backend.domain.user.domain.User;
import ohsoontaxi.backend.global.common.reservation.ReservationStatus;
import ohsoontaxi.backend.global.utils.security.SecurityUtils;
import ohsoontaxi.backend.global.utils.user.UserUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService implements ReservationUtils {

    private final ReservationRepository reservationRepository;
    private final UserUtils userUtils;

    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest createReservationRequest){

        User user = userUtils.getUserFromSecurityContext();

        Reservation reservation = makeReservation(createReservationRequest, user);

        reservationRepository.save(reservation);

        return getReservationResponse(reservation);
    }

    @Transactional
    public void deleteReservation(Long reservationId){

        User user = userUtils.getUserFromSecurityContext();

        Reservation reservation = queryReservation(reservationId);

        reservation.validUserIsHost(user.getId());

        reservationRepository.delete(reservation);
    }

    //방 상세정보
    public ReservationResponse getReservationDetailById(Long reservationId) {

        Reservation reservation = queryReservation(reservationId);

        return getReservationResponse(reservation);
    }

    @Transactional
    public ReservationResponse updateReservation(Long reservationId, UpdateReservationRequest updateReservationRequest) {

        Long currentUserId = SecurityUtils.getCurrentUserId();

        Reservation reservation = queryReservation(reservationId);

        reservation.validUserIsHost(currentUserId);

        reservation.updateReservation(updateReservationRequest.toUpdateReservationDto());

        return getReservationResponse(reservation);
    }


    //모든 리스트

//    public Slice<Group> findSliceOpenGroups(PageRequest pageRequest) {
//        Slice<Group> groupList = groupRepository.findSliceByGroupType(GroupType.OPEN, pageRequest);
//
//        return groupList;
//    }



    // 가까운 순서대로 페이징 해서 가져오기
    public Slice<ReservationBriefInfoDto> findAllReservation(PageRequest pageRequest) {

        Slice<Reservation> sliceReservation =
                reservationRepository.findSliceBy(pageRequest);

        return sliceReservation.map(reservation -> new ReservationBriefInfoDto(reservation.getReservationBaseInfoVo()));
    }

    // 내가 만든방
    public Slice<ReservationBriefInfoDto> findMyReservation(PageRequest pageRequest) {

        Long currentUserId = SecurityUtils.getCurrentUserId();

        Slice<Reservation> sliceReservation =
                reservationRepository.findAllByUserId(pageRequest,currentUserId);

        return sliceReservation.map(reservation -> new ReservationBriefInfoDto(reservation.getReservationBaseInfoVo()));
    }



    private Reservation makeReservation(CreateReservationRequest createReservationRequest,User user){

        Reservation reservation = Reservation.builder()
                .user(user)
                .title(createReservationRequest.getTitle())
                .startPoint(createReservationRequest.getStartPoint())
                .destination(createReservationRequest.getDestination())
                .departureDate(createReservationRequest.getDepartureDate())
                .reservationStatus(ReservationStatus.POSSIBLE)
                .gender(createReservationRequest.getGender())
                .passengerNum(4)
                .currentNum(0)
                .startLatitude(createReservationRequest.getStartLatitude())
                .startLongitude(createReservationRequest.getStartLongitude())
                .destinationLatitude(createReservationRequest.getDestinationLatitude())
                .destinationLongitude(createReservationRequest.getDestinationLongitude())
                .build();

        return reservation;
    }

    // TODO: 2023/04/19 방주인 확인 로직 상황에 따라 추가
    private ReservationResponse getReservationResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getReservationBaseInfoVo(),
                reservation.getParticipationInfoVOs());
    }

    @Override
    public Reservation queryReservation(Long id) {
        return reservationRepository
                .findById(id)
                .orElseThrow(()-> ReservationNotFoundException.EXCEPTION);
    }
}
