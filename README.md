# MinecraftServerDeployTool
自动化 Minecraft 服务端部署工具/开服器 By Helan_XingChen

![](https://img.shields.io/badge/license-GPL--3.0-orange) 
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/a19111b87e8c4fa095c00a160953d07c)](https://app.codacy.com/manual/shaokeyibb/MinecraftServerDeployTool?utm_source=github.com&utm_medium=referral&utm_content=shaokeyibb/MinecraftServerDeployTool&utm_campaign=Badge_Grade_Dashboard)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/shaokeyibb/MinecraftServerDeployTool)
![GitHub Releases](https://img.shields.io/github/downloads/shaokeyibb/MinecraftServerDeployTool/latest/total)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/shaokeyibb/MinecraftServerDeployTool)
> ~~本工具无法自动下载 Java，因为本工具就是 Java 做的，因此请务必安装好 Java 再使用~~
> RHEL和Debian系Linux发行版可直接使用Start.sh脚本，包含安装openjdk和下载此项目并运行

## 特点 Feature

- 支持自动化部署几乎市面上所有服务端核心，从 CraftBukkit 到 Tuinity，从 Vanilla 到 SpongeForge，应有尽有
- 全程文字指导，让任何一个小白也能轻易部署服务端
- 支持 BMCLAPI 镜像资源下载加速，忘记漫长的服务端部署等待!
- 自动同意 EULA，避免手动打开 eula.txt
- 全命令行窗口操作，拒绝难以操作的 UI 界面
- 程序轻量，总大小仅 3MB，源码大小仅 65KB
- 使用 Kotlin 开发，跨平台，更安全
- 开源友好，定制属于你自己的 MCSDT!
- 更多...



## 支持自动部署的服务端核心 Support

        Akarin,
        Arclight,
        CatServer,
        Contigo,
        CraftBukkit,
        Magma,
        Mohist,
        Paper,
        Spigot,
        SpongeForge,
        SpongeVanilla,
        Tuinity,
        Uranium,
        Vanilla,
        VanillaForge
        
## 相关截图 Screenshot
> 以部署 SpongeForge 为例

[![d5cyCR.md.png](https://s1.ax1x.com/2020/08/28/d5cyCR.md.png)](https://imgchr.com/i/d5cyCR)
[![d5cr59.png](https://s1.ax1x.com/2020/08/28/d5cr59.png)](https://imgchr.com/i/d5cr59)
[![d5cBE4.md.png](https://s1.ax1x.com/2020/08/28/d5cBE4.md.png)](https://imgchr.com/i/d5cBE4)
[![d5cwbF.md.png](https://s1.ax1x.com/2020/08/28/d5cwbF.md.png)](https://imgchr.com/i/d5cwbF)
[![d5cDUJ.md.png](https://s1.ax1x.com/2020/08/28/d5cDUJ.md.png)](https://imgchr.com/i/d5cDUJ)
[![d5c681.md.png](https://s1.ax1x.com/2020/08/28/d5c681.md.png)](https://imgchr.com/i/d5c681)

## 下载 Download

[点击前往下载](https://github.com/shaokeyibb/MinecraftServerDeployTool/releases)

## 开源地址 Open-Source

[GitHub](https://github.com/shaokeyibb/MinecraftServerDeployTool) open-source under GNU Public License V3

## 已知问题 Known Problems

- 除非通过使用 `/stop` 命令结束服务端进程，否则即使关闭命令行窗口，服务端也不会被关闭
- 部分服务端核心需要手动指定版本
- 代码写的太烂，拓展性极差

## 其它信息 Other Information

  本工具发行文件中包含了 FastJson，KotlinStd，Zip4J 等实用工具以增加开发效率

  本工具使用了 BMCLAPI，ServerJarsAPI，JenkinsCIAPI，GitHubAPI 以读取和下载文件

## 最后 Latest

如果您觉得我写的还行，那么请千万不要吝惜您的金粒和人气以及 Star，还有回复哦
