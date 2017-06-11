MYAPI
========
### **172.18.69.153：9001/** ###
----
### /login  ###
|POST|类型|简介|
|---|---|---|
|user_id|string|用户id|
|password|string|用户密码|
**成功返回200**
json数据
```
"msg":"success"
"user_name":user_name //我的名字
"config":config //我的个性签名
```
**失败返回403（逻辑错误）**
```
"msg":"illegal username or password"
```

**失败返回404（提交格式或服务器错误）**
```
"msg":error_msg //具体错误信息，比如缺少字段等
```

-----
###/register###
|POST|类型|简介|
|---|---|---|
|user_name|string|用户名|
|password|string|密码|
|config|string|个人简介|

**成功返回200**
```
"msg":"success"
"user_id":user_id
```
**失败返回403（用户名重复）**
```
"msg":"complit user_name"
```
**失败返回404**
```
"msg":error_msg
```

-----
###/heart###
### 心跳信息，客户端定时发送给服务器 ###
### 客户端告诉服务器自己最新的msg_id###
|POST|类型|简介|
|---|---|---|
|user_id|string|用户id|
|password|string|密码,验证用|

**成功返回200**
```
"msg":"success"
"new_msg":{"100000":"2", "100001":"3", user_id:"<msg_count>"} //谁发送了什么信息id，只能拿到信息id而看不到具体内容
"new_shake":["100000", user_id_1, user_id_2] //谁给你发送了震动
"new_friend":{"100000": {"user_name":user_name, "config":config}, 
		user_id:{"user_name":user_name_2, "config":config}} //谁请求添加好友，附带了请求者的id，用户名和个性签名
"new_readed":{"100000":"1", user_id:msg_id} //user_id已读了msg_id
"new_setting":{user_id:{"user_name":user_name, "config":config},} //好友谁更新了名字个性签名
"flag":string //该次心跳的特征值，和时间有关，需要在收到后返回给服务器
```
**没消息返回403**


----
----
###/checkheart###
### 收到心跳信息后客户端返回的值###
|POST|类型|简介|
|----|----|----|
|user_id|string|用户id|
|password|string|密码,验证用|
|flag|string|心跳的特征值|

**成功返回200**
客户端不用处理返回值*/



----
### /postmsg ###
### 向服务器发送信息（普通信息，好友请求，震动信息） ###
|POST|类型|简介|
|---|---|---|
|user_id|string|用户id|
|password|string|密码,验证用|
|---|---|---|
|to_user|string|给谁的信息|
|type|string|0_普通信息 <br> 1_震动 <br> 2_请求好友 |
|msg|string|具体信息，该字段也必须填写，其他类型消息为""即可|


**成功返回200**
```
"msg":"success"
"msg_id":msg_id //所有请求都有msg_id，但是只有普通消息的有用,时间设置为服务器时间
```
**失败返回403**
```
"msg":"illegal userid or password or type"
```
**失败返回404**
```
"msg":error_msg
```


-----
### /getmsg ###
### 获取消息，获取消息的同时会向发送者发送已读标识###
### 只能获取自己的消息，服务器会判断权限 ###

|POST|类型|简介|
|---|---|---|
|user_id|string|用户id|
|password|string|密码,验证用|
|---|---|---|
|contact_id|string|消息的id|

**成功返回200**
```
"msg":"success"
"msg_id":["1", "2", "3",]
"1":{
"detail":msg_detail //具体的消息
"time":date_time //发送者发送消息的时间
"to_user":to_user_id //发送给谁的
"type":0 or 1 or 2 or 3 //消息类型，尽量保持只获得type为0的消息
}
"2":{...}
```
**失败返回403**
```
"msg":"illegal username or password or msg_id"
```
**失败返回404**
```
"msg":error_msg
```

-----
### /change ###
### 改变设置，用户名密码个性签名等等###
### 同时发送更改给所有好友 ###
|POST|类型|简介|
|---|---|---|
|user_id|string|原来的用户id|
|password|string|原来的密码,验证用|
|---|---|---|
|user_name|string|新用户名，会判断重复，为空默认不改变|
|new_password|string|新密码，为空默认不改变|
|config|string|新个性签名|

**成功返回200**
```
"msg":"success"
```

**失败返回403**
```
"msg":"complit user_name"
```

**失败返回404**
```
"msg":error_msg
```

----
### /getcontext ###
### 获取通讯录 ###
|POST|类型|简介|
|---|---|---|
|user_id|string|用户id|
|password|string|密码,验证用|

**成功返回200**
```
"msg":"success"
"user_id":[100000,2000000,3000000,] //你拥有的朋友的user_id列表
"100000":{"user_name":甲, "config":config},
user_id:{"user_name":user_name, "config":config},
```
**失败返回403**
```
"msg":"illegal userid or password"
```

**失败返回404**
```
"msg":error_msg
```
