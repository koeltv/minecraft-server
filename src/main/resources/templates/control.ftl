<#-- @ftlvariable name="serverOn" type="Boolean" -->
<!DOCTYPE html>
<html lang="en">
<head>
    <#include 'headers.ftl'>
    <title>Minecraft Server - Controls</title>
    <link rel="stylesheet" href="/files/css/minecraftStyle.css"/>
</head>
<body style="text-align: center; font-family: sans-serif">
    <#include 'nav.ftl'>
    <br>
    <div>
        <ul id="events">
        </ul>
        <script src="/files/sse.js"></script>
    </div>

    <div class="header">
        <form action="/minecraft/command" method="post">
            <label>
                <input type="text" name="command" placeholder="/command" value="/"/><input type="submit" value="Send">
            </label>
        </form>
    </div>

    <div>
        <form action="/minecraft/start" method="post" class="inlined">
            <input type="submit" value="Start" class="animated-button" <#if serverOn>disabled</#if>>
        </form>
        <form action="/minecraft/stop" method="post" class="inlined">
            <input type="submit" value="Stop" class="animated-button" <#if !serverOn>disabled</#if>>
        </form>
    </div>
</body>
</html>