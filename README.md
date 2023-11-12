
![1](https://github.com/DanrDanr/pet_project/assets/139938245/d7caec1d-6c7b-4e2b-b4eb-1506520b3f60)
![2](https://github.com/DanrDanr/pet_project/assets/139938245/3ebb0cd8-c950-4e16-a30f-5e6a6438158c)

获取验证码 用于登陆和注册的时候的验证 返回的结果可以自己实现个类包一下 方便阅读 
这里成功后用redis存了对应号码的的验证码 方便后面登陆或者注册接口读取 对应前台输入验证码看是否正确
![1699792469734](https://github.com/DanrDanr/pet_project/assets/139938245/9de5e815-8ed0-4154-9de8-189cd3e099ca)
postman接口测试成功返回结果 成功后对应手机号就会收到相应验证短信
![1699792275498](https://github.com/DanrDanr/pet_project/assets/139938245/b070a138-6bfe-4ac1-847c-b10d8fb7c379)
                                                  用户注册接口传参格式

![885912f08b03f0b55bcd95f492d3690](https://github.com/DanrDanr/pet_project/assets/139938245/07950b27-d8e7-495a-85bf-88a090b75f50)


用户登陆和店铺管理员登陆用同一接口  用role判断0是用户1是店铺管理员
![af8cdfbdbbb67d0708e65bda6a1d13a](https://github.com/DanrDanr/pet_project/assets/139938245/10ed3eb4-3864-4c33-a430-cc842dcf9879)

                                            商铺注册                                   
![09904b7dcf911d320dafd3bb998068b](https://github.com/DanrDanr/pet_project/assets/139938245/e38e5b3b-5fa6-4cfc-bef8-b05ff01a27ec)

管理员如果同意那根据商品信息创建该商品admin账号并于该商铺绑定  state=1表示同意 state=2表示拒绝
                    ![3e835b79b2c78e636e43dfaab2ba7f0](https://github.com/DanrDanr/pet_project/assets/139938245/6dcaf6f9-fa53-499a-8e5d-fc4b0984b8b5)



