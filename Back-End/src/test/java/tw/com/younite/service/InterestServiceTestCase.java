package tw.com.younite.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tw.com.younite.entity.UserProfileEntity;
import tw.com.younite.service.inter.IInterestService;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class InterestServiceTestCase {
    @Autowired
    IInterestService iInterestService;
    @Test
    public void testInterestService() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("游泳");
        list.add("跑步");
        list.add("登山");
        list.add("睡覺");
        iInterestService.setInterests(286, list);
    }
}