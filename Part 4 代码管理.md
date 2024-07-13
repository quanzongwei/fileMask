## v1.1 
日期：20240712  
升级内容：
1. mac和windows平台代码融合，方便统一迭代
2. 修复登录密码输错后，重新输入正确密码，解密异常问题，特别感谢"木OK木"同学发现该问题
3. 增加软件版本信息

代码分支：
1. feature/20240712/v1.1/refactor_login_and_show_version
2. release/20240712/v1.1
## v1.0
主要内容：
1. 级联、批量文件加解密能力
2. 支持windows版本、mac版本    

代码分支：  
1. release/20240712/v1.0


## 分支管理原则
1. master为最新的代码
2. feature从master拉取，为开发分支
3. feature开发完成合并到master后，稳定测试通过并打包发布app，最后从master拉取release分支
4. release只允许从master拉取，分支不允许修改，只允许重新升级
5. 一个release版本对应一个app版本
6. feature命名规则：feature/20240712/v1.0/xxxx
7. release命名规则：release/20240712/v1.0/xxxx