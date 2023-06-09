package ohsoontaxi.backend.domain.user.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ohsoontaxi.backend.domain.user.presentation.dto.request.ChangeProfileRequest;
import ohsoontaxi.backend.domain.user.presentation.dto.response.UserProfileResponse;
import ohsoontaxi.backend.domain.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @PatchMapping("/profile")
    public UserProfileResponse changeProfilePath(@RequestBody @Valid ChangeProfileRequest changeProfileRequest){
        return userService.changeProfilePath(changeProfileRequest);
    }

    @GetMapping("/other-profile/{userId}")
    public UserProfileResponse getOtherProfile(@PathVariable Long userId){
        return userService.getOtherProfile(userId);
    }

    @GetMapping("/profile")
    public UserProfileResponse getProfile(){
        return userService.getProfile();
    }
 }
