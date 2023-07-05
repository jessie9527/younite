package tw.com.younite.service.inter;


import tw.com.younite.entity.OrdersEntity;
import tw.com.younite.mapper.OrdersMapper;

import java.util.Date;

public interface OrdersService {
    //先透過Itemid 去得到 price
    //定義create Order 返回值是integer類型

    Integer createOrder(Integer userId, Integer itemId);

    void updateUnlocked(String mTradeNo, Boolean unlocked, Date purchased);

//    Date setVipDate(OrdersMapper ordersMapper, Date vipDate) ;

    //    @Override
    //    public Date setVipDate(OrdersMapper ordersMapper, Date vipDate)
    Date setVipDate(String mTradeNo, Integer itemId, Date vipDate);

    void lockedVip(Integer id, Boolean unlocked);
}
