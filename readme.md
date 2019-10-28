### **前后端接口详细文档**

用户忘记了密码需要修改密码（注意用户账号没有忘记的前提下）

用户在登录页面发现登录不上，提示：密码错误。用户就会点击“忘记密码”

前端应该向`http://localhost:8080/user/getQuestion`发送`userName:qingchun`

后端得到前端发送过来的用户名，
 
 1.如果没有此用户返回数据如下
 
 `
 {
     "status": 1,
     "msg": "用户不存在"
 }
 `
 
2.如果有此用户会向前端发送此用户的密保问题（此为用户注册是添的）

`{
     "status": 0,
     "msg": "0"
 }`
 
 此时页面在回答密保问题界面，
 
 用户回答完后，向后端`http://localhost:8080/user/setAnswer`发送`userName:zhuqingchun和answer:密保答案`

后端验证

1.回答错误

`{
     "status": 1,
     "msg": "问题的答案错误"
 }`
 
 2.回答正确
 
 `{
      "status": 0,
      "msg": "17c8a86d-5208-47c0-9439-5d408d1fa889"
  }`
  
  注意msg的值有效时间为60s
  
  验证通过后就可以修改密码了（此时页面在修改密码页面）
  
  修改密码后向后端`http://localhost:8080/user/setPassword`发送
  
  `userName:qingchun    password:7297492    token:17c8a86d-5208-47c0-9439-5d408d1fa889`
  
  后端受到后有如下几种结果
  
  1.token超时了(此时用户需要重新回答密保问题)
  
  `{
       "status": 1,
       "msg": "操作时间超时，请重新操作"
   }`
   
  2.修改成功
  
  `{
       "status": 0,
       "msg": "修改密码成功"
   }`
  
  这时用户就可以用新密码登录了
  