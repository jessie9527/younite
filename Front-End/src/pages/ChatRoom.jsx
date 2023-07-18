// ChatRoom.js
import React, { useEffect, useState } from 'react';
import SideNav from '../component/SideNav';
import Chat from '../component/Message/Chat';
import '../styles/chatroom.scss';
import MessageBar from '../component/Message/MessageBar';
import axios from 'axios';

const ChatRoom = () => {
  const [friendList, setFriendList] = useState([]);
  const [chatRoomInfo, setChatRoomInfo] = useState([]);
  const [userInfo, setUserInfo] = useState();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const resUserInfo = await axios.get('/users/profile');
        const resFriendList = await axios.get('/getProfile/friendList');
        const id = resUserInfo.data.data.userId;
        console.log('id', id);
        const friendLists = [];
        const roomList = [];
        resFriendList.data.forEach(async (element) => {
          friendLists.push({ id: element.userid, key: element.userid });
          let room = await axios.get(`/getProfile/getRoom/${id}/${element.userid}`);
          roomList.push(room);
        });
        const resChatRoomInfo = await axios.get('/message/find/32');
        setFriendList(resFriendList.data);
        setChatRoomInfo(resChatRoomInfo.data);
        setUserInfo(resUserInfo.data);
      } catch (error) {
        console.error(error);
      }
    };
    fetchData();
  }, []);

  console.log('friendList', friendList);
  console.log('chatRoomInfo', chatRoomInfo);
  console.log('userInfo', userInfo);

  return (
    <>
      <SideNav />
      <div className='container-fluid'>
        <div className='row chatroom-main'>
          <div className='col-3 bg-secondary chatroom-list'>
            <MessageBar friendList={friendList} />
          </div>
          <div className='col-9 text-white p-0'>
            {userInfo &&
              userInfo.data && ( // 添加條件判斷，确保userInfo和userInfo.data都存在时再渲染Chat组件
                <Chat
                  friendList={friendList}
                  chatRoomInfo={chatRoomInfo}
                  userInfo={userInfo}
                  fullName={userInfo.data.fullName} // 將 fullName 設置為 userInfo.data.fullName
                  profileAvatar={userInfo.data.profileAvatar}
                />
              )}
          </div>
        </div>
      </div>
    </>
  );
};

export default ChatRoom;
