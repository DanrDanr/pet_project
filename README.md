获取验证码 用于登陆和注册的时候的验证 返回的结果可以自己实现个类包一下 方便阅读 
这里成功后用redis存了对应号码的的验证码 方便后面登陆或者注册接口读取 对应前台输入验证码看是否正确
![1699792469734](https://github.com/DanrDanr/pet_project/assets/139938245/9de5e815-8ed0-4154-9de8-189cd3e099ca)
postman接口测试成功返回结果 成功后对应手机号就会收到相应验证短信
![1699792275498](https://github.com/DanrDanr/pet_project/assets/139938245/b070a138-6bfe-4ac1-847c-b10d8fb7c379)

用户注册接口传参格式
![885912f08b03f0b55bcd95f492d3690](https://github.com/DanrDanr/pet_project/assets/139938245/07950b27-d8e7-495a-85bf-88a090b75f50)

用户登陆和店铺管理员登陆用同一接口  用role判断0是用户1是店铺管理员

![af8cdfbdbbb67d0708e65bda6a1d13a](https://github.com/DanrDanr/pet_project/assets/139938245/10ed3eb4-3864-4c33-a430-cc842dcf9879)
