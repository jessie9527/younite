package tw.com.younite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import tw.com.younite.entity.User;
import tw.com.younite.mapper.UserMapper;
import tw.com.younite.service.inter.IUserService;
import tw.com.younite.service.exception.UsernameDuplicatedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import tw.com.younite.service.exception.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 將UserService 交給spring管理，可自動創建component，沒加會報錯
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void reg(User user) {
        String username = user.getUsername();
        String email = user.getEmail();
        String phone = user.getPhone();
        User result = userMapper.getByUsername(username);

        //驗證帳號是否重複
        if (result != null) {
            throw new UsernameDuplicatedException("帳號重複!");
        }

        //驗證信箱是否重複
        if (userMapper.getByUserEmail(email) != null) {
            throw new EmailDuplicatedException("電子信箱重複!");
        }

        //驗證手機是否重複
        if (userMapper.getByUserPhone(phone) != null) {
            throw new PhoneDuplicatedException("電話重複!");
        }


        /**TODO: 若使用者用Google/Facebook註冊，將對應的欄位設定為1 */


        /** 密碼加密處理, 加密方法: md5
         form: salt value + password + salt value */

        String oldPassword = user.getPassword();
        String saltValue = UUID.randomUUID().toString().toUpperCase();
        user.setSalt(saltValue);
        String md5Password = getMD5Password(oldPassword, saltValue);
        user.setPassword(md5Password);
        /** 加入資料庫前，補全部分資料: */
        Date date = new Date();
        user.setVipExpiry(date);
        user.setCreatedAt(date);
        user.setModifiedAt(date);
        /** 註冊成功: rows == 1.*/
        Integer rows = userMapper.register(user);
        if (rows != 1) {
            throw new RegisterException("伺服器/資料庫異常!");
        }
    }

    @Override
    @Transactional
    public void regByOAuth(User user) {
        Integer rows = userMapper.registerByOAuth(user);
        if (rows != 1) {
            throw new RegisterException("第三方註冊失敗");
        }
    }

    @Override
    @Transactional
    public User login(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        User result = userMapper.getByUsername(username);
        if (result == null) {
            throw new UserNotFoundException("帳號不存在");
        }
        String databasePassword = result.getPassword();
        String salt = result.getSalt();
        String MD5Password = getMD5Password(password, salt);
        if (!MD5Password.equals(databasePassword)) {
            throw new PasswordNotMatchException("密碼錯誤!");
        }
        User newUser = new User();
        newUser.setId(result.getId());
        newUser.setUsername(result.getUsername());
        return newUser;
    }

//    @Override
//    @Transactional
//    public User loginByGoogle(String token) {
//        User user = new User();
//        user.setUsername(parseJSON(token).get("name").asText());
//        user.setEmail(parseJSON(token).get("email").asText());
//        user.setThirdPartyLogin(true);
//        System.out.println(user + "˙456");
//        System.out.println(user);
//        return user;
//        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
//        lqw.eq(User::getEmail, user.getEmail()).eq(User::getThirdPartyLogin, true);
//        List<User> result = userMapper.selectList(lqw);
//        if (result.isEmpty()) {
//            return user;
//        } else {
//        return null;
//        }
//    }
    public static JsonNode parseJSON(String token){
        String[] parts = token.split("\\.", 0);


        byte[] bytes = Base64.getUrlDecoder().decode(parts[1]);
        String decodedString = new String(bytes, StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;

        try {
            jsonNode = objectMapper.readTree(decodedString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return jsonNode;
    }



    private String getMD5Password(String password, String salt) {
        for (int i = 0; i < 5; i++) {
            password = DigestUtils.md5DigestAsHex((salt + password + salt).getBytes()).toUpperCase();
        }
        return password;
    }

    @Override
    @Transactional
    public void resetPassword(Integer id, String username,
                               String oldPassword, String newPassword) {
        User result = userMapper.getUserByID(id);
        if (result == null) {
            throw new UserNotFoundException();
        }
        //確認輸入的舊密碼和資料庫中的密碼是否相同
        checkPassword(oldPassword, result);
        //輸入的新密碼進行加密
        String newMD5Password = getMD5Password(newPassword, result.getSalt());
        Integer row = userMapper.updatePasswordByID(id, newMD5Password, new Date());
        if (row != 1) {
            throw new UpdateException();
        }
    }

    private void checkPassword(String uncheckPassword, User user) throws PasswordNotMatchException {
        //資料庫中的密碼
        String databasePassword = user.getPassword();
        //待確認密碼加密
        String salt = user.getSalt();
        String password = getMD5Password(uncheckPassword, salt);
        System.out.println("uncheckPassword = " + uncheckPassword);
        System.out.println("password = " + password);
        System.out.println("databasePassword = " + databasePassword);
        if (!databasePassword.equals(password)) {
            throw new PasswordNotMatchException("密碼錯誤");
        }
    }
}
