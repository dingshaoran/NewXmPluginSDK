# 米家卡片配置说明

## 介绍

卡片配置即快捷操作卡片配置，快捷操作卡片可在米家app->设置->快捷操作卡片中打开，效果如下图所示：

![卡片配置](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_jpg_6051efa559e798b0170c67235c415e53.jpg)

可通过快捷操作卡片中的按钮控制设备。

如果遇到卡片配置相关问题，可以在[Github ISSUE](https://github.com/normanren/NewXmPluginSDK/issues)上提交，会有专人处理。

## 卡片配置及调试流程

###注意：如果是设备支持spec协议，可以不用配置卡片，米家已配置好spec设备的卡片。如果spec设备不支持卡片，请联系米家进行添加，无需自己配置。
（支持spec的设备参见：[SPEC支持链接](https://miot-spec.org/miot-spec-v2/instances?status=all)

#### Android调试步骤
1.在[GitHub](https://github.com/MiEcosystem/NewXmPluginSDK/tree/master/card_config)上下载米家debug App。该页面也会持续更新配置相关的FAQ。

2.在米家app->设置->开发者选项中打开本地卡片调试（注意：快捷卡片操作也需要开启，可参见介绍）

3.配置卡片json（见后文）

4.将json文件中的文本拷贝放入mijia_card_config.txt文件(必须保存为UTF-8编码格式，否则卡片会显示乱码)，拷贝至手机根目录即可调试。

![](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_jpg_5a6b7d88aef11867160071c7ec4557db.jpg)

5.调试通过后，通过邮件将json文件发送至米家申请上线
#### IOS调试步骤
卡片配置调试流程（此流程也同时支持小组件） 
入口：米家App -> 我的设置 -> 调试入口 -> 支持本地卡片配置。 
操作: 

（1）开启本地卡片配置开关状态。

（2）将调试json粘贴到文本框中，点击保存即可。 

如图所示：

<img src="http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_jpg_c97cb605c72dc3c6a709b0bb767626f3.jpg" width="240" hegiht="480" align=center />

## 卡片json配置

### 配置整体结构

卡片配置可分为三部分，如下所示：

```json
{
    "models":[ //设备所对应的model列表，参见models配置

    ],
    "props":[ //属性列表，参见props配置

    ],
    "cards":{ //卡片布局与操作列表
        "layout_type":0, //布局类型，参见cards配置
        "card_items":[   //操作类型列表，参见cards配置

        ]
    }
}
```

> json文件不支持注释，配置时请将“//”及后面的文字删除

1. models是此配置适用的设备model，可配置多个
2. props用来配置卡片的属性信息
3. cards用来配置卡片的布局和操作需要发送的命令以及各个操作间的依赖关系

#### models配置

models是此配置适用的设备model，如果有多个model的props和cards是相同的，则多个model可共用同一套配置。

#### props配置

props用来配置卡片展示的属性描述信息，下面的例子是某空气净化器的配置：

```json
{
    "props":[
        {
            "prop_key":"prop.aqi", //上传的key值
            "format":"%.0f",       //格式，此处保留0位小数
            "prop_unit":"",        //属性值的单位，此处为空白
            "ratio":1,             //属性值转换比例
            "prop_name":{          //显示名称
                "zh_CN":"PM2.5",
                "en":"PM2.5",
                "zh_TW":"PM2.5",
                "zh_HK":"PM2.5"
                //还可配置其他多语言
            },
            "prop_extra":[
                {
                    "param_range":{ //prop.aqi的值对应的属性范围，当对应的属性是单个值的时候可由value替代
                        "min":0,    //最小值
                        "max":35    //最大值
                    },
                    "text_color":"#FF30C480", //字体的显示颜色
                    "desc":{         //上述范围所对应的描述，例如这里PM2.5的值为0到34时为优
                        "zh_CN":"优",
                        "en":"Excellent",
                        "zh_TW":"優",
                        "zh_HK":"優"
                    }
                }
                ]
            }
        ]
}

```

##### 主要参数

* prop_key:上传的属性的key，prop_key可有多个，分别对应不同的属性
* format: 指定保留几位小数
* prop_unit：指定属性值的单位
* ratio：若要展示的属性值与上传的属性值不一致存在倍数关系时可通过ratio指定属性值转换比例
* prop_name:指定属性的描述信息，此处可配置多语言，如中文（zh_CN）、英文（en）、台湾（zh_TW）、香港（zh_HK）、西班牙文（es）、俄文（ru）、日文（ja）
* switchStatus：开关类操作需要有switchStatus，用来指定打开的状态，如on、off、auto等

##### 可选参数（prop_extra）

prop_extras是根据属性值需要展示的其他信息，一个prop_extra对应一个prop_key。可根据属性值的不同显示不同的文字。如下是一个空气进化器中一个prop_key对应的prop_extra

```json
        "prop_extra": [
          {
            "param_range": { //prop_key所对应的值为0到35时
              "min": 0,      //最小值
              "max": 35      //最大值
            },
            "text_color": "#FF30C480", //描述的显示颜色
            "desc": { //上述范围所对应的描述，这里PM2.5的值为0到34时为优
              "zh_CN": "优",
              "en": "Excellent",
              "zh_TW": "優",
              "zh_HK": "優"
            }
          },
          {
            "param_range": {  //prop_key所对应的值为36到75时
              "min": 36,
              "max": 75
            },
            "text_color": "#FF76C430", //描述的显示颜色
            "desc": { //上述范围所对应的描述，这里PM2.5的值为36到75时为良
              "zh_CN": "良",
              "en": "Fine",
              "zh_TW": "良",
              "zh_HK": "良"
            }
          }
        ]
```

如上所示，这是空气净化器中一个prop_key对应的prop_extra，prop_extra中可配多组，每组结构如下所示：

```json
         {
            "param_range": { //对应的范围，当对应的属性是单个值的时候可由value替代
              "min":  
              "max": 
            } 
            或
            "value": {      //对应的单个属性值
                
            }
            
            "desc": {       //对信息的描述
              "zh_CN":
              "en":
              "zh_TW":
              "zh_HK":
              //可配置其他多语言
            }
          }
```
* value：对应的具体值，一般用于灯泡、开关等的开关属性或者模式
* param_range：对应的属性范围，一般用于空气净化器等设备
* text_color：文字颜色
* desc：所对应状态的描述

#### cards配置

cards用来配置卡片的布局和操作需要发送的命令以及各个操作间的依赖关系。某油烟机cards配置如下:

```json
    "cards": {
      "layout_type": 3,                     //卡片布局，参见后文
      "card_items": [
        {
          "cardType": 1,                    //操作类型，参见后文
          "prop_key": "prop.power_state",   //上传的属性值，与props中的prop_key一一对应
          "operation": [
            {
              "button_image": {                   //按钮所对应的显示图片
                "normal": "title_btn_power_off",  //正常显示时
                "selected": "title_btn_power_on", //被选中时
                "unable": "title_btn_power_unable"//当按钮无法被点击时
              },
              "prop_value": "0"，
              //prop_key对应的属性值为0时点击会触发param=2的set_power函数
              "method": "set_power",
              "param": [
                "2"
              ]
            },
            {
              "button_image": {
                "normal": "title_btn_power_off",
                "selected": "title_btn_power_on",
                "unable": "title_btn_power_unable"
              },
              "prop_value": "1",         
              "method": "set_power",
              "param": [
                "2"
              ]
            },
            {
              "button_image": {
                "normal": "title_btn_power_on",
                "selected": "title_btn_power_off",
                "unable": "title_btn_power_unable"
              },
              "prop_value": "2"，
              "method": "set_power",
              "param": [
                "0"
              ]
            },
            {
              "button_image": {
                "normal": "title_btn_power_off",
                "selected": "title_btn_power_on",
                "unable": "title_btn_power_unable"
              },
              "prop_value": "3",
              "method": "set_power",
              "param": [
                "2"
              ]
            },
            {
              "button_image": {
                "normal": "title_btn_power_off",
                "selected": "title_btn_power_on",
                "unable": "title_btn_power_unable"
              },
              "prop_value": "4",
              "method": "set_power",
              "param": [
                "2"
              ]
            }
          ]
        },
        {
          "cardType": 8,
          "prop_key": "prop.power_state"
        },
        {
          "cardType": 3,
          "prop_key": "prop.wind_state",
          "operation": [
            {
              "button_name": {  //按钮显示的名字
                "en": "Low",
                "zh_CN": "低挡"
              },
              "button_image": {
                "normal": "btn_auto_off",
                "selected": "btn_auto_on",
                "unable": "btn_auto_unable"
              },
              "prop_value": "1",
                //对应的属性值为1时点击会触发param=1的set_wind函数
              "method": "set_wind",
              "param": [
                "1"
              ],
              "disable_status": [ //不执行函数时的条件
                {
                  "key": "prop.power_state",
                  "value": "0"
                  //当prop.power_state所对应的值为0时，函数不执行
                }
              ]
            },
            {
              "button_name": {
                "en": "Middle",
                "zh_CN": "高挡",
                "zh_TW": "高挡",
                "zh_HK": "高挡"
              },
              "button_image": {
                "normal": "btn_sleep_off",
                "selected": "btn_sleep_on",
                "unable": "btn_sleep_unable"
              },
              "prop_value": "4",
              "method": "set_wind",
              "param": [
                "4"
              ],
              "disable_status": [
                {
                  "key": "prop.power_state",
                  "value": "0"
                }
              ]
            },
            {
              "button_name": {
                "en": "High",
                "zh_CN": "爆炒",
                "zh_TW": "爆炒",
                "zh_HK": "爆炒"
              },
              "button_image": {
                "normal": "popup_icon_love_nor",
                "selected": "popup_icon_love_hig",
                "unable": "popup_icon_love_unable"
              },
              "prop_value": "16",
              "method": "set_wind",
              "param": [
                "16"
              ],
              "disable_status": [
                {
                  "key": "prop.power_state",
                  "value": "0"
                }
              ]
            }
          ]
        }
      ]
    }
```

##### card_items

card_items指定卡片的所有操作，包括是什么类型的操作（开关、模式选择、滑动等）和点击调用函数等等。参考示例如下：

```json
{
          "cardType": 1,                    //操作类型，参见后文
          "prop_key": "prop.power_state",   //上传的属性值，与props中的prop_key一一对应
          "operation": [
            {
              "button_image": {                   //按钮所对应的显示图片
                "normal": "title_btn_power_off",  //正常显示时
                "selected": "title_btn_power_on", //被选中时
                "unable": "title_btn_power_unable"//当按钮无法被点击时
              },
              "prop_value": "0"，
              //对应的属性值为0时点击会触发param=2的set_power函数
              "method": "set_power",
              "param": [
                "2"
              ]
            },
            {
              "button_image": {
                "normal": "title_btn_power_off",
                "selected": "title_btn_power_on",
                "unable": "title_btn_power_unable"
              },
              "prop_value": "1",         
              "method": "set_power",
              "param": [
                "2"
              ]
            },
            {
              "button_image": {
                "normal": "title_btn_power_on",
                "selected": "title_btn_power_off",
                "unable": "title_btn_power_unable"
              },
              "prop_value": "2"，
              "method": "set_power",
              "param": [
                "0"
              ]
            },
            {
              "button_image": {
                "normal": "title_btn_power_off",
                "selected": "title_btn_power_on",
                "unable": "title_btn_power_unable"
              },
              "prop_value": "3",
              "method": "set_power",
              "param": [
                "2"
              ]
            },
            {
              "button_image": {
                "normal": "title_btn_power_off",
                "selected": "title_btn_power_on",
                "unable": "title_btn_power_unable"
              },
              "prop_value": "4",
              "method": "set_power",
              "param": [
                "2"
              ]
            }
          ]
        }
```

* prop_key: 为属性上传的key，可参见props中的prop_key，cards中的prop_key与props中的prop_key一一对应
* cardType：指定操作的类型，目前米家支持以下几种操作：

1.开关类操作

![开关类](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_40b9192acc65af91aab60612295d48c3.png)

开关类操作示例如下：

```json
{
          "cardType": 1,                                //开关类操作
          "prop_key": "prop.power",                     //上传的属性值，与props中的prop_key一一对应
          "operation": [                                
            {
              "button_image": {
                "normal": "title_btn_power_off",
                "selected": "title_btn_power_on",
                "unable": "title_btn_power_unable"
              },
              "button_name": {                          //按钮显示名称
                "zh_CN": "开关",
                "en": "Power",
                "zh_TW": "開關",
                "zh_HK": "開關"
              },
              "prop_value": "off",
                //当开关状态为关时，会调用param=on的set_power函数，即把关闭的灯泡打开
              "method": "set_power",
              "param": [
                "on"
              ]
            },
            {
              "button_image": {
                "normal": "",
                "selected": ""
              },
              "button_name": {
                "zh_CN": "开关",
                "en": "Power",
                "zh_TW": "開關",
                "zh_HK": "開關"
              },
              "prop_value": "on",
              "method": "set_power",
              "param": [
                "off"
              ]
            }
          ]
        }
```



2.两态类操作，比如播放暂停

![播放暂停](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_9cb4eece5e683661e65f0cd749ef3bc1.png)

两态类操作示例如下：

```json
{
          "cardType": 2,                                     //两态类操作
          "prop_key": "prop.MicrophoneMute",                 //上传的属性值，与props中的prop_key一一对应
          "operation": [
            {
              "button_image": {
                "normal": "btn_close_mic_off",
                "selected": "btn_close_mic_on",
                "unable": "btn_close_mic_disable"
              },
              "button_name": {                               //按钮名称
                "en": "On",
                "zh_CN": "麦克已启用",
                "zh_TW": "麥克已啓用",
                "zh_HK": "麥克已啓用"
              },
              "prop_value": "true",
                ////当麦克为开启（true）时，会调用param=true的set_microphone_MicrophoneMute函数，注意，此处的参数根据具体设备而定，例如这里的true代表警用麦克，false代表启用麦克
              "method": "set_microphone_MicrophoneMute",
              "param": [
                true
              ]
            },
            {
              "button_image": {
                "normal": "",
                "selected": "",
                "unable": ""
              },
              "button_name": {
                "en": "Off",
                "zh_CN": "麦克已禁用",
                "zh_TW": "麥克已禁用",
                "zh_HK": "麥克已禁用"
              },
              "prop_value": "false",
              "method": "set_microphone_MicrophoneMute",
              "param": [
                false
              ]
            }
          ]
        }
```



3.模式选择类操作

![模式选择](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_689664d6851b9a35f3da0446b4b45f8e.png)

模式选择类操作示例如下:

```json
{
          "cardType": 3,                                    //模式选择类操作
          "prop_key": "prop.mode",                          //上传的属性值，与props中的prop_key一一对应
          "operation": [
            {
              "button_name": {
                "en": "Auto",
                "zh_CN": "自动"
              },
              "button_image": {
                "normal": "btn_auto_off",
                "selected": "btn_auto_on",
                "unable": "btn_auto_unable"
              },
              "prop_value": "auto",
                //点击自动按钮（auto）时调用param=auto的set_mode函数
              "method": "set_mode",
              "param": [
                "auto"
              ],
              "disable_status": [                           //当prop.power对应的值为off时点击无效
                {
                  "key": "prop.power",
                  "value": "off"
                }
              ]
            },
            {
              "button_name": {
                "en": "Cool",
                "zh_CN": "制冷"
              },
              "button_image": {
                "normal": "popup_icon_cold_nor",
                "selected": "popup_icon_cold_hig",
                "unable": "popup_icon_cold_unable"
              },
              "prop_value": "cool",
              "method": "set_mode",
              "param": [
                "cool"
              ],
              "disable_status": [
                {
                  "key": "prop.power",
                  "value": "off"
                }
              ]
            },
            {
              "button_name": {
                "en": "Heat",
                "zh_CN": "制热"
              },
              "button_image": {
                "normal": "popup_icon_sun_nor",
                "selected": "popup_icon_sun_hig",
                "unable": "popup_icon_sun_unable"
              },
              "prop_value": "heat",
              "method": "set_mode",
              "param": [
                "heat"
              ],
              "disable_status": [
                {
                  "key": "prop.power",
                  "value": "off"
                }
              ]
            }
          ]
        }
```



4.加减调节类操作

![](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_2834dc10793b56ffcd9eadc39f1c22b5.png)

加减调解类操作示例如下：

```json
{
          "cardType": 4,                                     //加减调解类操作
          "prop_key": "prop.tar_temp",                       //上传的属性值，与props中的prop_key一一对应
          "param_range": {                                   //prop_key对应的属性的范围
            "min": 16,
            "max": 31
          },
          "param_delta": 0.5,                                //步长，即每次按加减按钮变化的值
          "param_type": [                                    //参见后文
            {
              "index": 0,
              "type": "JSONArray"
            },
            {
              "type": "double"
            }
          ],
          "operation": [
            {
              "method": "set_tar_temp",                       //调用set_tar_temp函数，参数（param）为当前显示的值
              "disable_status": [                             //当关闭（prop.power为off）或为自动模式(prop.mode为auto)时无效
                {
                  "key": "prop.power",
                  "value": "off"
                },
                {
                  "key": "prop.mode",
                  "value": "auto"
                }
              ]
            }
          ]
        }
```



5.无极滑动调节类操作

![](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_327644c6c3a80d55e4a099153f3e4659.png)

无极滑动调节类操作示例如下：

```json
{
          "cardType": 5,                               //无极调节类操作
          "prop_key": "prop.bri",                      //上传的属性值，与props中的prop_key一一对应
          "param_range": {
            "min": 1,
            "max": 100
          },
          "start_color": "",                            //可不配置，由米家指定，这两个属性可配置滑动条颜色
          "end_color": "",
          "small_image": "seekbar_thumb_light",         //小图标（如上图中的灯光图标）
          "operation": [
            {
              "method": "set_bright",                   //可参考加减调节类操作
              "disable_status": [
                {
                  "key": "prop.power",
                  "value": "off"
                }
              ]
            }
          ],
          "param_type": [                               //参见后文
            {
              "type": "JSONArray",
              "index": "0"
            },
            {
              "type": "int"
            }
          ]
        }
```



6.摄像机类操作

![](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_7ffdeec83f929271eeec8da5b439c6c1.png)

摄像机类操作示例如下:

```json
{
          "cardType": 6                                    //摄像机类操作
        }
```



7.文字+数字类操作

![](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_04270ec597acac32551e38989d27b9f4.png)

文字+数字类操作示例如下：

```json
{
          "cardType": 7,                                  //文字+数字类操作
          "prop_key": "event.pure_water_record"           //上传的属性值，与props中的prop_key一一对应
        }
```



8.文字+文字类操作

![](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_5dc1e9a2b75cc5ed62a607957d9d03b1.png)

文字+文字类操作示例如下：

```json
{
          "cardType": 8,                                   //文字+文字类操作
          "prop_key": "prop.wash_process"                  //上传的属性值，与props中的prop_key一一对应
        }
```

9.时间+数字类操作（*暂不支持*）

11.渐变色无极滑动调节

![](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_415c976d399099e2d4f8b061f6746342.png)

渐变色无极滑动调节示例如下:

```json
{
          "cardType": 11,                                  //渐变色无极滑动调节操作
          "prop_key": "prop.cct",                          //上传的属性值，与props中的prop_key一一对应
          "param_range": {
            "min": 1,
            "max": 100
          },
          "start_color": "",                               //可不配置，由米家指定，这两个属性可配置滑动条颜色
          "end_color": "",
          "small_image": "",                               //可不配置，由米家指定，与无极滑动调节操作的小图标相似
          "operation": [
            {
              "method": "set_cct",                         //可参考加减调节类操作（cardType == 4）
              "disable_status": [
                {
                  "key": "prop.power",
                  "value": "off"
                }
              ]
            }
          ],
          "param_type": [
            {
              "type": "JSONArray",
              "index": "0"
            },
            {
              "type": "int"
            }
          ]
        }
```



12.挡位操作

![](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_297de6591887cb3a2aa37e74d13e163a.png)

挡位操作示例如下所示：

```json
{
          "cardType": 12,                                     //挡位操作
          "prop_key": "prop.mode",                            //上传的属性值，与props中的prop_key一一对应
          "operation": [
            {
              "button_name": {                                //此项与prop_value对应，例如这里prop_value为silent时显示为1档
                "en": "One",
                "zh_CN": "1挡",
                "zh_TW": "1擋",
                "zh_HK": "1擋"
              },
              "button_image": {
                "normal": "",
                "selected": ""
              },
              "prop_value": "silent",
              "method": "set_mode",                           //可参考加减调节类操作(cardType==4)                 
              "param": [
                "silent"
              ],
              "disable_status": [
                {
                  "key": "prop.power",
                  "value": "off"
                }
              ]
            },
            {
              "button_name": {
                "en": "Two",
                "zh_CN": "2挡",
                "zh_TW": "2擋",
                "zh_HK": "2擋"
              },
              "button_image": {
                "normal": "",
                "selected": ""
              },
              "prop_value": "medium",
              "method": "set_mode",
              "param": [
                "medium"
              ],
              "disable_status": [
                {
                  "key": "prop.power",
                  "value": "off"
                }
              ]
            },
            {
              "button_name": {
                "en": "Three",
                "zh_CN": "3挡",
                "zh_TW": "3擋",
                "zh_HK": "3擋"
              },
              "button_image": {
                "normal": "",
                "selected": ""
              },
              "prop_value": "high",
              "method": "set_mode",
              "param": [
                "high"
              ],
              "disable_status": [
                {
                  "key": "prop.power",
                  "value": "off"
                }
              ]
            }
          ]
        }
```

###### operation

> 各个操作所对应的operation可参考上文cardType所给出的示例

指定需要进行的操作，且operation也可配置多组，分别对应着当前的值（prop_value，参见后文)，例如油烟机某个card_item对应的operation如下：

```json
"operation": [
            {
              "button_image": {                   //按钮所对应的显示图片
                "normal": "title_btn_power_off",  //正常显示时
                "selected": "title_btn_power_on", //被选中时
                "unable": "title_btn_power_unable"//当按钮无法被点击时
              },
              "prop_value": "0"，
              //对应的属性值为0时点击会触发param=2的set_power函数
              "method": "set_power",
              "param": [
                "2"
              ]
            },
            {
              "button_image": {
                "normal": "title_btn_power_off",
                "selected": "title_btn_power_on",
                "unable": "title_btn_power_unable"
              },
              "prop_value": "1",         
              "method": "set_power",
              "param": [
                "2"
              ]
            },
            {
              "button_image": {
                "normal": "title_btn_power_on",
                "selected": "title_btn_power_off",
                "unable": "title_btn_power_unable"
              },
              "prop_value": "2"，
              "method": "set_power",
              "param": [
                "0"
              ]
            },
            {
              "button_image": {
                "normal": "title_btn_power_off",
                "selected": "title_btn_power_on",
                "unable": "title_btn_power_unable"
              },
              "prop_value": "3",
              "method": "set_power",
              "param": [
                "2"
              ]
            },
            {
              "button_image": {
                "normal": "title_btn_power_off",
                "selected": "title_btn_power_on",
                "unable": "title_btn_power_unable"
              },
              "prop_value": "4",
              "method": "set_power",
              "param": [
                "2"
              ]
            }
          ]
        }
```

* method：调用的函数名
* button_image：指定button使用的图片，此项可不配置由米家来指定
* button_name：指定展示的button名称，若不需要展示名称可不配置
* prop_value：进行此操作时的实际属性值
* disable_status：指定操作不可用的条件，可配置多个条件，满足一项即不可用
* enable_status：指定操作可用的条件，可配置多个条件，满足一项即可用
* param_type：指定发送命令时参数组装，也可配置多组，例如如果参数需要组装成jsonArray的形式：[100]需要如下配置
```json
"param_type":[
                      {
                          "type":"JSONArray",  //参数类型
                          "index":"0"          //索引
                      },
                      {
                          "type":"int"         //参数类型
                      }
                  ]
```
* param_range与param_delta：加减调节和无极调节操作需要配置，例如
```json
"param_range":{
                      "min":16,
                      "max":31
                  },
               "param_delta":0.5 //步长
```
##### layout_type

指定卡片的具体布局，目前米家支持六种布局，具体如下所示：

1. 布局0（只能加入一种非滑动类且非模式选择类操作）

![](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_421567c8dbcb76d11ab6b60d07563ec3.png)

2. 布局1（可加入两种操作：非滑动类操作且非模式选择类操作和一种滑动类操作）

![](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_d004103ceb9a97aa9895ad53bb5f588f.png)

3. 布局2（可加入两种非滑动类且非模式选择类操作）

![](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_6459b6ec1dae8d5dd80a6f2a4c7241f9.png)

4. 布局3（可多种组合操作，最多可加入两种滑动类操作，其中一种滑动类操作可被1到3种非滑动类操作或模式选择类操作替代）
下图是两个控件（一个空气净化器数字显示控件和一个多模式控件）加一个右上角开关控件的组合。

![](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_21bec8d3b479c436d20a4e68632c3ae5.png)

5. 布局4（可加入三种非滑动类操作或者一种模式选择类操作）

![](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_7a334ac1ae96b672c85c83b45b4dfe8a.png)

6. 布局5（可加入三种滑动类操作）

![](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_ea36ec3eac18092e5b1abf2a953eb370.png)

布局详细情况可参见[cardLayout](http://cdn.cnbj0.fds.api.mi-img.com/miio.files/resource_package/201810171148_card_config_des.zip)

## 卡片配置实例

配置仅供参考，具体配置需要根据设备而定

### 空气净化器

```json

  {
    "models": [
      "XXX.airpurifier.XXX",
      "XXX.airpurifier.XXX1"                //设备的model名，可配置多个  
    ],
    "props": [
      {
        "prop_key": "prop.aqi",            //上传的key，与cards配置中的prop_key一一对应
        "format": "%.0f",                  //保留0位小数
        "prop_name": {                     //属性名
          "zh_CN": "PM2.5",
          "en": "PM2.5",
          "zh_TW": "PM2.5",
          "zh_HK": "PM2.5"
        },
        "prop_extra": [                    //额外的属性，在prop.sqi对应的属性值为0到35时空气质量为优
          {
            "param_range": {                
              "min": 0,
              "max": 35
            },
            "text_color": "#FF30C480",      //显示的字体的颜色
            "desc": {
              "zh_CN": "优",
              "en": "Excellent",
              "zh_TW": "優",
              "zh_HK": "優"
            }
          },
          {
            "param_range": {
              "min": 36,
              "max": 75
            },
            "text_color": "#FF76C430",
            "desc": {
              "zh_CN": "良",
              "en": "Fine",
              "zh_TW": "良",
              "zh_HK": "良"
            }
          },
          {
            "param_range": {
              "min": 76,
              "max": 115
            },
            "text_color": "#FFE6BB25",
            "desc": {
              "zh_CN": "轻度污染",
              "en": "Light pollution",
              "zh_TW": "輕度污染",
              "zh_HK": "輕度污染"
            }
          },
          {
            "param_range": {
              "min": 116,
              "max": 150
            },
            "text_color": "#FFE67D19",
            "desc": {
              "zh_CN": "中度污染",
              "en": "Moderate pollution",
              "zh_TW": "中度污染",
              "zh_HK": "中度污染"
            }
          },
          {
            "param_range": {
              "min": 151,
              "max": 250
            },
            "text_color": "#CCF13312",
            "desc": {
              "zh_CN": "重度污染",
              "en": "Heavy pollution",
              "zh_TW": "重度污染",
              "zh_HK": "重度污染"
            }
          },
          {
            "param_range": {
              "min": 251,
              "max": 1000
            },
            "text_color": "#E5B60E11",
            "desc": {
              "zh_CN": "严重污染",
              "en": "Serious pollution",
              "zh_TW": "嚴重污染",
              "zh_HK": "嚴重污染"
            }
          }
        ]
      },
      { 
        "prop_key": "prop.mode",             //上传的key，与cards配置中的prop_key一一对应
        "prop_name": {
          "zh_CN": "开关",
          "en": "Switch",
          "zh_TW": "開關",
          "zh_HK": "開關"
        },
        "switchStatus": [                    //开关类操作需要有switchStatus，在cards中prop_key为prop.mode的card_item所对应的cardType为1，即开关类操作，所以这里需要配置switchStatus   
          "auto",
          "strong",
          "silent"
        ]
      }
    ],
    "cards": {
      "layout_type": 3,                     //布局3
      "card_items": [                       //卡片操作配置，可根据layout_type配置多种
        {
          "cardType": 1,                    //开关类操作
          "prop_key": "prop.mode",
          "operation": [
            {
              "button_image": {
                "normal": "title_btn_power_off",
                "selected": "title_btn_power_on",
                "unable": "title_btn_power_unable"
              },
              "button_name": {
                "en": "Power",
                "zh_CN": "开关",
                "zh_TW": "開關",
                "zh_HK": "開關"
              },
              "prop_value": "auto",
              //当prop.mode对应的值为auto时，点击按钮会调用param=idle的set_mode函数  
              "method": "set_mode",
              "param": [
                "idle"
              ]
            },
            {
              "button_image": {
                "normal": "",
                "selected": ""
              },
              "button_name": {
                "en": "Power",
                "zh_CN": "开关",
                "zh_TW": "開關",
                "zh_HK": "開關"
              },
              "prop_value": "idle",
              "method": "set_mode",
              "param": [
                "auto"
              ]
            },
            {
              "button_image": {
                "normal": "",
                "selected": ""
              },
              "button_name": {
                "en": "Power",
                "zh_CN": "开关",
                "zh_TW": "開關",
                "zh_HK": "開關"
              },
              "prop_value": "strong",
              "method": "set_mode",
              "param": [
                "idle"
              ]
            },
            {
              "button_image": {
                "normal": "",
                "selected": ""
              },
              "button_name": {
                "en": "Power",
                "zh_CN": "开关",
                "zh_TW": "開關",
                "zh_HK": "開關"
              },
              "prop_value": "silent",
              "method": "set_mode",
              "param": [
                "idle"
              ]
            }
          ]
        },
        {
          "cardType": 17,
          "prop_key": "prop.aqi"
        },
        {
          "cardType": 3,
          "prop_key": "prop.mode",
          "operation": [
            {
              "button_name": {
                "en": "Automatic",
                "zh_CN": "自动",
                "zh_TW": "自動",
                "zh_HK": "自動"
              },
              "button_image": {
                "normal": "btn_auto_off",
                "selected": "btn_auto_on",
                "unable": "btn_auto_unable"
              },
              "prop_value": "auto",
              "method": "set_mode",
              "param": [
                "auto"
              ],
              "disable_status": [
                {
                  "key": "prop.mode",
                  "value": "idle"
                }
              ]
            },
            {
              "button_name": {
                "en": "High-speed",
                "zh_CN": "高速",
                "zh_TW": "高速",
                "zh_HK": "高速"
              },
              "button_image": {
                "normal": "btn_highspeed_off",
                "selected": "btn_highspeed_on",
                "unable": "btn_highspeed_unable"
              },
              "prop_value": "strong",
              "method": "set_mode",
              "param": [
                "strong"
              ],
              "disable_status": [
                {
                  "key": "prop.mode",
                  "value": "idle"
                }
              ]
            },
            {
              "button_name": {
                "en": "Sleep",
                "zh_CN": "睡眠",
                "zh_TW": "睡眠",
                "zh_HK": "睡眠"
              },
              "button_image": {
                "normal": "btn_sleep_off",
                "selected": "btn_sleep_on",
                "unable": "btn_sleep_unable"
              },
              "prop_value": "silent",
              "method": "set_mode",
              "param": [
                "silent"
              ],
              "disable_status": [
                {
                  "key": "prop.mode",
                  "value": "idle"
                }
              ]
            }
          ]
        }
      ]
    }
  }
```

### 灯

```json
{
    "models": [
      "XXX.light.XXX",
      "XXX.light.XXX1"                          
    ],
    "props": [
      {
        "prop_extra": [
          {
            "desc": {
              "en": "Close",
              "zh_CN": "关闭",
              "zh_TW": "關閉",
              "zh_HK": "關閉"
            },
            "value": "on"
          },
          {
            "desc": {
              "en": "Open",
              "zh_CN": "打开",
              "zh_TW": "打開",
              "zh_HK": "打開"
            },
            "value": "off"
          }
        ],
        "prop_key": "prop.power",
        "prop_name": {
          "en": "Power",
          "zh_CN": "开关",
          "zh_TW": "開關",
          "zh_HK": "開關"
        },
        "switchStatus": [
          "on"
        ]
      },
      {
        "prop_key": "prop.bri",
        "prop_name": {
          "en": "Set bright",
          "zh_CN": "亮度调节",
          "zh_TW": "亮度調節",
          "zh_HK": "亮度調節"
        }
      },
      {
        "prop_key": "prop.cct",
        "supportType": [
          1
        ],
        "prop_name": {
          "en": "Set cct",
          "zh_CN": "色温调节",
          "zh_TW": "色溫調節",
          "zh_HK": "色溫調節"
        }
      }
    ],
    "cards": {
      "layout_type": 3,
      "card_items": [
        {
          "cardType": 1,
          "prop_key": "prop.power",
          "operation": [
            {
              "button_image": {
                "normal": "title_btn_power_off",
                "selected": "title_btn_power_on",
                "unable": "title_btn_power_unable"
              },
              "button_name": {
                "zh_CN": "开关",
                "en": "Power",
                "zh_TW": "開關",
                "zh_HK": "開關"
              },
              "prop_value": "off",
              "method": "set_power",
              "param": [
                "on"
              ]
            },
            {
              "button_image": {
                "normal": "",
                "selected": ""
              },
              "button_name": {
                "zh_CN": "开关",
                "en": "Power",
                "zh_TW": "開關",
                "zh_HK": "開關"
              },
              "prop_value": "on",
              "method": "set_power",
              "param": [
                "off"
              ]
            }
          ]
        },
        {
          "cardType": 11,
          "prop_key": "prop.cct",
          "param_range": {
            "min": 1,
            "max": 100
          },
          "start_color": "",
          "end_color": "",
          "small_image": "",
          "operation": [
            {
              "method": "set_cct",
              "disable_status": [
                {
                  "key": "prop.power",
                  "value": "off"
                }
              ]
            }
          ],
          "param_type": [
            {
              "type": "JSONArray",
              "index": "0"
            },
            {
              "type": "int"
            }
          ]
        },
        {
          "cardType": 5,
          "prop_key": "prop.bri",
          "param_range": {
            "min": 1,
            "max": 100
          },
          "start_color": "",
          "end_color": "",
          "small_image": "seekbar_thumb_light",
          "operation": [
            {
              "method": "set_bright",
              "disable_status": [
                {
                  "key": "prop.power",
                  "value": "off"
                }
              ]
            }
          ],
          "param_type": [                          //指定发送命令时参数组装,可参见cards配置
            {
              "type": "JSONArray",
              "index": "0"
            },
            {
              "type": "int"
            }
          ]
        }
      ]
    }
  }
```

##### card_items

1、首页宫格显示的信息是卡片的一部分信息时，添加supportGrid字段
```json
"card_items":[
            {
                "supportGrid":1,
                "cardType":18,
                "prop_key":"prop.doorbell_status"
            }
]
```

2、如果首页宫格显示的信息和卡片显示不一样时，添加grid_items字段，此字段与card_items并列
```json
"grid_items":[
            {
                "cardType":18,
                "prop_key":"prop.doorbell_status"
            }
]
```

3、卡片首页目前支持的显示：

1）单个操作（最多显示一个，card_type=1/2）

2）文字（最多显示一个，card_type=8）

3）文字+数字（最多显示两个，card_type=7）

4）文字+时间（最多显示一个，card_type=10）

5）文字+时间（最多显示一个，其中文字和时间均为从服务器获取，card_type=18）

6）文字+文字（最多显示两个，card_type=8）

##### IOS小组件（Widget）配置说明
###### 介绍

目前小组件（快捷操作、设备状态）与卡片共用一套配置，故加此说明。
效果如图所示：

<img src="http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_png_cbdc117033cdb0a465300e3cdbee9995.png" width="200" hegiht="400" align=center />

###### 配置小组件
1.目前小组件的显示是根据卡片配置 “card_items” 中cardType进行展示的，如要设备支持小组件，只需让设备的card_items中属性设置成小组件所支持的cardType类型即可。
```
"card_items":[
            {
                "supportGrid":1,
                "cardType":18,
                "prop_key":"prop.doorbell_status"
            }
]
```
2.目前米家设备状态支持的cardType类型：7，8，9，10，17，18且相应prop的supportType包含2。

3.目前米家快捷操作支持的cardType类型：1，2，3，12。

4.特别注意(必须要配置的)：

 如需设备支持小组件或者线上已支持的小组件，必须配置以下字段：
 
 （1）设备状态：必须在设备状态所支持的prop中设置”prop_name”，没配置的话，下图标红处则为空。（需包含中英文，可加小语种）
<img src="http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_jpg_a64147537df24230fb316e7350b85a88.jpg" width="240" hegiht="200" align=center />
 
配置如下：
```
    {
                "prop_key":"prop.st_temp_dec",
                "prop_unit":"℃",
                "supportType":[
                    1
                ],
                "prop_name":{
                    "zh_CN":"温度",
                    "en":"Temperature"
                },
                "ratio":0.1,
                "format":"%.0f"
   },
```
（2）快捷操作：同设备状态，在快捷操作所支持的prop中设置”prop_name”（需包含中英文，可加小语种）。还需在所支持设备的“card_items” -> “operation”中，对每个操作配置“button_name”（需包含中英文，可加小语种）、“button_image”，显示如下图所示：

<img src="http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_jpg_b715e5a4cd271852168f95dc2ab6812a.jpg" width="240" hegiht="160" align=center />


配置如下： 
```
{
                            "param":[
                                "on"
                            ],
                            "method":"set_power",
                            "prop_value":"off",
                            "button_name":{
                                "en":"Power",
                                "zh_HK":"電源",
                                "zh_CN":"电源",
                                "zh_TW":"電源"
                            },
                            "button_image":{
                                "selected":"btn_single_on",
                                "unable":"btn_single_unable",
                                "normal":"btn_single_off"
                            }
}
```

### 备注
卡片图片资源参考
```
http://cdn.cnbj0.fds.api.mi-img.com/miio.files/commonfile_rar_47bd28debf27b4714d72b144b82086e0.rar
```
