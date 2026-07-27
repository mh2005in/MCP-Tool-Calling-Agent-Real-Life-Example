@echo off
REM ============================================================
REM  mcprun.cmd — Start the Immigration MCP Server
REM  Designed for Claude Code MCP settings and Eclipse.
REM  stdout is reserved for MCP protocol; diagnostics go to stderr.
REM ============================================================

set "JAVA_HOME=C:\Program Files\Java\jdk-24.0.2"
set "MAVEN_HOME=C:\Program Files\apache-maven-3.9.16"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

if not defined MCP_API_KEY set "MCP_API_KEY="

cd /d "%~dp0"
mvn -q compile exec:java %*
