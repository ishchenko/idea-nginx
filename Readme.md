# Overview
* Configuration file syntax support, run/reload configs/stop support. Works on all three platform IDEA runs on (Windows, Linux, Mac). Works for all Jetbrains platform products (IDEA, RubyMine, PhpStorm, WebStorm, PyCharm).
* The plugin has won a Honorable Mention Award in 2009-2010 Plugin Contest! Yay! http://blogs.jetbrains.com/idea/2010/06/announcing-the-ideal-plugins-for-2009-2010
* Detailed plugin description in Russian http://habrahabr.ru/blogs/nginx/66255
* The plugin in Jetbrains plugin repo http://plugins.intellij.net/plugin/?id=4415

![nginx plugin features](http://img571.imageshack.us/img571/4932/pluginfeatures.png)

# Features

* Supports all three platforms IDEA runs on (Windows, Linux, Mac).
* Server instance configuration are application-wide. Run configuration can use any of configured servers (similar to j2ee servers support in IDEA).
* -V output is respected and --conf-path/--pid-path compile-time settings are used. --prefix option is also supported. Though some compile-time parameters combinations support may be buggy. Resulting configuration and pid paths are absolute and can be edited. Edit if -V parsing failed or else.
* Configuration file syntax is supported. Configuration files are not displayed in project view, but can be opened with ctrl+shift+n (go to file) shortcut. Neighbour configuration files can also be opened with "go to file". All files in configuration file's directory are available for "go to file" and are treated as nginx configuration files. Child directories are also scanned (1 level only), thus some default configuration locations can be opened (e.g. /etc/nginx/nginx.conf and some stuff in /etc/nginx/sites-available). I doubt if more complex logic is needed here (e.g. opening files listed in "include" directives only).
* Log files can be shown in tabs next to console.
* $variables support. Autocompletion, quick documentation lookup and highlighting are available. But no support in log_format value and no parent context checks yet.
* Autoformatting
* Opening included files with ctrl+b
* Maia version is available as "nginx Support 9" in your plugin list.
* Slightly outdated build for IDEA 8 can be found here http://plugins.intellij.net/plugin/?id=4411

# Known issues
* Sometimes nginx displays wrong line number on error message. This is a nginx bug.
* No support for ambiguous directive validation, e.g. `server` may reside in `http`, `mail` or `upstream` sections
