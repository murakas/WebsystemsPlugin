@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  WebsystemsPlugin startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and WEBSYSTEMS_PLUGIN_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\WebsystemsPlugin.jar;%APP_HOME%\lib\QSystem.jar;%APP_HOME%\lib\AbsoluteLayout-RELEASE111.jar;%APP_HOME%\lib\antlr-2.7.7.jar;%APP_HOME%\lib\appframework-1.0.3.jar;%APP_HOME%\lib\barbecue-1.5-beta1.jar;%APP_HOME%\lib\basicplayer3.0.jar;%APP_HOME%\lib\beansbinding-1.2.1.jar;%APP_HOME%\lib\byte-buddy-1.8.17.jar;%APP_HOME%\lib\c3p0-0.9.5.2.jar;%APP_HOME%\lib\classmate-1.3.4.jar;%APP_HOME%\lib\commons-beanutils-1.9.3.jar;%APP_HOME%\lib\commons-cli-1.4.jar;%APP_HOME%\lib\commons-codec-1.11.jar;%APP_HOME%\lib\commons-collections-3.2.2.jar;%APP_HOME%\lib\commons-configuration2-2.2.jar;%APP_HOME%\lib\commons-dbcp-1.4.jar;%APP_HOME%\lib\commons-digester-2.1.jar;%APP_HOME%\lib\commons-jexl3-3.1.jar;%APP_HOME%\lib\commons-lang3-3.7.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-pool-1.6.jar;%APP_HOME%\lib\compiler-0.9.7.jar;%APP_HOME%\lib\core-3.4.1.jar;%APP_HOME%\lib\dom4j-2.1.1.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\groovy-all-1.1-rc-1.jar;%APP_HOME%\lib\gson-2.8.5.jar;%APP_HOME%\lib\guava-30.0-jre.jar;%APP_HOME%\lib\h2-1.4.197.jar;%APP_HOME%\lib\hibernate-c3p0-5.3.6.Final.jar;%APP_HOME%\lib\hibernate-commons-annotations-5.0.4.Final.jar;%APP_HOME%\lib\hibernate-core-5.3.6.Final.jar;%APP_HOME%\lib\hibernate-jpa-2.1-api-1.0.2.Final.jar;%APP_HOME%\lib\httpcore-4.1.jar;%APP_HOME%\lib\httpcore-nio-4.1.jar;%APP_HOME%\lib\itext-2.1.7.jar;%APP_HOME%\lib\jandex-2.0.5.Final.jar;%APP_HOME%\lib\jasperreports-5.2.0.jar;%APP_HOME%\lib\jasperreports-fonts-4.0.0.jar;%APP_HOME%\lib\javahelp-2.0.05.jar;%APP_HOME%\lib\javassist-3.23.1-GA.jar;%APP_HOME%\lib\javax.activation-api-1.2.0.jar;%APP_HOME%\lib\javax.persistence-api-2.2.jar;%APP_HOME%\lib\javax.servlet-3.1.1.jar;%APP_HOME%\lib\jboss-logging-3.3.2.Final.jar;%APP_HOME%\lib\jboss-transaction-api_1.2_spec-1.1.1.Final.jar;%APP_HOME%\lib\jcalendar-1.4.jar;%APP_HOME%\lib\jcommon-1.0.16.jar;%APP_HOME%\lib\jetty-annotations-9.4.20.v20190813.jar;%APP_HOME%\lib\jetty-continuation-9.4.20.v20190813.jar;%APP_HOME%\lib\jetty-http-9.4.20.v20190813.jar;%APP_HOME%\lib\jetty-io-9.4.20.v20190813.jar;%APP_HOME%\lib\jetty-runner-9.4.20.v20190813.jar;%APP_HOME%\lib\jetty-security-9.4.20.v20190813.jar;%APP_HOME%\lib\jetty-server-9.4.20.v20190813.jar;%APP_HOME%\lib\jetty-servlet-9.4.20.v20190813.jar;%APP_HOME%\lib\jetty-servlets-9.4.20.v20190813.jar;%APP_HOME%\lib\jetty-util-9.4.20.v20190813.jar;%APP_HOME%\lib\jetty-webapp-9.4.20.v20190813.jar;%APP_HOME%\lib\jetty-xml-9.4.20.v20190813.jar;%APP_HOME%\lib\jfreechart-1.0.13.jar;%APP_HOME%\lib\jl1.0.jar;%APP_HOME%\lib\jogg-0.0.7.jar;%APP_HOME%\lib\jorbis-0.0.15.jar;%APP_HOME%\lib\jspeex0.9.7.jar;%APP_HOME%\lib\log4j-api-2.11.1.jar;%APP_HOME%\lib\log4j-core-2.11.1.jar;%APP_HOME%\lib\log4j-slf4j18-impl-2.11.1.jar;%APP_HOME%\lib\mail-1.4.7.jar;%APP_HOME%\lib\mchange-commons-java-0.2.11.jar;%APP_HOME%\lib\mp3spi1.9.4.jar;%APP_HOME%\lib\mysql-connector-java-8.0.17.jar;%APP_HOME%\lib\QResources.jar;%APP_HOME%\lib\reflections-0.9.11.jar;%APP_HOME%\lib\RXTXcomm.jar;%APP_HOME%\lib\RXTXLibrary.jar;%APP_HOME%\lib\sli4j-slf4j-log4j-2.0.jar;%APP_HOME%\lib\swing-worker-1.1.jar;%APP_HOME%\lib\tritonus_share.jar;%APP_HOME%\lib\vorbisspi1.0.2.jar;%APP_HOME%\lib\zip4j-1.3.2.jar;%APP_HOME%\lib\Java-WebSocket-1.5.1.jar;%APP_HOME%\lib\jersey-container-servlet-core-2.27.jar;%APP_HOME%\lib\jersey-container-jetty-http-2.27.jar;%APP_HOME%\lib\jersey-server-2.27.jar;%APP_HOME%\lib\jersey-media-json-jackson-2.27.jar;%APP_HOME%\lib\jersey-hk2-2.27.jar;%APP_HOME%\lib\okhttp-3.13.1.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\jersey-client-2.27.jar;%APP_HOME%\lib\jersey-media-jaxb-2.27.jar;%APP_HOME%\lib\jersey-common-2.27.jar;%APP_HOME%\lib\jersey-entity-filtering-2.27.jar;%APP_HOME%\lib\javax.ws.rs-api-2.1.jar;%APP_HOME%\lib\hk2-locator-2.5.0-b42.jar;%APP_HOME%\lib\hk2-api-2.5.0-b42.jar;%APP_HOME%\lib\hk2-utils-2.5.0-b42.jar;%APP_HOME%\lib\javax.annotation-api-1.2.jar;%APP_HOME%\lib\javax.inject-2.5.0-b42.jar;%APP_HOME%\lib\validation-api-1.1.0.Final.jar;%APP_HOME%\lib\jetty-server-9.4.7.v20170914.jar;%APP_HOME%\lib\jetty-http-9.4.7.v20170914.jar;%APP_HOME%\lib\jetty-io-9.4.7.v20170914.jar;%APP_HOME%\lib\jetty-util-9.4.7.v20170914.jar;%APP_HOME%\lib\jetty-continuation-9.4.7.v20170914.jar;%APP_HOME%\lib\jackson-module-jaxb-annotations-2.8.10.jar;%APP_HOME%\lib\jackson-databind-2.8.10.jar;%APP_HOME%\lib\jackson-annotations-2.8.10.jar;%APP_HOME%\lib\okio-1.17.2.jar;%APP_HOME%\lib\osgi-resource-locator-1.0.1.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\jackson-core-2.8.10.jar;%APP_HOME%\lib\aopalliance-repackaged-2.5.0-b42.jar;%APP_HOME%\lib\javassist-3.22.0-CR2.jar;%APP_HOME%\lib\javax.inject-1.jar


@rem Execute WebsystemsPlugin
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %WEBSYSTEMS_PLUGIN_OPTS%  -classpath "%CLASSPATH%" websystems.ServerStart %*

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable WEBSYSTEMS_PLUGIN_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%WEBSYSTEMS_PLUGIN_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
