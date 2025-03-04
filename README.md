## 下载地址


| 操作系统        | 最新版本 | 下载地址(推荐百度云)   | 下载地址(github)                                                                                                          | 更新日期 |
|--------|------|-------------|-----------------------------------------------------------------------------------------------------------------------|------------|
| windows     | v1.2 | https://pan.baidu.com/s/1_NvicvS_jA7ycjoi35QjTw?pwd=8812 | [fileMask-v1.2.rar](https://github.com/quanzongwei/fileMask/releases/download/fileMask-v1.2-binary/fileMask-v1.2.rar) | 2024-12-21 |
| mac         | v1.2 | https://pan.baidu.com/s/1Deu1luaT-HGgcbqpFZiyXw?pwd=6612 | [fileMask-v1.2.dmg](https://github.com/quanzongwei/fileMask/releases/download/fileMask-v1.2-binary/fileMask-v1.2.dmg) | 2024-12-27|

注：mac系统软件安装完成后，若提示【“fileMask”已损坏，无法打开。 您应该将它移到废纸篓。】，一般不是软件本身的问题，而是Mac启用了安全机制，默认只信任Mac App Store下载的软件以及拥有开发者ID签名的软件，解决方案可参考以下文章，也可自行通过百度或谷歌搜索解决方案：  
1、[macOS 提示：“应用程序” 已损坏，无法打开的解决方法总结](https://sysin.org/blog/macos-if-crashes-when-opening/)   
2、[mac安装应用提示已损坏，打不开。您应该将它移到废纸娄问题解决](https://zhuanlan.zhihu.com/p/617123498)
## 一 fileMask软件简介
该软件主要专注于文件和文件夹的加密和解密  

开发语言: java  
开发周期: 7个月(2019年11月-2020年6月)

## 二 软件特点
界面简洁，功能强大，算法绝对安全(欢迎进行学术性交流)，用户体验良好(支持加密进度展示、随时停止、加解密支持幂等性), 具有思想和灵魂的文件和文件夹加密软件。  软件中一个不经意的小细节，就有可能是一个不错的小创意

## 三 软件界面
### 3.1 windows系统（主页）
![image](https://github.com/quanzongwei/markdown-picture/blob/main/%E7%AE%80%E4%BB%8B-%E4%B8%BB%E7%95%8C%E9%9D%A2.png)  
### 3.2 mac系统（进度页）
<img src="https://github.com/quanzongwei/markdown-picture/blob/main/mac-进度页_975px.png" alt="your_image" style="width:636px; height:632px;" />


## 四 加密类型
### 4.1 三种加密类型
* <strong><font color=#FF0000> 类型一: 文件名称加密 </font></strong>   
支持对文件名称和文件夹名称进行加密，加密速度极快(毫秒级)
* <strong><font color=#FF0000> 类型二: 文件头部加密 </font></strong>  
将文件头部进行加密，加密后无法被正常打开。 比如文本文件打开后所有数据变为乱码，视频音频以及图片文件都无法正常打开，同时，加密速度极快(毫秒级)
* <strong><font color=#FF0000> 类型三: 文件内容加密(即全文加密) </font></strong>  
对文件中，所有的数据进行全文加密，无法通过任何手段进行破解。 安全性最高，但是加密速度较慢(100M耗时1秒，1G耗时10秒，12G耗时4分钟)


### 4.2 组合加密
* 文件名称加密可以和文件头部加密组合使用
* 文件名称加密可以和文件全文加密组合使用

## 五 加密方式
三种加密类型，都对应三种加密方式
* <strong><font color=#FF0000> 方式1：文件夹级联加密 </font></strong>    
对文件夹下所有的子文件夹，以及子文件夹的子文件夹，进行级联加密
* <strong><font color=#FF0000> 方式2：文件夹加密 </font></strong>    
只对选择的文件夹下的文件进行加密， 不会级联加密
* <strong><font color=#FF0000> 方式3：文件加密 </font></strong>  
只对单个文件加密

## 六 解密
解密也支持三种解密方式，文件夹级联解密，文件夹解密和文件解密。系统会自动检测文件被哪种或者哪几种加密方式加密过，然后进行解密

## 七 应用场景
1. 个人笔记本中的文件，同时使用文件名称加密和文件内容加密， 比如小视频，自拍丑照， 再也不用担心电脑借给同学用了，哈哈
2. 公司电脑保存私人文件， 一键级联加密整个文件夹，此时它就是你的专用文件夹，即使离职，文件不删除也无所谓，因为没有人能打开，也没有人知道他是啥
3. 网吧，有些同学经常到网吧上网，而且使用的同一台电脑，有些私人文件想存放在电脑中，又不想被其他人看见。此时该软件就是很好的选择
4. 家庭电脑，假如您和您的家人使用同一台电脑，有些文件是您的私人文件，但是又想存在电脑上，使用该软件即可让这些文件成为你的专属文件
5. 公司电脑达到更换新机的标准，需要删除个人敏感数据后归还旧电脑。如果直接删除，可以“通过数据恢复软件将磁盘已删除的数据进行恢复”，有数据泄露风险，此时可以使用
   该软件进行加密。最安全的文件删除方式是先加密、后删除，即使被恢复，也无法被解密
6. 二手电脑挂闲鱼，可以加密敏感文件后再出售，避免直接删除导致数据被恶意恢复

## 八 作者建议
**<font color=#ff0000>首先</font>**，一般情况下， 使用加密类型一（文件名称加密），就能实现加密效果，一般人无法识别这个文件是啥

**<font color=#ff0000>接着</font>**，如果其他用户会尝试猜测文件类型，然后使用对应的软件直接打开，那么此时使用类型二（文件头部加密），此时，文件无法被打开。例如，我们对mp4文件使用类型二（文件头部加密），那么即使该文件，被拖入到视频播放软件也是无法打开的

**<font color=#ff0000>然后</font>**，如果您是公司高管，军政要员，需要极高安全性的加密，此时可以使用类型三（全文加密）进行加密，该方式，无法通过任何手段进行解密（包括暴力破解），速度相对于类型一和类型二，较慢，单文件预计耗时（100M耗时1秒，1G耗时10秒，12G耗时4分钟）。但是，相对于市场上其他的全文加密软件，作者使用了很巧妙的思路和方法，最大化的提高了加密的速度

## 九 更多阅读 
1. [Part 1 软件使用教程](https://github.com/quanzongwei/fileMask/blob/master/Part%201%20%E8%BD%AF%E4%BB%B6%E4%BD%BF%E7%94%A8%E6%95%99%E7%A8%8B.md)
2. [Part 2 核心加密思想](https://github.com/quanzongwei/fileMask/blob/master/Part%202%20%E6%A0%B8%E5%BF%83%E5%8A%A0%E5%AF%86%E6%80%9D%E6%83%B3.md)
3. [Part 3 版本升级记录](https://github.com/quanzongwei/fileMask/blob/master/Part%203%20%E7%89%88%E6%9C%AC%E5%8D%87%E7%BA%A7%E8%AE%B0%E5%BD%95.md)
## 十 捐赠

> welcome to donate to fileMask


| 微信                                                      | 支付宝                                                     |
|---------------------------------------------------------|---------------------------------------------------------|
| <img src="https://github.com/quanzongwei/markdown-picture/blob/main/alipay_donate_v2.png" width="300" height="350"> | <img src="https://github.com/quanzongwei/markdown-picture/blob/main/weixin_donate_v2.jpg" width="300" height="350"> |

