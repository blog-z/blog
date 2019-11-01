# **前后端接口详细文档**

## **用户修改密码**

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
  
## **用户忘记密码(邮箱方式，以csdn为模板)**

用户添好邮箱后点击发送验证码`http://localhost:8080/user/getEmailCheck`

参数有`userEmail:1105584793@qq.com`

有两种返回情况

1.邮箱写错了（此邮箱没有注册）

`{
     "status": 1,
     "msg": "此Email未注册"
 }`
 
 2.正确
 
 `{
      "status": 0,
      "msg": "发送email成功"
  }`
  
  之后用户受到验证码并输入了新密码后发送`http://localhost:8080/user/setPasswordByEmail`


  
  `userEmail:1105584793@qq.com`
  
`  passwordNumber:596847`
  
  `password:729512117`
  
  返回值又如下几种
  
  1.验证码失效
  
  `{
       "status": 1,
       "msg": "超时，请重新发送"
   }`
   
   2.修改密码成功
   
   `{
          "status": 0,
          "msg": "修改密码成功"
      }`


### **文章模块**

1.当用户进入网站是，首先就像简书网站一样如下

![Image text](https://github.com/blog-z/blog/blob/master/images/jianshu.png)

图中有如“寝室里的塑料情”，“2019前端面试汇总”等等...

这些数据是从`http://localhost:8080/upload/homeArticle`中获取的，如下图

![Image text](https://github.com/blog-z/blog/blob/master/images/3.png)

其中有一个参数`pageNum:1`这个参数的意思是当点击“阅读更多”的次数，默认是不点击为1，以后每点击一次加1


2.当用户想看那片文章时,直接点击文章,此时前端向`http://localhost:8080/upload/getArticle`发送请求，如下图

![Image text](https://github.com/blog-z/blog/blob/master/images/6.png)

其中有一个参数`articleId:9`,意思是文章的ID

3.当用户看一篇文章超过1分钟后，会触发增加此文章的热度属性，前端向`http://localhost:8080/upload/updateArticleHeat`发送请求，如下图

![Image text](https://github.com/blog-z/blog/blob/master/images/4.png)

其中有参数`articleId:9 articleHeat:2`,意思是文章的ID和文章的热度（此值可以在第二步得到）

4.如果想发表文章（前提是先登录），文章写完后前端可以向`http://localhost:8080/upload/uploadArticle`发送请求，如下图

![Image text](https://github.com/blog-z/blog/blob/master/images/2.png)

其中有参数`articleTitle:文章标题    articleContent:文章内容   userName:用户名`

5.发表后想修改，前端可以向`http://localhost:8080/upload/updateArticle`发送请求，如下图

![Image text](https://github.com/blog-z/blog/blob/master/images/5.png)

其中有参数`articleId:文章ID    articleTitle:文章标题    articleContent:文章内容   userName:用户名`

6.如果想看自己发表了哪些文章，前端可以向`http://localhost:8080/upload/getOwnArticles`发送请求，如下图

![Image text](https://github.com/blog-z/blog/blob/master/images/9.png)

其中有参数`userName:用户名`

7.如果想删除哪个文章，前端可以向`http://localhost:8080/upload/deleteArticle`发送请求，如下图

![Image text](https://github.com/blog-z/blog/blob/master/images/7.png)

