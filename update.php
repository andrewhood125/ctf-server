<?php
// Stop the current instance
echo exec('killall java');
// Get teh new codes
echo exec('git pull');
// Fork and restart the server redirecting output to index.html
print `bash -c "(make &> index.html) &"`
