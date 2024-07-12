## 一 软件目录结构
解压fileMask.rar文件，软件目录如下所示:  
![image](https://github.com/quanzongwei/markdown-picture/blob/main/%E6%96%87%E4%BB%B6%E7%9B%AE%E5%BD%95.png)

* authentication目录存放用户认证信息
* doc目录存放使用帮助文档
* icon目录存放软件图标
* jre1.8存放jre文件(源代码大概50k，带上jre后变成了40M，所以主要是jre比较大)
* lib存放jar文件
* logs存放日志文件
* fileMask.exe是应用程序入口，双击运行即可

## 二 软件主界面
### 2.1 首次运行

![image](https://github.com/quanzongwei/markdown-picture/blob/main/%E9%A6%96%E6%AC%A1%E8%BF%90%E8%A1%8C.png)  
首次运行需要输入用户密码，并且确认密码。请牢记您的密码，当一个文件被加密后，只有持有该密码的用户才能解密。

### 2.2 主界面
![image](https://github.com/quanzongwei/markdown-picture/blob/main/%E4%B8%BB%E7%95%8C%E9%9D%A2v2.png)  


#### 2.2.1 加密类型
* <strong><font color=#FF0000> 类型一: 文件名称加密 </font></strong>   
支持对文件名称和文件夹名称进行加密，加密速度极快(毫秒级)
* <strong><font color=#FF0000> 类型二: 文件头部加密 </font></strong>  
将文件头部进行加密，加密后无法被正常打开。 比如文本文件打开后所有数据变为乱码，视频音频以及图片文件都无法正常打开，同时，加密速度极快(毫秒级)
* <strong><font color=#FF0000> 类型三: 文件内容加密(即全文加密) </font></strong>  
对文件中，所有的数据进行全文加密，无法通过任何手段进行破解。 安全性最高，但是加密速度较慢(100M耗时1秒，1G耗时10秒，12G耗时4分钟)  

#### 2.2.2 加密方式
每一种加密类型都对应三种加密方式
* <strong><font color=#FF0000> 方式1：文件夹级联加密 </font></strong>    
对文件夹下所有的子文件夹，以及子文件夹的子文件夹，进行级联加密。
* <strong><font color=#FF0000> 方式2：文件夹加密 </font></strong>    
只对选择的文件夹下的文件进行加密， 不会级联加密
* <strong><font color=#FF0000> 方式3：文件加密 </font></strong>  
只对单个文件加密
#### 文件解密
解密也支持三种解密方式，文件夹级联解密，文件夹解密和文件解密。系统会自动检测文件被哪种或者哪几种加密方式加密过，然后进行解密。

### 2.3 菜单栏
菜单栏中有对应的使用帮助文档
![image](https://github.com/quanzongwei/markdown-picture/blob/main/%E8%8F%9C%E5%8D%95%E6%A0%8F%E5%B8%AE%E5%8A%A9%E6%96%87%E6%A1%A3.png)  
  

联系作者菜单项中可以找到项目源码和作者联系方式  
![image](https://github.com/quanzongwei/markdown-picture/blob/main/%E8%81%94%E7%B3%BB%E4%BD%9C%E8%80%85.png)  

### 2.4 加密/解密处理进度
这个是作者非常满意的一个功能,如下所示:  
![image](https://github.com/quanzongwei/markdown-picture/blob/main/%E8%BF%9B%E5%BA%A6%E6%98%BE%E7%A4%BA.png)  
参数解释:  
1. 展示扫描文件总数和总大小
2. 展示已处理文件总数和已耗时间
3. 预估剩余处理时间
4. 展示当前文件处理进度
5. 展示当前文件预计剩余处理时间
6. 提前停止,如果用户待处理的文件比较多,可以点击提前停止按钮,系统等待当前文件处理完成,到达安全点后,操作完成,此时部分文件处理成功,部分文件未处理    

使用场景: 预估剩余时间以及支持提前停止
## 三 文件加解密示例
#### 3.1 加密类型选择加密类型一(文件名称加密)，加密方式使用文件夹级联加密  
**加密前**  
![image](https://github.com/quanzongwei/markdown-picture/blob/main/%E5%8A%A0%E5%AF%86%E5%89%8D-1.png)  
**加密后**  
![image](https://github.com/quanzongwei/markdown-picture/blob/main/%E5%8A%A0%E5%AF%86%E5%90%8E-1.png)  
加密后文件和文件夹的名称变成一个递增序号，每个文件夹下会多出一个.fileMask文件夹，用于保存递增的序号信息以及加密过的文件夹原始名称信息  

**解密后**  
![image](https://github.com/quanzongwei/markdown-picture/blob/main/%E8%A7%A3%E5%AF%86%E5%90%8E-1.png)    
文件解密后，文件和文件夹名称恢复原样

#### 3.2 加密类型选择加密类型二(文件头部加密)，加密方式使用文件加密  
**加密前**  
![image](https://github.com/quanzongwei/markdown-picture/blob/main/%E5%8A%A0%E5%AF%86%E5%89%8D-2.png)  
**加密后**  
![image](https://github.com/quanzongwei/markdown-picture/blob/main/%E5%8A%A0%E5%AF%86%E5%90%8E-2.png)    
加密后文件内容变成乱码
   
**解密后**  
![image](https://github.com/quanzongwei/markdown-picture/blob/main/%E8%A7%A3%E5%AF%86%E5%90%8E-2.png)    
文件解密后，文件数据恢复原样，如果解密后依然显示乱码，只需要关闭该文件并重新打开即可

#### 3.3 加密类型选择加密类型三(文件全文加密)，加密方式使用文件加密  
**加密前**  
![image](https://github.com/quanzongwei/markdown-picture/blob/main/%E5%8A%A0%E5%AF%86%E5%89%8D-3.png)  
**加密后**  
![image](https://github.com/quanzongwei/markdown-picture/blob/main/%E5%8A%A0%E5%AF%86%E5%90%8E-3.png)  
加密后文件内容变成乱码  
  
**解密后**  
![image](https://github.com/quanzongwei/markdown-picture/blob/main/%E8%A7%A3%E5%AF%86%E5%90%8E-3.png)    
文件解密后，文件数据恢复原样，如果解密后依然显示乱码，只需要关闭该文件并重新打开即可
