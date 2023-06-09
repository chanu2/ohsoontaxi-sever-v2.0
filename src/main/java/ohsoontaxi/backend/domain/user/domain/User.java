package ohsoontaxi.backend.domain.user.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ohsoontaxi.backend.domain.temperature.domain.Temperature;
import ohsoontaxi.backend.domain.user.domain.vo.UserInfoVO;
import ohsoontaxi.backend.global.common.user.Gender;
import ohsoontaxi.backend.global.database.BaseEntity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class User extends BaseEntity {

    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String oauthProvider;

    private String oauthId;

    private String name;

    private String schoolNum;

    private String email;

    private String profilePath;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "temperature_id")
    private Temperature temperature;

    @Enumerated(EnumType.STRING)
    private AccountRole accountRole = AccountRole.USER;

    public UserInfoVO getUserInfo() {
        return UserInfoVO.builder()
                .userId(id)
                .name(name)
                .schoolNum(schoolNum)
                .gender(gender)
                .email(email)
                .profilePath(profilePath)
                .temperatureInfoVo(temperature.getTemperatureInfoVo())
                .build();
    }

    @Builder
    public User(
            String oauthProvider,
            String oauthId,
            String name,
            String schoolNum,
            String email,
            String profilePath,
            Gender gender,
            Temperature temperature) {
        this.oauthProvider = oauthProvider;
        this.oauthId = oauthId;
        this.name = name;
        this.schoolNum = schoolNum;
        this.email = email;
        this.profilePath = profilePath;
        this.gender = gender;
        this.temperature = temperature;
    }

    public static User createUser(String oauthProvider, String oauthId, String name,
                                  String schoolNum, String email, String profilePath,
                                  Gender gender, Temperature temperature) {
        return builder()
                .oauthProvider(oauthProvider)
                .oauthId(oauthId)
                .name(name)
                .schoolNum(schoolNum)
                .email(email)
                .profilePath(profilePath)
                .gender(gender)
                .temperature(temperature)
                .build();
    }

    public void changeProfilePath(String profilePath){
        this.profilePath = profilePath;
    }




}
