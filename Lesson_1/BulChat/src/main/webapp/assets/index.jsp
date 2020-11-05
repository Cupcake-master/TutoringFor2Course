<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta content="width=device-width, initial-scale=1" name="viewport">
    <title>Bugout chat tutorial</title>
    <style>
        body { background-color: #333; font-size: 1.5em; padding: 0em 0.25em; }
        pre { color: #fff; white-space: pre-wrap; word-wrap: break-word; text-shadow: 0 0 10px #ccc; }
        #input { border-bottom: 1px solid #ccc; background-color: #383838; padding: 0.25em; outline: 0; }
        #input::before { content: "> "; }
    </style>
    <script src="https://chr15m.github.io/bugout/bugout.min.js" type="application/javascript"></script>
    <script>
        function log(message) {
            document.getElementById("log").textContent += message + "\n";
        }
    </script>
</head>
<body>
<pre id="log"></pre>
<pre id="input" contenteditable="true"></pre>
</body>
<script>
    log("Hello world!");

    var b = Bugout("bugout-chat-tutorial");
    b.on("message", function(address, message) {
        log(address + ": " + message);
    });
    b.on("seen", function(address) { log(address + " [ seen ]"); });
    log(b.address() + " [ me ]");

    document.getElementById("input").onkeydown = function(ev) {
        if (ev.keyCode === 13) {
            if (b.lastwirecount) {
                b.send(ev.target.textContent);
                ev.target.textContent = "";
            }
            ev.preventDefault();
        }
    }

</script>
</html>