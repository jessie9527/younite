package tw.com.younite.controller;

import tw.com.younite.entity.User;
import tw.com.younite.entity.UserPhotos;
import tw.com.younite.entity.UserProfile;
import tw.com.younite.service.inter.IUserPhotosService;
import tw.com.younite.service.inter.IUserProfileService;
import tw.com.younite.util.DateUtil;
import tw.com.younite.util.FileUploadUtil;
import tw.com.younite.util.JSONResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tw.com.younite.service.uploadException.FileEmptyException;
import tw.com.younite.service.uploadException.FileSizeException;
import tw.com.younite.service.uploadException.FileTypeException;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class UserProfileController extends BaseController {
    @Autowired
    private IUserProfileService iUserProfileService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Autowired
    private DateUtil dateUtil;

    @Autowired
    private IUserPhotosService iUserPhotosService;

    /**
     *
     * @param userProfile
     * @param session
     * @param avatar MultipartFile是Spring MVC提供的一個接口，可以接受各文件類型的數據
     * @return
     */

    /**
     * 限制上傳大頭貼最大值
     */
    public static final int AVATAR_MAX_SIZE = 10 * 1024 * 1024;
    public static final int VOICE_INTRO_MAX_SIZE = 5 * 1024 * 1024;

    /**
     * 限制上傳的文件類型
     */
    public static final List<String> AVATAR_TYPE = new ArrayList<>();
    public static final List<String> VOICE_TYPE = new ArrayList<>();

    static {
        AVATAR_TYPE.add("image/jpeg");
        AVATAR_TYPE.add("image/png");
        AVATAR_TYPE.add("image/bmp");
        AVATAR_TYPE.add("image/gif");
        AVATAR_TYPE.add("image/svg");
        AVATAR_TYPE.add("image/webp");
    }

    static {
        VOICE_TYPE.add("audio/mpeg");
        VOICE_TYPE.add("audio/wav");
        VOICE_TYPE.add("audio/aac");
    }

//    @PostMapping(value = "/users/userInfo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public JSONResult<String> insertProfile(@ModelAttribute UserProfile userProfile,
//                                            HttpSession session,
//                                            @RequestParam("avatar") MultipartFile avatar,
//                                            @RequestParam("birthday") String birthday) {
//        Integer userID = getIDFromSession(session);
//        userProfile.setId(userID);
//        if (avatar.isEmpty()) {
//            throw new FileEmptyException("大頭照不能為空");
//        }
//
//        if (avatar.getSize() > AVATAR_MAX_SIZE) {
//            throw new FileSizeException("大頭貼大小必須小於等於10Mb!");
//        }
//
////        if (voiceIntro.getSize() > VOICE_INTRO_MAX_SIZE) {
////            throw new FileEmptyException("聲音檔案大小必須小於或等於5Mb");
////        }
//
//        if (!AVATAR_TYPE.contains(avatar.getContentType())) {
//            throw new FileTypeException("大頭照上傳類型錯誤，必須為jpeg/png/gif/bpm/svg/webp");
//        }
//
////        if (!VOICE_TYPE.contains(voiceIntro.getContentType())) {
////            throw new FileTypeException("音檔上傳類型錯誤，必須為mp3/wav/aac");
////        }
//
//        String avatarPath = fileUploadUtil.uploadFile(avatar);
////        String voiceIntroPath = fileUploadUtil.uploadFile(voiceIntro);
//        userProfile.setProfileAvatar(avatarPath);
////        userProfile.setVoiceIntro(voiceIntroPath);
//
//        Date date = dateUtil.parseDate(birthday);
//        userProfile.setBirthday(date);
//
//        iUserProfileService.insertProfile(userProfile);
//        //返回使用者的大頭貼路徑給前端，未來可以展示用
//        return new JSONResult<>(CREATE_OK, avatarPath);
//    }

    @PostMapping(value = "/users/userInfo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JSONResult<String> insertProfile(@ModelAttribute UserProfile userProfile,
                                            HttpSession session,
                                            MultipartFile voice,
                                            MultipartFile avatar,
                                            String birthday) {
        Integer userID = getIDFromSession(session);
        userProfile.setId(userID);
        if (avatar.isEmpty()) {
            throw new FileEmptyException("大頭照不能為空");
        }

        if (avatar.getSize() > AVATAR_MAX_SIZE) {
            throw new FileSizeException("大頭貼大小必須小於等於10Mb!");
        }

        if (voice.getSize() > VOICE_INTRO_MAX_SIZE) {
            throw new FileEmptyException("聲音檔案大小必須小於或等於5Mb");
        }

        if (!AVATAR_TYPE.contains(avatar.getContentType())) {
            throw new FileTypeException("大頭照上傳類型錯誤，必須為jpeg/png/gif/bpm/svg/webp");
        }

        if (!VOICE_TYPE.contains(voice.getContentType())) {
            throw new FileTypeException("音檔上傳類型錯誤，必須為mp3/wav/aac");
        }

        String voiceIntroPath = fileUploadUtil.uploadFile(voice);
        String avatarPath = fileUploadUtil.uploadFile(avatar);

        userProfile.setProfileAvatar(avatarPath);
        userProfile.setVoiceIntro(voiceIntroPath);

        Date date = dateUtil.parseDate(birthday);
        userProfile.setBirthday(date);

        iUserProfileService.insertProfile(userProfile);
        //返回使用者的大頭貼路徑給前端，未來可以展示用
        return new JSONResult<>(CREATE_OK, avatarPath);
    }

    @PostMapping(value = "/users/userInfoooo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public JSONResult<String> insertProfileeee(@ModelAttribute UserProfile userProfile,
                                               HttpSession session,
                                               MultipartFile voice,
                                               MultipartFile avatar,
                                               MultipartFile[] photos,
                                               String birthday) {
        Integer userID = getIDFromSession(session);
        userProfile.setId(userID);
        if (avatar.isEmpty()) {
            throw new FileEmptyException("大頭照不能為空");
        }

        if (avatar.getSize() > AVATAR_MAX_SIZE) {
            throw new FileSizeException("大頭貼大小必須小於等於10Mb!");
        }

        if (voice.getSize() > VOICE_INTRO_MAX_SIZE) {
            throw new FileEmptyException("聲音檔案大小必須小於或等於5Mb");
        }

        if (!AVATAR_TYPE.contains(avatar.getContentType())) {
            throw new FileTypeException("大頭照上傳類型錯誤，必須為jpeg/png/gif/bpm/svg/webp");
        }

        if (!VOICE_TYPE.contains(voice.getContentType())) {
            throw new FileTypeException("音檔上傳類型錯誤，必須為mp3/wav/aac");
        }

        String voiceIntroPath = fileUploadUtil.uploadFile(voice);
        String avatarPath = fileUploadUtil.uploadFile(avatar);

        userProfile.setProfileAvatar(avatarPath);
        userProfile.setVoiceIntro(voiceIntroPath);

        Date date = dateUtil.parseDate(birthday);
        userProfile.setBirthday(date);

        iUserProfileService.insertProfile(userProfile);

        //處理使用者生活照
        Integer profileID = iUserProfileService.getUserProfile(userID).getProfileId();
        UserPhotos userPhotos = new UserPhotos();
        userPhotos.setProfileID(profileID);
        for (MultipartFile photo : photos) {
            String photoPath = fileUploadUtil.uploadFile(photo);
            switch (Arrays.asList(photos).indexOf(photo)) {
                case 0 -> userPhotos.setFirstPhotoPath(photoPath);
                case 1 -> userPhotos.setSecondPhotoPath(photoPath);
                case 2 -> userPhotos.setThirdPhotoPath(photoPath);
                case 3 -> userPhotos.setFourthPhotoPath(photoPath);
                case 4 -> userPhotos.setFifthPhotoPath(photoPath);
                case 5 -> userPhotos.setSixthPhotoPath(photoPath);
                default -> {
                }
            }
        }
        iUserPhotosService.insertPhotos(userPhotos);
        //返回使用者的大頭貼路徑給前端，未來可以展示用
        return new JSONResult<>(CREATE_OK, avatarPath);
    }

    @GetMapping("/users/userProfile")
    public JSONResult<UserProfile> getUserProfile(HttpSession session) {
        Integer userID = getIDFromSession(session);
        UserProfile data = iUserProfileService.getUserProfile(userID);
        return new JSONResult<>(OK, data);
    }

    @PostMapping("/users/voice")
    public String uploadVoice(MultipartFile voice) {
        return fileUploadUtil.uploadFile(voice);
    }

    @PutMapping("/users/resetUserProfile")
    public JSONResult<Void> resetProfile(HttpSession session,
                                         @RequestParam("fullName") String fullName,
                                         @RequestParam("birthday") String birthday,
                                         @RequestParam("gender") String gender,
                                         @RequestParam("sexualOrientation") String sexualOrientation,
                                         @RequestParam("datingGoal") String datingGoal,
                                         @RequestParam("location") String location,
                                         @RequestParam("selfIntro") String selfIntro,
                                         @RequestParam("preferredGender") String preferredGender,
                                         @RequestParam("avatar") MultipartFile avatar,
                                         @RequestParam("voiceIntro") MultipartFile voice) {
        Integer id = getIDFromSession(session);
        UserProfile userProfile = iUserProfileService.getUserProfile(id);

        userProfile.setFullName(fullName);
        userProfile.setBirthday(dateUtil.parseDate(birthday));
        userProfile.setGender(gender);
        userProfile.setSexualOrientation(sexualOrientation);
        userProfile.setDatingGoal(datingGoal);
        userProfile.setLocation(location);
        userProfile.setSelfIntro(selfIntro);
        userProfile.setPreferredGender(preferredGender);

        String newAvatarPath = fileUploadUtil.uploadFile(avatar);
        System.out.println("newAvatarPath = " + newAvatarPath);
        String newVoicePath = fileUploadUtil.uploadFile(voice);
        System.out.println("newVoicePath = " + newVoicePath);

        userProfile.setProfileAvatar(newAvatarPath);
        userProfile.setVoiceIntro(newVoicePath);
        iUserProfileService.resetUserProfile(userProfile);
        return new JSONResult<Void>(NO_CONTENT_OK);
    }
}
